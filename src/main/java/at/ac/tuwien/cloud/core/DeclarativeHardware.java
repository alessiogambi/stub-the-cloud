package at.ac.tuwien.cloud.core;

public class DeclarativeHardware extends DeclarativeResource {
	private String name;

	private DeclarativeLocation location;

	public DeclarativeLocation getLocation() {
		return location;
	}

	public void setLocation(DeclarativeLocation location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
