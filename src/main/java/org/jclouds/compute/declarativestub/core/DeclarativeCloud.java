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
/* All the VM deployed in the cloud */
"vms : set DeclarativeNode",
/*
 * Running VM
 */
"running : set DeclarativeNode",
/*
 * Disk Images
 */
"images : set org.jclouds.compute.domain.Image",
/*
 * Image Flavours, a.k.a., Hardware configuration
 */
"flavors : set org.jclouds.compute.domain.Hardware" })
@Invariant({//
/* All the VM must have unique ID */
		"all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
		/* Null is not an option for Virtual Machines */
		// "null !in this.vms",
		"no (null & this.vms)",
		/* Running VM */
		"this.running in this.vms",
		/* This is only to avoid clsSpec ! */
		"all vm : this.running | vm.status = org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
		/* Null is not an option for Images */
		"no (null & this.images)",
		/* Null is not an option for Hardware */
		"no (null & this.flavors)" })
public class DeclarativeCloud {

	public String toString() {
		return "IMAGES:" + this.getAllImages() + "\n"//
				+ "FLAVORS:" + this.getAllFlavors() + "\n"//
				+ "NODES:" + this.getAllNodes() + "\n";
	}

	public DeclarativeCloud(Set<Image> images, Set<Hardware> flavors) {
		init(images, flavors);
	}

	public DeclarativeCloud() {
		init();
	}

	/* NON ANNOTATED VERSION */
	public DeclarativeNode createNode() {
		return createNode(allocateID());
	}

	/*
	 * Since it is not possible to create random strings we use a generative
	 * naive approach
	 */
	final static private AtomicInteger currentId = new AtomicInteger();

	public static String allocateID() {
		return "" + currentId.incrementAndGet();
	}

	// @Ensures({ "#this.vms = 0", "#this.images = 0" })
	@Ensures({ "no this.vms", "no this.images", "no this.flavors" })
	@Modifies({ "this.vms", "this.images", "this.flavors" })
	private void init() {
		Squander.exe(this);
	}

	@Requires({ "null ! in _images.elts" })
	@Ensures({ "no this.vms", "this.images = _images.elts",
			"this.flavors = _flavors.elts" })
	@Modifies({ "this.vms", "this.images", "this.flavors" })
	private void init(Set<Image> _images, Set<Hardware> _flavors) {
		Squander.exe(this, _images, _flavors);
	}

	@Ensures("return.elts == this.images")
	@FreshObjects(cls = Set.class, typeParams = { Image.class }, num = 1)
	@Modifies("return.elts")
	public Set<Image> getAllImages() {
		return Squander.exe(this);
	}

	@Ensures("return.elts == this.flavors")
	@FreshObjects(cls = Set.class, typeParams = { Hardware.class }, num = 1)
	@Modifies("return.elts")
	public Set<Hardware> getAllFlavors() {
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
	@Requires({ "newNodeID !in @old(this.vms.id)", "#this.images > 0" })
	@Ensures({
			/* Deploy the new node with given ID and RUNNING state */
			// THIS ONE IS FAULTY + return?
			"this.vms = @old(this.vms) + return",
			/* Maintain ID */
			"return.id = newNodeID",
			/* Start VM as RUNNING */
			"return.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
			/*
			 * Node and Image connection: we do not care, but the image for the
			 * new node must be one of the available one !!
			 */
			"return.image in this.images" })
	@Modifies({ "this.vms", "return.id", "return.image", "return.status" })
	@Options(ensureAllInts = true, solveAll = true, bitwidth = 8)
	public DeclarativeNode createNode(String newNodeID) {
		return Squander.exe(this, newNodeID);
	}

	@Requires({
			// At least one VM
			"some this.vms",
			// The node to stop must be in the running nodes
			"node.id in this.vms.id" })
	@Ensures({ "node.id !in this.vms.id && #this.vms = #@old(this.vms) - 1" })
	@Modifies({ "this.vms", })
	public void removeNode(DeclarativeNode node) {
		Squander.exe(this, node);
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
			// The node to stop must be in the running nodes
			"node.id in this.vms.id" })
	@Ensures("one vm : this.vms | vm.id=node.id && return.id = vm.id && return.status=vm.status && return.image=vm.image")
	@Modifies({ "return.id", "return.status", "return.image" })
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(DeclarativeNode node) {
		return Squander.exe(this, node);
	}

	@Requires({
			// At least one VM
			"some this.vms",
			// The node must be in the running nodes
			"return.id in this.vms.id" })
	// Return a COPY of the NODE- maybe this one is better to do imperatively ?!
	@Ensures("one vm : this.vms | vm.id=_id && return.id = vm.id && return.status=vm.status && return.image=vm.image")
	@Modifies({ "return.id", "return.status", "return.image" })
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(String _id) {
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

	// /*
	// * IMAGE MANAGEMENT: not required for "pure" ComputeService scenario
	// */
	// public void addImage(Image i) {
	// addImage(i, allocateID());
	// }
	//
	// ALAS with do not access the ID element the Image.sfspec are not used !
	// @Ensures({ "this.images = @old(this.images) + i",//
	// "i.imageID = _id" })
	// @Modifies({ "this.images", "i.imageID" })
	// public void addImage(Image i, String _id) {
	// Squander.exe(this, i, _id);
	// }
	//
	// /*
	// * HARDWARE MANAGEMENT: not required for "pure" ComputeService scenario
	// */
}
