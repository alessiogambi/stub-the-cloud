package org.jclouds.compute.declarativestub.core;

import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.SpecField;

/**
 * This abstract class contains some of the specifications of a generic cloud
 * 
 * @author alessiogambi
 *
 */
@SpecField({
/*
 * All the instance deployed in the cloud
 */
"instances : set DeclarativeNode",
/*
 * Disk Images
 */
"images : set DeclarativeImage",
/*
 * Image hardwares, a.k.a., Hardware configuration
 */
"hardwares : set DeclarativeHardware",
/*
 * Location.
 */
"locations : set DeclarativeLocation" })
@Invariant({
/* All the Resources must have unique ID */
"all instanceA : this.instances | all instanceB : this.instances - instanceA | instanceA.id != instanceB.id",
		"all imageA : this.images | all imageB : this.images - imageA | imageA.id != imageB.id",
		"all hardwareA : this.hardwares | all hardwareB : this.hardwares - hardwareA | hardwareA.id != hardwareB.id",
		"all locationA : this.locations | all locationB : this.locations - locationA | locationA.id != locationB.id",
		/* Null is not an option for any Resource */
		"this.instances != null", "this.locations!=null", "this.images != null", "this.hardwares!=null",
		/* Null is not an option for any Resource */
		"no (null & this.instances)", "no (null & this.images)", "no (null & this.hardwares)",
		"no (null & this.locations)",

		/* Use only the locations defined for the Cloud */
		"this.images.location in this.locations", "this.hardwares.location in this.locations"
/* EOF */
})
public interface DeclarativeCloud {

	// /*
	// * Since it is not possible to create random strings we use a generative naive approach. The test assume that this
	// * atomic integer will be reset everytime we create an instance of cloud
	// */
	// public static class FactoryId {
	// final static private AtomicInteger currentId = new AtomicInteger(0);
	//
	// public abstract static String allocateID() {
	// return "" + currentId.incrementAndGet();
	// }
	//
	// // Only for Testing
	// // public static void resetID() {
	// // currentId.set(0);
	// // }
	// }

	public DeclarativeNode createNode();

	@Ensures("return != null && return.elts == this.locations")
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeLocation.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeLocation> getAllLocations();

	@Ensures({ "return.elts == this.images", })
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeImage.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeImage> getAllImages();

	@Ensures("return.elts == this.hardwares")
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeHardware.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeHardware> getAllHardwares();

	@Ensures("return.elts == this.instances")
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeNode> getAllNodes();

	@Requires("ids.elts in this.instances.id")
	@Modifies("return.elts")
	@Ensures({ "return.elts = { instance : this.instances | instance.id in ids.elts }" })
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	public Set<DeclarativeNode> getNodes(Set<String> ids);

	@Requires({
	/* The Node ID must be unique */
	"newNodeID !in this.instances.id",
	/*
	 * There must be at least one location where both hardwares and images that are available TODO && I.status ==
	 * DeclarativeImageStatus.AVAILABLE
	 */
	"some L : ( this.images.location & this.hardwares.location ) | some I : this.images | I.location == L" })
	@Ensures({
	/*
	 * Deploy the new node with given ID and RUNNING state
	 */
	"this.instances = @old(this.instances) + return && #this.instances = @old(#this.instances) + 1",
	/**/
	"return.status = org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
	/*
	 * Keep the given ID
	 */
	"return.id = newNodeID",
	/*
	 * The node location must be valid
	 */
	"return.location = return.image.location && return.location = return.hardware.location",
	/*
	 * Pick one available Hardware configuration.
	 */
	"return.hardware in this.hardwares",
	/*
	 * Pick one available Image
	 */
	"return.image in this.images" })
	// "one i : this.images | ( i.status = DeclarativeImageStatus.AVAILABLE && i.location in this.hardwares.location && return.image = i && return.image.location == i.location )",
	@Modifies({
	/* */
	"this.instances",
	/**/
	"return.id", "return.image", "return.location", "return.hardware", "return.status"//
	})
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode createNode(String newNodeID);

	@Requires({
	/* The Node ID must be unique */
	"newNodeID !in this.instances.id",
	/* Parameters are valid. TODO Note the fresh on get ! */
	"_image in this.images", "_location in this.locations", "_hardware in this.hardwares",
	/* Location constraints are respected */
	"_image.location = _hardware.location", })
	@Ensures({
	/*
	 * Deploy the new node with given ID and TODO RUNNING state
	 */
	"this.instances = @old(this.instances) + return && #this.instances = @old(#this.instances) + 1",
	/**/
	"return.status = org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
	/*
	 * Keep the given parameters
	 */
	"return.id = newNodeID", "return.name = _name", "return.group = _group", "return.location = _location",
			"return.hardware = _hardware", "return.image = _image" })
	@Modifies({
	/* */
	"this.instances",
	/**/
	"return.id", "return.name", "return.group", "return.location", "return.hardware", "return.image", "return.status"//
	})
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode createNode(String newNodeID, String _name, String _group, DeclarativeLocation _location,
			DeclarativeHardware _hardware, DeclarativeImage _image);

