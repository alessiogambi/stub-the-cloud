package org.jclouds.compute.declarativestub.core;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
@SpecField({ "vms : set DeclarativeNode"

})
@Invariant({//
/* All the VM must have unique ID */
"all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
/* Null is not an option */
"null ! in this.vms" })
public class DeclarativeCloud {

	// For the moment this seems to work but not what I wanted :(
	// final NodeMetadataStatus runningEnumStatus = NodeMetadataStatus.RUNNING;
	// final NodeMetadataStatus suspendedEnumStatus =
	// NodeMetadataStatus.SUSPENDED;

	public DeclarativeCloud() {
		init();
	}

	@Ensures({ "no this.vms" })
	private void init() {
		Squander.exe(this);
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
	@Requires({ "newNodeID !in @old(this.vms.id)", })
	@Ensures({
			"this.vms = @old(this.vms) + return",//
			"return.status =  org.jclouds.compute.domain.NodeMetadataStatus.RUNNING",
			"return.id = newNodeID" })
	@Modifies({ "this.vms", "return.id", "return.status" })
	@Options(ensureAllInts = true)
	public DeclarativeNode createNode(String newNodeID) {
		return Squander.exe(this, newNodeID);
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
			// The node to start must be in the available nodes
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
			// The node to start must be in the available nodes
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

}
