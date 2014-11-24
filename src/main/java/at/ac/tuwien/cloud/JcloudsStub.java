package at.ac.tuwien.cloud;

import static com.google.common.collect.Iterables.find;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadataStatus;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import at.ac.tuwien.cloud.core.DeclarativeCloud;
import at.ac.tuwien.cloud.core.DeclarativeHardware;
import at.ac.tuwien.cloud.core.DeclarativeImage;
import at.ac.tuwien.cloud.core.DeclarativeLocation;
import at.ac.tuwien.cloud.core.DeclarativeNode;
import at.ac.tuwien.cloud.core.impl.DeclarativeCloudImpl;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.InetAddresses;

/**
 * First, proof of concept implementation of a stub. The stub is that class that implements the various methods for
 * interacting with the cloud.
 * 
 * TODO: CREATE FRESH OBJECTS FROM THE CLOUD OR FROM THE STUB !!
 * 
 * @author alessiogambi
 *
 */
public class JcloudsStub {

	// Not really ok but let it go for the moment
	private Executor ioExecutor = Executors.newFixedThreadPool(2);

	// Note that here we might provide other clouds as well if required, or at least different models of the cloud
	private DeclarativeCloud cloud;

	// Internal management of data structures in term of jclouds instances
	private Set<Image> images;
	private Set<Hardware> hardwares;
	private Set<Location> locations;
	private Map<String, NodeMetadata> nodes;

	// TODO Not sure we need this
	// private Map<String, Template> nodeTemplate;

	// TODO Fix this:
	Random random = new Random();

	public JcloudsStub(Set<Image> images, Set<Hardware> hardwares, Set<Location> locations, Set<NodeMetadata> nodes) {
		// Abstract the Cloud
		Set<DeclarativeLocation> dLocations = new HashSet<DeclarativeLocation>();
		Set<DeclarativeImage> dImages = new HashSet<DeclarativeImage>();
		Set<DeclarativeHardware> dHardwares = new HashSet<DeclarativeHardware>();

		Set<DeclarativeNode> dNodes = new HashSet<DeclarativeNode>();

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

		this.nodes = new HashMap<String, NodeMetadata>();
		for (NodeMetadata n : nodes) {

			DeclarativeNode dn = new DeclarativeNode();
			dn.setId(n.getId());
			dn.setName(n.getName());
			dn.setGroup(n.getGroup());
			// Abstract Location !
			// Find the right location here
			for (DeclarativeLocation dl : dLocations) {
				if (dl.getId().equals(n.getLocation().getId())) {
					dn.setLocation(dl);
					break;
				}
			}
			// Find the right hardware here
			for (DeclarativeHardware dh : dHardwares) {
				if (dh.getId().equals(n.getHardware().getId())) {
					dn.setHardware(dh);
					break;
				}
			}

			// Find the right image here
			for (DeclarativeImage di : dImages) {
				if (di.getId().equals(n.getImageId())) {
					dn.setImage(di);
					break;
				}
			}

			this.nodes.put(n.getId(), n);

		}

		// TODO Add nodes if any !!
		cloud = new DeclarativeCloudImpl(dImages, dHardwares, dLocations);
		//
		this.locations = locations;
		this.images = images;
		this.hardwares = hardwares;
	}

	// Concretization function from AbstractNode and concrete NodeMetadata
	// Just read from the cache !
	private NodeMetadata concretize(DeclarativeNode node) {

		System.out.println("JcloudsStub.concretize() " + node);

		if (node == null) {
			// Defensive !
			return null;
		} else {
			return this.nodes.get(node.getId());
		}

		// Location location = ((Location) find(locations, new Predicate<Location>() {
		// @Override
		// public boolean apply(Location input) {
		// return input.getId().equals(node.getLocation().getId());
		// }
		// }));
		//
		// Image image = ((Image) find(images, new Predicate<Image>() {
		//
		// @Override
		// public boolean apply(Image input) {
		// return input.getId().equals(node.getImage().getId());
		// }
		// }));
		//
		// Map<String, String> metadata = (cachedNodeIDuserMetadata.get(node.getId()) != null) ?
		// cachedNodeIDuserMetadata
		// .get(node.getId()).getUserMetadata() : new HashMap<String, String>();
		// Set<String> tags = (cachedNodeIDuserMetadata.get(node.getId()) != null) ? cachedNodeIDuserMetadata.get(
		// node.getId()).getTags() : new HashSet<String>();
		//

	}

	// TODO For the moment return just the cache, then we need to update the returned object with the actual cloud
	// content
	public Iterable<NodeMetadata> getAllNodes() {
		return this.nodes.values();
	}

	// TODO For the moment return just the cache, then we need to update the returned object with the actual cloud
	// content
	public NodeMetadata getNode(String id) {
		return (concretize(cloud.getNode(id)));
	}

	public Iterable<NodeMetadata> getAllNodesById(Iterable<String> ids) {
		// Concretization for Set
		Set<String> _ids = new HashSet<String>();
		for (String id : ids) {
			_ids.add(id);
		}

		ImmutableList.Builder<NodeMetadata> result = ImmutableList.builder();
		for (DeclarativeNode abstractNode : cloud.getNodes(_ids)) {
			result.add(concretize(abstractNode));
		}
		return result.build();
	}

