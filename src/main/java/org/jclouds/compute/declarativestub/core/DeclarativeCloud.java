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

// TODO Possibly the DeclarativeNode can be directly specified in here !!
// VM with state vs relations of VM ?
@SpecField({
/* The set of deployed virtual machine */
"vms : set DeclarativeNode",
/* The subset of running virtual machine */
"running : set DeclarativeNode",
/* The subset of suspended/stopped virtual machine */
"stopped : set DeclarativeNode",
/*
 * IMAGE
 */
"images : set org.jclouds.compute.domain.Image" })
//
//
@Invariant({
/* All the VM must have unique ID */
"all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
/* Running Virtual Machines */
"this.running in this.vms",
/**/
"all vm : this.running | vm.status = NodeMetadataStatus.RUNNING",
/* Stopped Virtual Machines */
"this.stopped in this.vms",
/**/
"all vm : this.stopped | vm.status = NodeMetadataStatus.SUSPENDED",
/* All the VM must have 1 single state */
" no (this.running & this.stopped)",
/* Null is not an option */
"null ! in this.vms",
/* Null is not an option */
"null ! in this.images"

})
public class DeclarativeCloud {

	public String toString() {
		return "IMAGES:" + this.getAllImages() + "\n" + "NODES:"
				+ this.getAllNodes() + "\n";
	}

	public DeclarativeCloud() {
		clearImages();
		clearVirtualMachines();
	}

	public DeclarativeCloud(Set<Image> images) {
		initImages(images);
		clearVirtualMachines();
	}

	@Ensures({ "_images.elts in this.images" })
	@Modifies({ "this.images" })
	private void initImages(Set<Image> _images) {
		Squander.exe(this, _images);
	}

	@Ensures({ "no this.images" })
	private void clearImages() {
		Squander.exe(this);
	}

	@Ensures({ "no this.vms" })
	private void clearVirtualMachines() {
		Squander.exe(this);
	}

	@Ensures("return.elts == this.vms")
	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Modifies("return.elts")
	public Set<DeclarativeNode> getAllNodes() {
		return Squander.exe(this);
	}

	@Ensures("return.elts == this.images")
	@FreshObjects(cls = Set.class, typeParams = { Image.class }, num = 1)
	@Modifies("return.elts")
	public Set<Image> getAllImages() {
		return Squander.exe(this);
	}

	@FreshObjects(cls = Set.class, typeParams = { DeclarativeNode.class }, num = 1)
	@Requires("ids.elts in this.vms.id")
	@Modifies("return.elts")
	@Ensures({ "return.elts in this.vms && ids.elts == return.elts.id" })
	// TODO Iterable -> Set
	public Set<DeclarativeNode> getNodes(Set<String> ids) {
		return Squander.exe(this, ids);
	}

	/*
	 * @ Generate an Unique ID for this cloud via a stupid Factory method. Cloud
	 * specific ways of generating ID (e.g. Random UUID) must be encoded somehow
	 * . Ideally should be the underlying framework to deal with that. TODO
	 * Inject the ID provider ad done in StubClientAdapter
	 */
	private final static AtomicInteger id = new AtomicInteger(0);

	private static String allocateNewId() {
		return "" + id.incrementAndGet();
	}

	public DeclarativeNode createNode() {
		return createNode(allocateNewId());
	}

	public void addImage(Image i) {
		addImage(i, allocateNewId());
	}

	/*
	 * @
	 */

	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Requires({ "return.id ! in  @old(this.vms.id)" })
	@Ensures({
			"this.vms = @old(this.vms) + return",//
			"return !in @old(this.vms)", "return.status = Status.RUNNING",
			"return.id = _id" })
	@Modifies({ "this.vms", "return.id", "return.status" })
	// @Options(ensureAllInts = true) Not needed anymore since we create the id
	// outside an pass that as argument
	public DeclarativeNode createNode(String _id) {
		return Squander.exe(this, _id);
	}

	@Ensures({ "this.images = @old(this.images) + i",//
			"i.imageID ! in  @old(this.images.imageID)", //
			"i.imageID = _id" })
	@Modifies({ "this.images", "i.imageID" })
	@Options(ensureAllInts = true)
	public void addImage(Image i, String _id) {
		Squander.exe(this, i, _id);
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
	@Ensures("one vm : this.vms | vm.id=node.id && return.id = vm.id && return.status=vm.status")
	@Modifies({ "return.id", "return.status" })
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
	@Ensures("one vm : this.vms | vm.id=_id && return.id = vm.id && return.status=vm.status")
	@Modifies({ "return.id", "return.status" })
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(String _id) {
		return Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.vms",
			// The node must exist
			"one vm : this.vms | vm.id == _id" })
	// This is redundant if we use the instance selector
	@Ensures({ "one vm : this.vms | ( vm.id == _id && vm.status = Status.RUNNING)" })
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
	// This is redundant if we use the instance selector
	@Ensures({ "one vm : this.vms | ( vm.id == _id && vm.status = Status.SUSPENDED )" })
	@Modifies({ "DeclarativeNode.status [{vm : this.vms | vm.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void suspendNode(String _id) {
		Squander.exe(this, _id);
	}

}
