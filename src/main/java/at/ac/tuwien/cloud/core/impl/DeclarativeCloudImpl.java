package at.ac.tuwien.cloud.core.impl;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import at.ac.tuwien.cloud.core.DeclarativeCloud;
import at.ac.tuwien.cloud.core.DeclarativeHardware;
import at.ac.tuwien.cloud.core.DeclarativeImage;
import at.ac.tuwien.cloud.core.DeclarativeLocation;
import at.ac.tuwien.cloud.core.DeclarativeNode;
import edu.mit.csail.sdg.squander.Squander;

public class DeclarativeCloudImpl implements DeclarativeCloud {
	public String toString() {
		return "\n" + //
				"IMAGES:" + this.getAllImages() + "\n"//
				+ "LOCATIONS:" + this.getAllLocations() + "\n"//
				+ "HARDWARES:" + this.getAllHardwares() + "\n"//
				+ "NODES:" + this.getAllNodes();
	}

	// Not sure this pattern is ok, this is to just create a random node
	public DeclarativeNode createNode() {
		return createNode(allocateID());
	}

	/*
	 * Since it is not possible to create random strings we use a generative naive approach. The test assume that this
	 * atomic integer will be reset everytime we create an instance of cloud
	 */
	final private AtomicInteger currentId = new AtomicInteger(0);

	public String allocateID() {
		return "" + currentId.incrementAndGet();
	}

	/**
	 * This implements abstraction/concretization via caching
	 * 
	 * @param images
	 * @param hardwares
	 * @param locations
	 */
	public DeclarativeCloudImpl(Set<DeclarativeImage> images, Set<DeclarativeHardware> hardwares,
			Set<DeclarativeLocation> locations) {
		init(images, hardwares, locations);
	}

	public DeclarativeCloudImpl() {
		init();
	}

	/*
	 * ANNOTATED METHODS BELOW
	 */

	@Override
	public void init() {
		Squander.exe(this);
	}

	@Override
	public void init(Set<DeclarativeImage> _images, Set<DeclarativeHardware> _hardwares,
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
	public DeclarativeNode createNode(String _id) {
		return Squander.exe(this, _id);
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
		Squander.exe(this, _id);

	}

	@Override
	public void suspendNode(String _id) {
		Squander.exe(this, _id);

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
		// NOTE: since objects can be null we need to explicitly pass Class and Values as separated inputs to squander !
		return Squander.exe(this, new Class[] { String.class, String.class, String.class, DeclarativeLocation.class,
				DeclarativeHardware.class, DeclarativeImage.class }, new Object[] { newNodeID, _name, _group,
				_location, _hardware, _image });
	}
}
