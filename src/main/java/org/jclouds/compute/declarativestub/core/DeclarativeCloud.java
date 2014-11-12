package org.jclouds.compute.declarativestub.core;

//Not sure what problem shall solve the following code:
// @Fresh({@FreshObjects(cls=Entry.class, num=1),
// @FreshObjects(cls=Entry[].class, num=1)})
// @Override
// public void setEmailAddress(String name, String email) {
// Squander.exe(this, new Class<?>[]{String.class, String.class}, new
// Object[]{name, email});
// }
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Try to mix imperative and declarative programming to define a basic stub of the ComputeService of a cloud
 * 
 * @author alessiogambi
 *
 */
@SpecField({
/*
 * All the Instances deployed in the cloud. This form the Spec field of the ComputeService. The other elements are
 * concrete instances that will/must not change, in a sense they form the universe for the cloud !
 */
"instances : set DeclarativeNode",
/*
 * All the Disk Images defined for the cloud
 */
// "images : set org.jclouds.compute.domain.Image",
/*
 * All the Hardware configuration (a.k.a Flavors) defined for the cloud
 */
// "hardwares : set org.jclouds.compute.domain.Hardware",
/*
 * All the Locations defined for the cloud
 */
// "locations : set org.jclouds.domain.Location"
})
// @Invariant({/* All the Resources must have unique ID */
// "all vmA : this.instances | all vmB : this.instances - vmA | vmA.id != vmB.id",
// "all imageA : this.images.elts | all imageB : this.images.elts - imageA | imageA.id != imageB.id",
// "all hardwareA : this.hardwares.elts | all hardwareB : this.hardwares.elts - hardwareA | hardwareA.id != hardwareB.id",
// "all locationA : this.locations.elts | all locationB : this.locations.elts - locationA | locationA.id != locationB.id",
// /* Null is not an option for any Resource */
// "no (null & this.instances)",
// "no (null & this.images.elts)",
// "no (null & this.hardwares.elts)",
// "no (null & this.locations.elts)",
// /* This is only to avoid clsSpec on NodeMetadataStatus ! */
// "all vm : this.running | (vm in this.instances && vm.status = org.jclouds.compute.domain.NodeMetadataStatus.RUNNING)",
// //
// "all ai : this.av_images| (ai in this.images.elts && ai.status = org.jclouds.compute.domain.ImageStatus.AVAILABLE)",
// /* Use only the locations defined for the Cloud */
// "this.images.elts.location in this.locations.elts", "this.hardwares.elts.location in this.locations.elts"
// /* EOF */
// })
public class DeclarativeCloud {

	private Set<Image> images;
	private Set<Hardware> hardwares;
	private Set<Location> locations;

	public String toString() {
		return "IMAGES:" + this.getAllImages() + "\n"//
				+ "LOCATIONS:" + this.getAllLocations() + "\n"//
				+ "HARDWARES:" + this.getAllHardwares() + "\n"//
				+ "NODES:" + this.getAllNodes();
	}

	@Requires({
			"some _images.elts",
			"some _locations.elts",
			"some _hardwares.elts",
			// Force the invariants on the input data otherwise this creates problems !!
			/* All the Resources must have unique ID */
			"all imageA : _images.elts | all imageB : _images.elts - imageA | imageA.id != null && imageA.id != imageB.id",
			"all hardwareA : _hardwares.elts | all hardwareB : _hardwares.elts - hardwareA | hardwareA.id != null && hardwareA.id != hardwareB.id",
			"all locationA : _locations.elts | all locationB : _locations.elts - locationA | locationA.id != null && locationA.id != locationB.id",
	// /* Null is not an option for any Resource */
	// "no (null & _images)", "no (null & _hardwares)", "no (null & _locations)", "no (null & _images.elts)",
	// "no (null & _hardwares.elts)", "no (null & _locations.elts)",
	// /* Use only the locations defined for the Cloud */
	// " _images.elts.location in _locations.elts", "_hardwares.elts.location in _locations.elts",//
	// //
	// "all imageA : _images.elts | imageA.status != null" //
	})
	// TODO: This is not entirely right to me. I want just to check some preconditions on input data before doing
	// anything, maybe I need a static method ?
	@Ensures({ "no this.instances",//
			"some this.images.elts", //
			"some this.hardwares.elts", //
			"some this.locations.elts", //
	})
	@Modifies({ "this.instances",//
			// We are changing anyway the status of this object even if
			"this.images", //
			"this.hardwares", //
			"this.locations", //
	})
	private void checkPreconditionsAndInit(Set<Image> _images, Set<Hardware> _hardwares, Set<Location> _locations) {
		Squander.exe(this, _images, _hardwares, _locations);
	}

