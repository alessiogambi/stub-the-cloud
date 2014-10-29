package org.jclouds.compute.declarativestub.core;

import org.testng.annotations.Test;

public class DeclarativeCloudTest {

	@Test
	public void testInit() {
		DeclarativeCloud c = new DeclarativeCloud();
		System.out.println("DeclarativeCloudTest.testInit() " + c);
	}

	@Test
	public void testListNodesEmpty() {
		DeclarativeCloud c = new DeclarativeCloud();
		System.out
				.println("DeclarativeCloudTest.testInit() " + c.getAllNodes());
	}

	@Test
	public void testAddNode() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.addNode();
		System.out.println("DeclarativeCloudTest.testaddNode() Node " + n);
	}

	@Test
	public void testAddAndListNode() {
		DeclarativeCloud c = new DeclarativeCloud();
		c.addNode();
		System.out.println("DeclarativeCloudTest.testAddAndListNode() Nodes "
				+ c.getAllNodes());
	}

	@Test
	public void testAddNodes() {
		DeclarativeCloud c = new DeclarativeCloud();
		DeclarativeNode n = c.addNode();
		DeclarativeNode n1 = c.addNode();
		System.out.println("DeclarativeCloudTest.testAddNodes() Node 1: " + n);
		System.out.println("DeclarativeCloudTest.testAddNodes() Node 2: " + n1);
	}

}
