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
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * This abstract class contains some of the specifications of a generic cloud
 * 
 * @author alessiogambi
 *
 */
// FIXME: if we do not provide any spec that uses NodeMetadata, this will result
// in a clsSpec == null !
@SpecField({
/*
 * All the VM deployed in the cloud
 */
"vms : set DeclarativeNode",
/*
 * Running VM
 */
"running : set DeclarativeNode from this.vms",
/*
 * Disk Images
 */
"images : set org.jclouds.compute.domain.Image",
/*
 * Available Disk Images
 */
"av_images : set org.jclouds.compute.domain.Image from this.images",
/*
 * Image hardwares, a.k.a., Hardware configuration
 */
"hardwares : set org.jclouds.compute.domain.Hardware",
/*
 * Location.
 */
"locations : set org.jclouds.domain.Location" })
@Invariant({/* All the Resources must have unique ID */
		"all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
		"all imageA : this.images | all imageB : this.images - imageA | imageA.id != imageB.id",
		"all hardwareA : this.hardwares | all hardwareB : this.hardwares - hardwareA | hardwareA.id != hardwareB.id",
		"all locationA : this.locations | all locationB : this.locations - locationA | locationA.id != locationB.id",
		/* Null is not an option for any Resource */
		"no (null & this.vms)",
		"no (null & this.images)",
		"no (null & this.hardwares)",
		"no (null & this.locations)",
		/* This is only to avoid clsSpec on NodeMetadataStatus ! */
		"all vm : this.running | (vm in this.vms && vm.status = org.jclouds.compute.domain.NodeMetadataStatus.RUNNING)",
		//
		"all ai : this.av_images| (ai in this.images && ai.status = org.jclouds.compute.domain.ImageStatus.AVAILABLE)",
		/* Use only the locations defined for the Cloud */
		"this.images.location in this.locations", "this.hardwares.location in this.locations"
/* EOF */
})
public class DeclarativeCloud {

	public String toString() {
		return "IMAGES:" + this.getAllImages() + "\n"//
				+ "LOCATIONS:" + this.getAllLocations() + "\n"//
				+ "HARDWARES:" + this.getAllHardwares() + "\n"//
				+ "NODES:" + this.getAllNodes();
	}

	public DeclarativeCloud(Set<Image> images, Set<Hardware> hardwares, Set<Location> locations) {
		init(images, hardwares, locations);
	}

