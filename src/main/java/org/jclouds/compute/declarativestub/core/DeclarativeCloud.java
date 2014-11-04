package org.jclouds.compute.declarativestub.core;

import java.util.Set;

import org.jclouds.compute.domain.NodeMetadataStatus;

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
@SpecField({ "vms : set DeclarativeNode",//
		"running : set DeclarativeNode",//
		"stopped : set DeclarativeNode",

})
@Invariant({//
/* All the VM must have unique ID */
"all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
/* Running Virtual Machines */
"this.running in this.vms",
/**/
"all vm : this.running | vm.status = this.runningEnumStatus",
/* Stopped Virtual Machines */
"this.stopped in this.vms",
/**/
"all vm : this.stopped | vm.status = this.suspendedEnumStatus",
/* All the VM must have 1 single state */
" no (this.running & this.stopped)",
/* Null is not an option */
"null ! in this.vms" })
public class DeclarativeCloud {

	// For the moment this seems to work but not what I wanted :(
	final NodeMetadataStatus runningEnumStatus = NodeMetadataStatus.RUNNING;
	final NodeMetadataStatus suspendedEnumStatus = NodeMetadataStatus.SUSPENDED;

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
	// TODO Integer -> String, Iterable -> Set
	public Set<DeclarativeNode> getNodes(Set<Integer> ids) {
		return Squander.exe(this, ids);
	}

	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Ensures({
			"this.vms = @old(this.vms) + return",//
			"return !in @old(this.vms)",
			"return.status = this.runningEnumStatus" })
	@Modifies({ "this.vms", "return.id", "return.status" })
	@Options(ensureAllInts = true)
	public DeclarativeNode createNode() {
		return Squander.exe(this);
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
	public void removeNode(int _id) {
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
	public DeclarativeNode getNode(int _id) {
		return Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.vms",
			// The node to start must be in the available nodes
			"one vm : this.vms | vm.id == _id" })
	@Ensures({ "one vm : this.vms | ( vm.id == _id && vm.status = this.runningEnumStatus)" })
	@Modifies({ "DeclarativeNode.status [{vm : this.vms | vm.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void startNode(int _id) {
		// TODO: by changing the state we change the running/stopped relation,
		// but we do not declare them as "mofiable"
		Squander.exe(this, _id);
	}

	@Requires({
			// At least one VM
			"some this.vms",
			// The node to start must be in the available nodes
			"one vm : this.vms | vm.id == _id" })
	@Ensures({ "one vm : this.vms | ( vm.id == _id && vm.status = this.suspendedEnumStatus )" })
	@Modifies({ "DeclarativeNode.status [{vm : this.vms | vm.id == _id}]" })
	/**
	 * This call is idempotent
	 * @param _id
	 */
	public void suspendNode(int _id) {
		Squander.exe(this, _id);
	}

}
