package edu.mit.csail.sdg.squander.serializer;

import java.util.LinkedList;
import java.util.List;

import org.jclouds.compute.domain.ImageStatus;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.domain.Location;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.serializer.special.IObjSer;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * A first trial in creating Squander object serializer for existing Classes
 * 
 * @author alessiogambi
 *
 */
public class ImageImplSer implements IObjSer {

	public static final String ID = "id";
	public static final String LOCATION = "location";
	public static final String STATUS = "status";

	@Override
	public boolean accepts(Class<?> clz) {
		return ImageImpl.class.isAssignableFrom(clz);
	}

	@Override
	public ImageImpl newInstance(Class<?> cls) {
		// Since at the moment we rely on user-provided instances we do not let
		// Squander create instances of this interface
		throw new RuntimeException("Cannot create instance of Image");
		// // Create a default image
		// Image image = new ImageBuilder()
		// .ids("Default IMAGE")
		// .name("Default IMAGE")
		// .location(null)
		// .operatingSystem(
		// new OperatingSystem(OsFamily.LINUX, "desc", "version",
		// null, "desc", false)).description("desc")
		// .status(ImageStatus.AVAILABLE).build();
		// return image;
	}

	@Override
	public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
		Log.debug("\n\tImageImplSer.absFunc() " + obj.getClass() + "@" + obj.hashCode() + "\n");
		ClassSpec cls = javaScene.classSpecForObj(obj);
		List<FieldValue> result = new LinkedList<FieldValue>();

		// From the concrete object derive the abstract state (the id)
		ImageImpl concreteObject = (ImageImpl) obj;
		String id = concreteObject.getId();
		Location location = concreteObject.getLocation();
		ImageStatus status = concreteObject.getStatus();

		// Store the relation ?
		try {
			FieldValue idFvLen = new FieldValue(cls.findField(ID), 2);
			idFvLen.addTuple(new ObjTuple(obj, id));
			result.add(idFvLen);
		} catch (Throwable e) {
			System.err.println("ImageSer.absFunc() Missing " + ID);
		}
		try {
			FieldValue locationFvLen = new FieldValue(cls.findField(LOCATION), 2);
			locationFvLen.addTuple(new ObjTuple(obj, location));
			result.add(locationFvLen);
		} catch (Throwable e) {
			System.err.println("ImageSer.absFunc() Missing " + LOCATION);
		}

		try {
			FieldValue statusFvLen = new FieldValue(cls.findField(STATUS), 2);
			statusFvLen.addTuple(new ObjTuple(obj, status));
			result.add(statusFvLen);
		} catch (Throwable e) {
			System.err.println("ImageSer.absFunc() Missing " + STATUS);
		}

		Log.debug("ImageSer.absFunc() " + result);
		return result;
	}

	// It seams that sometimes this concretize one field with another value...
	// not sure why !
	@Override
	public Object concrFunc(Object obj, FieldValue fieldValue) {
		Log.debug("ImageSer.concrFunc() object " + obj.getClass() + "@" + obj.hashCode());
		Log.debug("ImageSer.concrFunc() fieldValue " + fieldValue);
		String fldName = fieldValue.jfield().name();
		// TODO Check that the object is really an Image ?
		if (ID.equals(fldName)) {
			return restoreID(obj, fieldValue);
		} else if (LOCATION.equals(fldName)) {
			return restoreLocation(obj, fieldValue);
		} else if (STATUS.equals(fldName)) {
			return restoreStatus(obj, fieldValue);
		}

		else if (!fieldValue.jfield().isPureAbstract()) {
			return obj;
		} else {
			throw new RuntimeException("Unknown field name for Image class: " + fldName);
		}
	}

	// Not sure this is really ok !
	private Object restoreID(Object concreteObj, FieldValue fieldValue) {
		try {
			ObjTupleSet value = fieldValue.tupleSet();

			assert value.arity() == 2;

			ImageImpl image = (ImageImpl) concreteObj;
			// Reset all the attributes
			Log.debug("ImageSer.restoreID() Reset : " + value.size());
			for (ObjTuple ot : value) {
				image.setId((String) ot.get(1));
			}

			return image;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Unknown error " + e.getMessage());
		}

	}

	// Not sure this is really ok !

	private Object restoreLocation(Object concreteObj, FieldValue fieldValue) {
		try {
			ObjTupleSet value = fieldValue.tupleSet();
			// Note sure about this
			assert value.arity() == 2;

			ImageImpl image = (ImageImpl) concreteObj;
			// Reset all the attributes
			for (ObjTuple ot : value) {
				image.setLocation((Location) ot.get(1));
			}

			return image;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Unknown error " + e.getMessage());
		}

	}

	private Object restoreStatus(Object concreteObj, FieldValue fieldValue) {
		try {
			ObjTupleSet value = fieldValue.tupleSet();
			// Note sure about this
			assert value.arity() == 2;

			ImageImpl image = (ImageImpl) concreteObj;
			// Reset all the attributes
			for (ObjTuple ot : value) {
				image.setStatus((ImageStatus) ot.get(1));
			}

			return image;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Unknown error " + e.getMessage());
		}

	}
}