	public DeclarativeCloud(Set<Image> _images, Set<Hardware> _hardwares, Set<Location> _locations) {
		// Check the preconditions
		checkPreconditionsAndInit(_images, _hardwares, _locations);
		// Initialize the state
		this.images = _images;
		this.hardwares = _hardwares;
		this.locations = _locations;
	}

	/**
	 * Generate a random, but sound node
	 * 
	 * @return
	 */
	/* NON ANNOTATED VERSION */
	public DeclarativeNode createNode() {
		return createNode(allocateID());
	}

	/*
	 * Since it is not possible to create random strings we use a generative naive approach
	 */
	final static private AtomicInteger currentId = new AtomicInteger();

	public static String allocateID() {
		return "" + currentId.incrementAndGet();
	}

	@Ensures({ "no this.instances", "no this.images.elts", "no this.hardwares.elts", "no this.locations.elts" })
	@Modifies({ "this.instances", "this.images.elts", "this.hardwares.elts", "this.locations.elts" })
	private void init() {
		Squander.exe(this);
	}

	@Ensures({ "return.elts == this.images.elts", "return.elts.status == this.images.elts.status",
			"return.elts.location == this.images.elts.location" })
	@FreshObjects(cls = Set.class, typeParams = { Image.class }, num = 1)
	@Modifies("return.elts")
	public Set<Image> getAllImages() {
		return Squander.exe(this);
	}

	@Ensures("return.elts == this.hardwares.elts")
	@FreshObjects(cls = Set.class, typeParams = { Hardware.class }, num = 1)
	@Modifies("return.elts")
	public Set<Hardware> getAllHardwares() {
		return Squander.exe(this);
	}

	@Ensures("return.elts == this.instances")
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeNode> getAllNodes() {
		return Squander.exe(this);
	}

	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Requires("ids.elts in this.instances.id")
	@Modifies("return.elts")
	@Ensures({ "return.elts in this.instances && ids.elts == return.elts.id" })
	public Set<DeclarativeNode> getNodes(Set<String> ids) {
		return Squander.exe(this, ids);
	}

	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Requires({ "newNodeID !in @old(this.instances.id)",
			//
			"#this.hardwares.elts > 0",
			"#this.locations.elts > 0",
			// "some image : this.images.elts | ( image.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE && image.location in this.hardwares.elts.location )"
			/*
			 * There must be at least one location with hardwares and images that are available
			 */
			"some L : ( this.images.elts.location & this.hardwares.elts.location ) | some I : this.images.elts | I.location == L && I.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE" })
	@Ensures({
			/* Deploy the new node with given ID and RUNNING state */
			"this.instances = @old(this.instances) + return",
			/* Maintain ID */
			"return.id = newNodeID",
			/* Start VM as RUNNING */
			"return.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
			/*
			 * Node and Image connection: we do not care, but the image for the new node must be one of the available
			 * one !!
			 */
			"one i : this.images.elts | ( i.status = org.jclouds.compute.domain.ImageStatus.AVAILABLE && i.location in this.hardwares.elts.location && return.image = i && return.image.location == i.location )",
			/*
			 * Assign the location of the node to be the one of the image, such that is the location of the image !
			 * (cannot do it in 3 steps ! image in images, location in locations, image.location = location )
			 */
			// "return.location = return.image.location && return.location = return.hardware.location",
			/*
			 * Pick one Hardware configuration.
			 */
			"return.hardware in this.hardwares.elts", })
	@Modifies({ "this.instances", //
			"return.id", //
			"return.image",//
			"return.image.location",//
			"return.image.status",//
			"return.status",//
			"return.location",//
			"return.hardware",//
			"return.hardware.location"//
	})
	@Options(ensureAllInts = true, solveAll = true, bitwidth = 5)
	public DeclarativeNode createNode(String newNodeID) {
		return Squander.exe(this, newNodeID);
	}