	public DeclarativeCloud() {
		init();
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

	@Ensures({ "no this.vms", "no this.images", "no this.hardwares", "no this.locations" })
	@Modifies({ "this.vms", "this.images", "this.hardwares", "this.locations" })
	private void init() {
		Squander.exe(this);
	}

	/**
	 * This is a strict version that requires all the constraints about location and id to be satisfied. Alternatively
	 * one can simply "ask" for them to be provided in the Ensures... The problem here is, can we update the location/id
	 * stuff really ??
	 * 
	 */
	@Requires({
			// Force the invariants on the input data otherwise this creates problems !!
			/* All the Resources must have unique ID */
			"all imageA : _images.elts | all imageB : _images.elts - imageA | imageA.id != imageB.id",
			"all hardwareA : _hardwares.elts | all hardwareB : _hardwares.elts - hardwareA | hardwareA.id != hardwareB.id",
			"all locationA : _locations.elts | all locationB : _locations.elts - locationA | locationA.id != locationB.id",
			/* Null is not an option for any Resource */
			"no (null & _images)", "no (null & _hardwares)", "no (null & _locations)", "no (null & _images.elts)",
			"no (null & _hardwares.elts)", "no (null & _locations.elts)",
			/* Use only the locations defined for the Cloud */
			" _images.elts.location in _locations.elts", "_hardwares.elts.location in _locations.elts",//
			//
			"all imageA : _images.elts | imageA.status != null"

	/* EOF */
	})
	/*
	 * 
	 */
	@Ensures({
	/* No virtual machines are running */
	"no this.vms", //
			/*
			 * Maintain the original relations. Not that we had to remove the invariants from the spec to make this
			 * working !
			 */
			"this.images = _images.elts", //
			"this.images.status = _images.elts.status", //
			"this.images.location = _images.elts.location",
			//
			"this.hardwares == _hardwares.elts",//
			"this.hardwares.location == _hardwares.elts.location",//
			"this.locations == _locations.elts",//
	/* EOF */})
	@Modifies({ "this.vms", //
			"this.images", "this.hardwares", "this.locations",
	/* EOF */
	})
	@Options(ensureAllInts = true, solveAll = true, bitwidth = 10)
	private void init(Set<Image> _images, Set<Hardware> _hardwares, Set<Location> _locations) {
		Squander.exe(this, _images, _hardwares, _locations);
	}

	@Ensures("return.elts == this.images")
	@FreshObjects(cls = Set.class, typeParams = { Image.class }, num = 1)
	@Modifies("return.elts")
	public Set<Image> getAllImages() {
		return Squander.exe(this);
	}

	@Ensures("return.elts == this.hardwares")
	@FreshObjects(cls = Set.class, typeParams = { Hardware.class }, num = 1)
	@Modifies("return.elts")
	public Set<Hardware> getAllHardwares() {
		return Squander.exe(this);
	}

	@Ensures("return.elts == this.vms")
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeNode> getAllNodes() {
		return Squander.exe(this);
	}

	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Requires("ids.elts in this.vms.id")
	@Modifies("return.elts")
	@Ensures({ "return.elts in this.vms && ids.elts == return.elts.id" })
	public Set<DeclarativeNode> getNodes(Set<String> ids) {
		return Squander.exe(this, ids);
	}

	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Requires({ "newNodeID !in @old(this.vms.id)",
			//
			"#this.hardwares > 0",
			"#this.locations > 0",
			// "some image : this.images | ( image.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE && image.location in this.hardwares.location )"
			/*
			 * There must be at least one location with hardwares and images that are available
			 */
			"some L : ( this.images.location & this.hardwares.location ) | some I : this.images | I.location == L && I.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE" })
	@Ensures({
			/* Deploy the new node with given ID and RUNNING state */
			"this.vms = @old(this.vms) + return",
			/* Maintain ID */
			"return.id = newNodeID",
			/* Start VM as RUNNING */
			"return.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
			/*
			 * Node and Image connection: we do not care, but the image for the new node must be one of the available
			 * one !!
			 */
			"one i : this.images | ( i.status = org.jclouds.compute.domain.ImageStatus.AVAILABLE && i.location in this.hardwares.location && return.image = i && return.image.location == i.location )",
			/*
			 * Assign the location of the node to be the one of the image, such that is the location of the image !
			 * (cannot do it in 3 steps ! image in images, location in locations, image.location = location )
			 */
			// "return.location = return.image.location && return.location = return.hardware.location",
			/*
			 * Pick one Hardware configuration.
			 */
			"return.hardware in this.hardwares", })
	@Modifies({ "this.vms", //
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
	 * @param nodeId Must be Unique for this cloud : nodeID !in @old(this.vms.id)
	 * @param group TODO not sure about specs.
	 * @param name TODO not sure about specs.
	 * @param location Must be a valid location: location in this.locations
	 * @param hardware Must be a valid hardware: hardware in this.hardware Must share a location with image:
	 *            image.location == hardware.location
	 * @param image Must be a valid image: image in this.images Must share a location with hardware hardware.location ==
	 *            image.location
	 * 
	 * @return A valid new node with all the parameters set !
	 */
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Requires({
	/*
	 * Basic requirements for the Cloud to start a node ?
	 */
	"#this.hardwares > 0", "#this.locations > 0", "#this.images > 0",
	/* There must be at least one valid location shared among hardwares and images */
	"#( this.locations & this.hardwares.location & this.images.location ) > 0",
	/*
	 * Requirements on Parameters
	 */
	/* nodeId must be unique */
	"nodeId !in @old(this.vms.id) && nodeId != null",
	/* Valid Location. Not sure. Can be location.id in this.locations.id ? */
	"_location in this.locations && _location != null && _location.id != null",
	/* Valid Image. Not sure. Can be image.id in this.images.id */
	"_image in this.images && _image != null && _image.id != null && _image.location in this.locations",
	// /* The Image must be available */
	// "_image.status == org.jclouds.compute.domain.ImageStatus.AVAILABLE",
	// /* Valid Hardware. // Not sure. Can be hardware.id in this.hardwares.id ? Can be null ?! */
	// "_hardware in this.hardwares && _hardware != null && _hardware.id != null && _hardware.location in this.locations",
	// /* Image and Hardware must be defined in the same Location, the location must be _location */
	// "_image.location == _hardware.location && _location == _image.location && _location == _hardware.location"
	/**/
	})
	/*
	 * 
	 */
	@Ensures({
	/* Deploy the new node with given ID and RUNNING status */
	"this.vms = @old(this.vms) + return", "return.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
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
	"this.vms",
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
			"some this.vms",
			// The node to stop must be in the running nodes
			"_id in this.vms.id" })
	@Ensures({ "_id !in this.vms.id && #this.vms = #@old(this.vms) - 1" })
	@Modifies({ "this.vms", })
	public void removeNode(String _id) {
		Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.vms",
			// The node must be in the running nodes
			"return.id in this.vms.id" })
	// Return a COPY of the NODE- maybe this one is better to do imperatively ?!
	// I think this is mandatory since we are creating a new instance, but it is
	// not really comfortable, isn't it ?
	@Ensures("one vm : this.vms | vm.id=_id && return.id = vm.id && return.status=vm.status && return.image=vm.image && return.location=vm.location && return.hardware = vm.hardware")
	@Modifies({ "return.id", "return.status", "return.image", "return.location", "return.hardware" })
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(String _id) {
		return Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"#this.images > 0",
			// The node must be in the running nodes
			"_id in this.images.id" })
	// Return a COPY of the NODE- maybe this one is better to do imperatively ?!
	@Ensures("one image : this.images | image.id=_id && return.id = image.id")
	public Image getImage(String _id) {
		return Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.vms",
			// The node must exist
			"one vm : this.vms | vm.id == _id" })
	@Ensures({ "one vm : this.vms | ( vm.id == _id && vm.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING)" })
	@Modifies({ "DeclarativeNode.status [{vm : this.vms | vm.id == _id}]" })
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
			"some this.vms",
			// The node must exist
			"one vm : this.vms | vm.id == _id" })
	@Ensures({ "one vm : this.vms | ( vm.id == _id && vm.status =  org.jclouds.compute.domain.NodeMetadataStatus.SUSPENDED )" })
	@Modifies({ "DeclarativeNode.status [{vm : this.vms | vm.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void suspendNode(String _id) {
		Squander.exe(this, _id);
	}

	@Ensures("return.elts == this.locations")
	@FreshObjects(cls = Set.class, typeParams = { Location.class }, num = 1)
	@Modifies("return.elts")
	public Set<Location> getAllLocations() {
		return Squander.exe(this);
	}

}