	/**
	 * This is a strict version that requires the node to be there, a simpler version can use quantifier and implication
	 * without any precondition!
	 * 
	 * @param _id
	 */
	@Requires({
	/*
	 * At least one instance
	 */
	"some this.instances",
	/*
	 * The id must correspond to a deployed instance
	 */
	"_id in this.instances.id" })
	@Ensures({
	/* This work in spite of the invariant on id */
	"_id !in this.instances.id",
	/* This works only in spite of the precondition */
	"#this.instances = #@old(this.instances) - 1" })
	@Modifies({ "this.instances", })
	public void removeNode(String _id);

	/**
	 * 
	 * @param _id
	 * @return The node or null if it is not there
	 */
	// Preconditions are not required anymore, but they were somehow verified even if the cloud state should have
	// falsified them !
	// @Requires({
	/*
	 * At least one instances are deployed
	 */
	// "some this.instances",
	/*
	 * The id must be one of deployed instances
	 */
	// "return.id in this.instances.id" -> not required
	// })
	/*
	 * Return a Copy or the Object ?! Ideally it should be a copy to avoid side effects on the cloud, however it should
	 * ne a deep copy !! For the moment just return THE node
	 */
	@Ensures({
	/* Matching conditions */
	// one instance : this.instances | instance.id=_id && ( return.id = instance.id && return.image=instance.image &&
	// return.location=instance.location && return.hardware = instance.hardware )",
	"return - null = { instance : this.instances | instance.id=_id }",
	/* Return the new instance. Note that this is shallow for the moment */
	// "return ! in this.instances"
	})
	// @Modifies({ "return.id", "return.image", "return.location", "return.hardware" })
	// @FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(String _id);

	// @Requires({
	// // At least one instance
	// "some this.images",
	// // The node must be in the running nodes
	// "_id in this.images.id" })
	/* Not sure we need to force a fresh instance of Location as well */
	@Ensures({ "return - null = {image : this.images | image.id=_id }",
	// "return ! in this.images && return.location ! in this.locations"
	})
	// @Modifies({
	// /*
	// * If we cannot modify return then one of the available instances will be returned and no the fresh instance
	// */
	// "return.id", "return.location",
	// /* I am not 100% sure this will modify only the fresh object ! */
	// "DeclarativeLocation.id [{ dl : DeclarativeLocation | dl.id == null }]" })
	// @Fresh({ @FreshObjects(cls = DeclarativeImage.class, num = 1),
	// @FreshObjects(cls = DeclarativeLocation.class, num = 1) })
	public DeclarativeImage getImage(String _id);

	@Requires({
			// At least one location
			"some this.locations",
			// The node must be in the running nodes
			"_id in this.locations.id" })
	@Ensures({ "one location : this.locations | location.id=_id && return.id = location.id ",
	/* Return the fresh instance */
	// "return ! in this.locations"
	})
	// @Modifies({ "return.id" })
	// @FreshObjects(cls = DeclarativeLocation.class, num = 1)
	public DeclarativeLocation getLocation(String _id);

	/**
	 * 
	 * @param _id
	 * @return The Hardware or null
	 */
	// @Requires({
	// At least one instance
	// "some this.hardwares",
	// The node must be in the running nodes
	// "_id in this.hardwares.id"
	// })
	/* Not sure we need to force a fresh instance of Location as well */
	@Ensures({ "return - null = { hardware : this.hardwares | hardware.id=_id }",
	// /* Force fresh objects */
	// "return ! in this.hardwares && return.location ! in this.locations"
	})
	// @Modifies({
	// /*
	// * If we cannot modify return then one of the available instances will be returned and no the fresh instance
	// */
	// "return.id", "return.location",
	/* I am not 100% sure this will modify only the fresh object ! */
	// "DeclarativeLocation.id [{ dl : DeclarativeLocation | dl.id == null }]" })
	// @Fresh({ @FreshObjects(cls = DeclarativeHardware.class, num = 1),
	// @FreshObjects(cls = DeclarativeLocation.class, num = 1) })
	public DeclarativeHardware getHardware(String _id);

	@Requires({
			// At least one instance
			"some this.instances",
			// The node must exist
			"one instance : this.instances | instance.id == _id" })
	@Ensures({ "one instance : this.instances | ( instance.id == _id && instance.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING)" })
	@Modifies({ "DeclarativeNode.status [{instance : this.instances | instance.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void startNode(String _id);

	@Requires({
			// At least one instance
			"some this.instances",
			// The node must exist
			"one instance : this.instances | instance.id == _id" })
	@Ensures({ "one instance : this.instances | ( instance.id == _id && instance.status =  org.jclouds.compute.domain.NodeMetadataStatus.SUSPENDED )" })
	@Modifies({ "DeclarativeNode.status [{instance : this.instances | instance.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void suspendNode(String _id);

}
