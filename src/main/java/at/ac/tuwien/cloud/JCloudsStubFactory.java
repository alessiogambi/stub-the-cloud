package at.ac.tuwien.cloud;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements a stub for jCloudCompute Service
 * 
 * @author alessiogambi
 *
 */
public class JCloudsStubFactory {

	private final static ConcurrentHashMap<String, JcloudsStub> stubs = new ConcurrentHashMap<String, JcloudsStub>();

	public static void setStub(String stubID, JcloudsStub stub) {
		stubs.put(stubID, stub);
	}

	public static JcloudsStub getStub(String stubID) {
		if (stubs.containsKey(stubID)) {
			return stubs.get(stubID);
		} else {
			return null;
		}
	}
}