	/**
	 * Version with all the required parameters
	 * 
	 * @param nodeId Must be Unique for this cloud : nodeID !in @old(this.instances.id)
	 * @param group TODO not sure about specs.
	 * @param name TODO not sure about specs.
	 * @param location Must be a valid location: location in this.locations.elts
	 * @param hardware Must be a valid hardware: hardware in this.hardware Must share a location with image:
	 *            image.location == hardware.location
	 * @param image Must be a valid image: image in this.images.elts Must share a location with hardware
	 *            hardware.location == image.location
	 * 
	 * @return A valid new node with all the parameters set !
	 */
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Requires({
	/*
	 * Basic requirements for the Cloud to start a node ?
	 */
	"#this.hardwares.elts > 0", "#this.locations.elts > 0", "#this.images.elts > 0",
	/* There must be at least one valid location shared among hardwares and images */
	"#( this.locations.elts & this.hardwares.elts.location & this.images.elts.location ) > 0",
	/*
	 * Requirements on Parameters
	 */
	/* nodeId must be unique */
	"nodeId !in @old(this.instances.id) && nodeId != null",
	/* Valid Location. Not sure. Can be location.id in this.locations.elts.id ? */
	"_location in this.locations.elts && _location != null && _location.id != null",
	/* Valid Image. Not sure. Can be image.id in this.images.elts.id */
	"_image in this.images.elts && _image != null && _image.id != null && _image.location in this.locations.elts",
	// /* The Image must be available */
	// "_image.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE",
	// /* Valid Hardware. // Not sure. Can be hardware.id in this.hardwares.elts.id ? Can be null ?! */
	// "_hardware in this.hardwares.elts && _hardware != null && _hardware.id != null && _hardware.location in this.locations.elts",
	// /* Image and Hardware must be defined in the same Location, the location must be _location */
	// "_image.location == _hardware.location && _location == _image.location && _location == _hardware.location"
	/**/
	})
	/*
	 * 
	 */
	@Ensures({
	/* Deploy the new node with given ID and RUNNING status */
	"this.instances = @old(this.instances) + return",
			"return.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
			/* Maintain ID */
			"return.id = nodeId",
			/* Maintain name and group */
			"return.group = _group && return.name = _name",
			/* Maintain Location */
			"return.location = _location",
			/* Maintain Hardware. TODO If null take the default one */
			"return.hardware = _hardware",
			/* Maintain Image */
			"return.image = _image"
	/**/
	})
	/*
	 * 
	 */
	@Modifies({
	/* Update the Deployed VM relation */
	"this.instances",
	/* Update all the attributes of the return object */
	/* TODO Must the second level relations be modifiable ? return.location.id ?! */
	"return.id", "return.group", "return.name", "return.status", "return.image", "return.location", "return.hardware", })
	public DeclarativeNode createNode(//
			String nodeId,//
			String _group,//
			String _name,//
			// Do we really need location ?
			Location _location, //
			Hardware _hardware, //
			Image _image) {
		return Squander.exe(this, nodeId, _group, _name, _location, _hardware, _image);
	}

	@Requires({
			// At least one VM
			"some this.instances",
			// The node to stop must be in the running nodes
			"_id in this.instances.id" })
	@Ensures({ "_id !in this.instances.id && #this.instances = #@old(this.instances) - 1" })
	@Modifies({ "this.instances", })
	public void removeNode(String _id) {
		Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.instances",
			// The node must be in the running nodes
			"return.id in this.instances.id" })
	// Return a COPY of the NODE- maybe this one is better to do imperatively ?!
	// I think this is mandatory since we are creating a new instance, but it is
	// not really comfortable, isn't it ?
	@Ensures("one vm : this.instances | vm.id=_id && return.id = vm.id && return.status=vm.status && return.image=vm.image && return.location=vm.location && return.hardware = vm.hardware")
	@Modifies({ "return.id", "return.status", "return.image", "return.location", "return.hardware" })
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(String _id) {
		return Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"#this.images.elts > 0",
			// The node must be in the running nodes
			"_id in this.images.elts.id" })
	// Return a COPY of the NODE- maybe this one is better to do imperatively ?!
	@Ensures("one image : this.images.elts | image.id=_id && return.id = image.id")
	public Image getImage(String _id) {
		return Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.instances",
			// The node must exist
			"one vm : this.instances | vm.id == _id" })
	@Ensures({ "one vm : this.instances | ( vm.id == _id && vm.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING)" })
	@Modifies({ "DeclarativeNode.status [{vm : this.instances | vm.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void startNode(String _id) {
		// TODO: by changing the state we change the running/stopped relation,
		// but we do not declare them as "mofiable"
		Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.instances",
			// The node must exist
			"one vm : this.instances | vm.id == _id" })
	@Ensures({ "one vm : this.instances | ( vm.id == _id && vm.status =  org.jclouds.compute.domain.NodeMetadataStatus.SUSPENDED )" })
	@Modifies({ "DeclarativeNode.status [{vm : this.instances | vm.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void suspendNode(String _id) {
		Squander.exe(this, _id);
	}

	@Ensures("return.elts == this.locations.elts")
	@FreshObjects(cls = Set.class, typeParams = { Location.class }, num = 1)
	@Modifies("return.elts")
	public Set<Location> getAllLocations() {
		return Squander.exe(this);
	}

}
