package org.jclouds.compute.declarativestub.core;

public class DeclarativeNode {

	private int id;

	public int getId() {
		return id;
	}

	public String toString() {
		return "Node = " + getId() + " -- " + this.hashCode();
	}

	public void setId(int id) {
		this.id = id;
	}
}