	public void destroyNode(String id) {
		// Remove the node from the cloud - Pay attention to sync and such !
		cloud.removeNode(id);
		// Shall we update also the cache ?!
		this.nodes.remove(id);
	}

	public NodeMetadata createNode(String group, String name, Template template) {
		// Allocate a new ID
		String nodeId = UUID.randomUUID().toString();
		// Extract from template the relevant objects and map the to abstrac objects

		// Abstraction
		DeclarativeLocation location = cloud.getLocation(template.getLocation().getId());
		DeclarativeImage image = cloud.getImage(template.getImage().getId());
		DeclarativeHardware hardware = cloud.getHardware(template.getHardware().getId());

		// This is the cloud state change - Image is null -> Squander fails !!
		DeclarativeNode node = cloud.createNode(nodeId, name, group, location, hardware, image);

		// Here we update the local variables
		// TODO: Generate VALID addresses !!
		node.setPrivateAddress(InetAddresses.fromInteger(random.nextInt()).getHostAddress());
		node.setPublicAddress(InetAddresses.fromInteger(random.nextInt()).getHostAddress());

		NodeMetadata concreteNode = new NodeMetadataBuilder()//
				.ids(node.getId())//
				.name(node.getName())//
				.group(node.getGroup())//
				.imageId(node.getImage().getId())//
				.status(node.getStatus())//
				// NOT SURE
				.hostname(node.getName())//
				// TODO - The address here must match the one in the expect ! - IP Allocation !!
				.privateAddresses(ImmutableSet.<String> builder().add(node.getPrivateAddress()).build())//
				.publicAddresses(ImmutableSet.<String> builder().add(node.getPublicAddress()).build())//
				// The passwors must match the ones in the expect
				.credentials(LoginCredentials.builder().user("root").password("password" + node.getId()).build())//
				.location(template.getLocation())//
				.operatingSystem(template.getImage().getOperatingSystem())//
				.userMetadata(template.getOptions().getUserMetadata())//
				// ...
				.hardware(template.getHardware())//
				.tags(template.getOptions().getTags())//
				.build();

		// Keep the new node in cache
		this.nodes.put(nodeId, concreteNode);
		// this.nodeTemplate.put(node.getId(), template);

		return concreteNode;

	}

	protected void setStateOnNodeAfterDelay(final NodeMetadataStatus status, final NodeMetadata node, final long millis) {
		if (millis == 0l) {
			setStateOnNode(status, node);
		} else {
			ioExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(millis);
					} catch (InterruptedException e) {
						Throwables.propagate(e);
					}
					setStateOnNode(status, node);
				}
			});
		}

	}

	private void setStateOnNode(final NodeMetadataStatus status, final NodeMetadata node) {
		NodeMetadataBuilder b = new NodeMetadataBuilder().fromNodeMetadata(this.nodes.get(node.getId()));
		// We need to update the local cache of nodes !
		if (NodeMetadataStatus.SUSPENDED.equals(status)) {
			cloud.suspendNode(node.getId());
			b.status(status);
		} else if (NodeMetadataStatus.RUNNING.equals(status)) {
			cloud.startNode(node.getId());
			b.status(status);
		} else {
			System.err.println("Wrong status ? " + status);
		}
		// Update cache
		this.nodes.put(node.getId(), b.build());

	}

	// TODO Implement this with the DeclarativeCloud
	public void rebootNode(String id) {
		setStateOnNode(NodeMetadataStatus.SUSPENDED, getNode(id));
		setStateOnNode(NodeMetadataStatus.RUNNING, getNode(id));
	}

	// TODO Implement this with the DeclarativeCloud
	public void resumeNode(String id) {
		setStateOnNode(NodeMetadataStatus.RUNNING, getNode(id));
	}

	// TODO Implement this with the DeclarativeCloud
	public void suspendNode(String id) {
		setStateOnNode(NodeMetadataStatus.SUSPENDED, getNode(id));
	}

	// TODO Auto-generated method stub
	public Iterable<Location> getAllLocations() {
		return this.locations;
	}

	// TODO Auto-generated method stub
	public Iterable<Hardware> getAllHardwares() {
		return this.hardwares;
	}

	// TODO Auto-generated method stub
	public Iterable<Image> getAllImages() {
		return this.images;
	}

	// TODO Auto-generated method stub
	public Image getImage(String id) {
		final String _id = id;
		return ((Image) find(images, new Predicate<Image>() {

			@Override
			public boolean apply(Image input) {
				return (input.getId().equals(_id));
			}

		}));
	}

	// TODO Auto-generated method stub
	public Hardware getHardware(String id) {
		final String _id = id;
		return ((Hardware) find(hardwares, new Predicate<Hardware>() {

			@Override
			public boolean apply(Hardware input) {
				return (input.getId().equals(_id));
			}

		}));
	}

	// TODO Auto-generated method stub
	public Location getLocation(String id) {
		final String _id = id;
		return ((Location) find(locations, new Predicate<Location>() {

			@Override
			public boolean apply(Location input) {
				return (input.getId().equals(_id));
			}

		}));
	}
}
