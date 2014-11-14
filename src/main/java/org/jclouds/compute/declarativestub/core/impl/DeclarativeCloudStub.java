package org.jclouds.compute.declarativestub.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.jclouds.compute.declarativestub.core.DeclarativeCloud;
import org.jclouds.compute.declarativestub.core.DeclarativeHardware;
import org.jclouds.compute.declarativestub.core.DeclarativeImage;
import org.jclouds.compute.declarativestub.core.DeclarativeLocation;
import org.jclouds.compute.declarativestub.core.DeclarativeNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.squander.Squander;

public class DeclarativeCloudStub implements DeclarativeCloud {
	public String toString() {

		return "\n" + //
				"IMAGES:" + this.getAllImages() + "\n"//
				+ "LOCATIONS:" + this.getAllLocations() + "\n"//
				+ "HARDWARES:" + this.getAllHardwares() + "\n"//
				+ "NODES:" + this.getAllNodes();
	}

	// Not sure this pattern is ok !
	public DeclarativeNode createNode() {
		return createNode(FactoryId.allocateID());
	}

	public DeclarativeCloudStub(Set<Image> images, Set<Hardware> hardwares, Set<Location> locations) {
		// TODO Here I cannot find a way to automatically abstract from these inputs
		Set<DeclarativeLocation> dLocations = new HashSet<DeclarativeLocation>();
		Set<DeclarativeImage> dImages = new HashSet<DeclarativeImage>();
		Set<DeclarativeHardware> dHardwares = new HashSet<DeclarativeHardware>();

		// Note that WE NEED to maintain the Location - Hardware, Location - Image Reference !
		for (Location l : locations) {
			DeclarativeLocation dl = new DeclarativeLocation();
			dl.setId(l.getId());
			dLocations.add(dl);
		}

		for (Hardware h : hardwares) {
			DeclarativeHardware dh = new DeclarativeHardware();
			dh.setId(h.getId());

			// Find the right location here
			for (DeclarativeLocation dl : dLocations) {
				if (dl.getId().equals(h.getLocation().getId())) {
					dh.setLocation(dl);
					break;
				}
			}

			dHardwares.add(dh);
		}
		for (Image i : images) {
			DeclarativeImage di = new DeclarativeImage();
			di.setId(i.getId());
			di.setName(i.getName());
			// Find the right location here
			for (DeclarativeLocation dl : dLocations) {
				if (dl.getId().equals(i.getLocation().getId())) {
					di.setLocation(dl);
					break;
				}
			}
			dImages.add(di);
		}

		init(dImages, dHardwares, dLocations);
	}

	public DeclarativeCloudStub() {
		init();
	}

	/*
	 * ANNOTATED METHODS BELOW
	 */

	@Ensures({ "no this.instances", "no this.images", "no this.hardwares", "no this.locations" })
	@Modifies({ "this.instances", "this.images", "this.hardwares", "this.locations" })
	private void init() {
		Squander.exe(this);
	}

	@Requires({
			"some _locations",
			/* All the input resources must have unique ID */
			"all imageA : _images.elts | all imageB : _images.elts - imageA | imageA.id != null && imageA.id != imageB.id",
			"all hardwareA : _hardwares.elts | all hardwareB : _hardwares.elts - hardwareA | hardwareA.id != null && hardwareA.id != hardwareB.id",
			"all locationA : _locations.elts | all locationB : _locations.elts - locationA | locationA.id != null && locationA.id != locationB.id",
			/* Input resources cannot be null */
			"_locations != null && _hardwares != null && _images != null",
			/* Inputs cannot contain null elements */
			"no (null & _locations.elts) && no (null & _hardwares.elts) && no (null & _images.elts)",
			/* Respect Location Constraints */
			" _images.elts.location in _locations.elts", "_hardwares.elts.location in _locations.elts",
	//
	})
	@Ensures({
	/* No virtual machines are running */
	"no this.instances", //
			/*
			 * Maintain the original relations in the abstract state.
			 */
			"this.images = _images.elts", //
			"this.hardwares == _hardwares.elts",//
			"this.locations == _locations.elts",//
	// "all image : this.images "
	// "this.images.location = _images.elts.location",
	// //
	// "this.hardwares.location == _hardwares.elts.location",//
	//
	/* EOF */})
	@Modifies({ "this.instances", //
			"this.images", "this.hardwares",
			//
			"this.locations"
	/* EOF */
	})
	private void init(Set<DeclarativeImage> _images, Set<DeclarativeHardware> _hardwares,
			Set<DeclarativeLocation> _locations) {
		Squander.exe(this, _images, _hardwares, _locations);
	}

	@Override
	public Set<DeclarativeLocation> getAllLocations() {
		return Squander.exe(this);
	}

	@Override
	public Set<DeclarativeHardware> getAllHardwares() {
		return Squander.exe(this);
	}

	@Override
	public Set<DeclarativeImage> getAllImages() {
		return Squander.exe(this);
	}

	@Override
	public Set<DeclarativeNode> getAllNodes() {
		return Squander.exe(this);
	}

	@Override
	public Set<DeclarativeNode> getNodes(Set<String> ids) {
		return Squander.exe(this, ids);
	}

	@Override
	public DeclarativeNode createNode(String newNodeID) {
		return Squander.exe(this, newNodeID);
	}

	@Override
	public void removeNode(String _id) {
		Squander.exe(this, _id);
	}

	@Override
	public DeclarativeNode getNode(String _id) {
		return Squander.exe(this, _id);
	}

	@Override
	public DeclarativeImage getImage(String _id) {
		return Squander.exe(this, _id);
	}

	/* Images Status Update */
	@Override
	public void startNode(String _id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void suspendNode(String _id) {
		// TODO Auto-generated method stub

	}

	@Override
	public DeclarativeLocation getLocation(String _id) {
		return Squander.exe(this, _id);
	}

	@Override
	public DeclarativeHardware getHardware(String _id) {
		return Squander.exe(this, _id);
	}

	@Override
	public DeclarativeNode createNode(String newNodeID, String _name, String _group, DeclarativeLocation _location,
			DeclarativeHardware _hardware, DeclarativeImage _image) {
		return Squander.exe(this, newNodeID, _name, _group, _location, _hardware, _image);
	}

}
