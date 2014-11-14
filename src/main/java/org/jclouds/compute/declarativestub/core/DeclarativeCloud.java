package org.jclouds.compute.declarativestub.core;

//Not sure what problem shall solve the following code:
// @Fresh({@FreshObjects(cls=Entry.class, num=1),
// @FreshObjects(cls=Entry[].class, num=1)})
// @Override
// public void setEmailAddress(String name, String email) {
// Squander.exe(this, new Class<?>[]{String.class, String.class}, new
// Object[]{name, email});
// }
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
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
@Invariant({/* All the Resources must have unique ID */
"all vmA : this.instances | all vmB : this.instances - vmA | vmA.id != vmB.id",
		"all imageA : this.images.elts | all imageB : this.images.elts - imageA | imageA.id != imageB.id",
		// "all hardwareA : this.hardwares.elts | all hardwareB : this.hardwares.elts - hardwareA | hardwareA.id != hardwareB.id",
		// "all locationA : this.locations.elts | all locationB : this.locations.elts - locationA | locationA.id != locationB.id",
		/* Null is not an option for any Resource */
		"no (null & this.instances)",
// "no (null & this.images.elts)", "no (null & this.hardwares.elts)",
// "no (null & this.locations.elts)",
/* Relevant Fields ? */
// "all H : this.hardwares.elts | H.location != null",
// "all I : this.images.elts | I.id != null && I.location != null && I.status != null",
/* Use only the locations defined for the Cloud */
// "this.images.elts.location in this.locations.elts", "this.hardwares.elts.location in this.locations.elts"
// /* EOF */
})
public class DeclarativeCloud {

	// Note that the input implementations are of type ImmutableSet so we can return them directly
	private Set<Location> locations;
	private Set<Hardware> hardwares;
	private Set<Image> images;

	public String toString() {
		return "IMAGES:" + this.getAllImages() + "\n"//
				+ "LOCATIONS:" + this.getAllLocations() + "\n"//
				+ "HARDWARES:" + this.getAllHardwares() + "\n"//
				+ "NODES:" + this.getAllNodes();
	}

	@Requires({
			/* At least one location must be defined for this cloud to be valid */
			"some _locations.elts",
			/* Null inputs are not ok */
			"_locations != null",
			"_hardwares != null ",
			"_images != null",
			/* No null elements in the input */
			"null ! in _locations.elts",
			"null ! in _hardwares.elts",
			"null ! in _images.elts",
			/* All the Resources must have a non-null ID that is unique per type of resource */
			"all locationA : _locations.elts | all locationB : _locations.elts - locationA | locationA.id != null && locationA.id != locationB.id",
			"all hardwareA : _hardwares.elts | all hardwareB : _hardwares.elts - hardwareA | hardwareA.id != null && hardwareA.id != hardwareB.id",
			"all imageA : _images.elts | all imageB : _images.elts - imageA | imageA.id != null && imageA.id != imageB.id",
			/* Images and Hardware can use only Locations defined for the Cloud */
			" _images.elts.location in _locations.elts", "_hardwares.elts.location in _locations.elts",
			/* Images must have a defined Status */
			"all imageA : _images.elts | imageA.status != null" //
	})
	@Ensures({ "no this.instances" })
	@Modifies({ "this.instances" })
	private void checkPreconditionsAndInit(Set<Image> _images, Set<Hardware> _hardwares, Set<Location> _locations) {
		Squander.exe(this, _images, _hardwares, _locations);
	}

	public DeclarativeCloud(Set<Image> _images, Set<Hardware> _hardwares, Set<Location> _locations) {
		// Check the preconditions on the input with Squander and initialize the instances SpecField
		checkPreconditionsAndInit(_images, _hardwares, _locations);
		// Assign the state objects
		this.images = _images;
		this.hardwares = _hardwares;
		this.locations = _locations;
	}

	public DeclarativeNode createNode() {
		//
		String id = allocateID();
		checkCreateNodePrecondition(id);
		return new DeclarativeNode();
	}

	@Requires({
	/* The id provided for the node must be unique */
	"newNodeID !in @old(this.instances.id)",
	/* There must be some locations, hardwares and images */
	"some this.locations.elts",
			"some this.hardwares.elts",
			"some this.images.elts",
			/* There must be a common location between images and hardwares */
			// "some ( this.images.elts.location & this.hardwares.elts.location )"
			/* There must be some image that is AVAILABLE in a Location where at least one Hardware is defined */
			// "some I : this.images.elts | ( I.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE && ( I.location in this.hardwares.elts.location ) )",
			"some L : ( this.images.elts.location & this.hardwares.elts.location ) | some I : this.images.elts | I.location == L && I.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE"

	})
	private void checkCreateNodePrecondition(String newNodeID) {
		Squander.exe(this, newNodeID);
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

	public Set<Location> getAllLocations() {
		return this.locations;
	}

	public Set<Hardware> getAllHardwares() {
		return this.hardwares;
	}

	public Set<Image> getAllImages() {
		return this.images;
	}

	// @Ensures("return.elts == this.instances")
	// @FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	// @Modifies("return.elts")
	// public Set<DeclarativeNode> getAllNodes() {
	// return Squander.exe(this);
	// }
	public Set<DeclarativeNode> getAllNodes() {
		return new HashSet<DeclarativeNode>();
	}

	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Requires("ids.elts in this.instances.id")
	@Modifies("return.elts")
	@Ensures({ "return.elts in this.instances && ids.elts == return.elts.id" })
	public Set<DeclarativeNode> getNodes(Set<String> ids) {
		return Squander.exe(this, ids);
	}

	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	// @Requires({
	// /* The id provided for the node must be unique */
	// "newNodeID !in @old(this.instances.id)",
	// /* There must be some locations, hardwares and images */
	// "some this.locations.elts", "some this.hardwares.elts",
	// /* We need to considere locations of images and hardwares */
	// "some this.images.elts.location", "some this.hardwares.elts.location",
	// //
	// "some ( this.images.elts.location & this.hardwares.elts.location )" // This does not work !
	/* There must be some image that is AVAILABLE in a Location where at least one Hardware is defined */
	// "some I : this.images.elts | ( I.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE && ( I.location in this.hardwares.elts.location ) )",
	// "some L : ( this.images.elts.location & this.hardwares.elts.location ) | some I : this.images.elts | I.location == L && I.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE"
	//
	// })
	/*
	 * TODO Check Precondition first !
	 */
	// This method post conditions are kinda Krappy, they allow the internal objects in this class to be modified
	// despite they are not declared as modifiable ?!
	@Ensures({
			/* Deploy the new node with given ID and RUNNING state */
			"this.instances = @old(this.instances) + return",
			/* Keep the rest as it is ! */
			"this.images = @old(this.images)",
			"this.locations = @old( this.locations)",
			"this.hardwares = @old( this.hardwares)",
			/* Maintain ID */
			"return.id = newNodeID",
			/* Start VM as RUNNING */
			"return.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
			/*
			 * Node and Image connection: we do not care, but the image for the new node must be one of the available
			 * one !!
			 */
			//
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
			"return.id", // Maybe somethign like DeclarativeNode.status [{ n : DeclarativeNode | n.status == null }] ?
			"return.status",//
			"return.image",//
			"return.location",//
			"return.hardware",//
	// "return.image.status",//
	// "return.image.location",//
	// "return.hardware.location"//
	})
	// @Options(ensureAllInts = true, solveAll = true, bitwidth = 5)
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

	@Requires({ "#( _id @& this.images.elts.id) > 0" })
	@Ensures({ "return = { I : this.images.elts | I.id == return.id && I.id == _id}" })
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

}
