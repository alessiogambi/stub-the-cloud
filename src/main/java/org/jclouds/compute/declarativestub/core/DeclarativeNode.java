package org.jclouds.compute.declarativestub.core;

public class DeclarativeNode {

	int id;

	public int getId() {
		return id;
	}

	public String toString() {
		return "Node = " + id + " -- " + this.hashCode();
	}
}
