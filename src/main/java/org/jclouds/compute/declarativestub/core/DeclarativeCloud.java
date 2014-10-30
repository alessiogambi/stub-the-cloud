package org.jclouds.compute.declarativestub.core;

import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.annotations.FreshObjects;
import edu.mit.csail.sdg.squander.annotations.Options;

/**
 * This abstract class contains some of the specifications of a generic cloud
 * 
 * @author alessiogambi
 *
 */

// TODO Possibly the DeclarativeNode can be directly specified in here !!

@SpecField({ "vms : set DeclarativeNode" })
@Invariant({//
/* All the VM must have unique ID */
"all vmA : this.vms | all vmB : this.vms - vmA | vmA.id != vmB.id",
/* Null is not an option */
"null ! in this.vms" })
public class DeclarativeCloud {

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
	@Modifies("return.elts")
	@Ensures({ "return.elts in this.vms && ids.elts == return.elts.id" })
	// TODO Integer -> String, Iterable -> Set
	public Set<DeclarativeNode> getNodes(Set<Integer> ids) {
		return Squander.exe(this, ids);
	}

	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	@Ensures({ "this.vms = @old(this.vms) + return",//
			"return !in @old(this.vms)" })
	@Modifies({ "this.vms", "return.id" })
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
	@Ensures("return.id == node.id")
	@Modifies("return.id")
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(DeclarativeNode node) {
		return Squander.exe(this, node);
	}

	// TODO removeNode(int id) !! I know I can new DeclarativeNode( id ) but I
	// do not want to !!
	// TODO getNode(int id) !! I know I can new DeclarativeNode( id ) but I

	@Requires({
			// At least one VM
			"some this.vms",
			// The node to stop must be in the running nodes
			"return.id in this.vms.id" })
	@Ensures("return.id = _id")
	@Modifies("return.id")
	@FreshObjects(cls = DeclarativeNode.class, num = 1)
	public DeclarativeNode getNode(int _id) {
		return Squander.exe(this, _id);
	}

}
