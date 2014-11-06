package edu.mit.csail.sdg.squander.serializer;

import java.util.LinkedList;
import java.util.List;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageStatus;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.serializer.special.IObjSer;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * A first trial in creating Squander object serializer for existing Classes
 * 
 * @author alessiogambi
 *
 */
public class ImageSer implements IObjSer {

	public static final String ID = "id";

	@Override
	public boolean accepts(Class<?> clz) {
		return Image.class.isAssignableFrom(clz);
	}

	@Override
	public Image newInstance(Class<?> cls) {
		// Create a default image
		Image image = new ImageBuilder()
				.ids("Default IMAGE")
				.name("Default IMAGE")
				.location(null)
				.operatingSystem(
						new OperatingSystem(OsFamily.LINUX, "desc", "version",
								null, "desc", false)).description("desc")
				.status(ImageStatus.AVAILABLE).build();
		return image;
	}

	@Override
	public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
		System.out.println("ImageSer.absFunc() " + obj);
		ClassSpec cls = javaScene.classSpecForObj(obj);
		List<FieldValue> result = new LinkedList<FieldValue>();

		// From the concrete object derive the abstract state (the id)
		Image concreteObject = (Image) obj;
		String id = concreteObject.getId();

		// Store the relation ?
		FieldValue fvLen = new FieldValue(cls.findField(ID), 2);
		fvLen.addTuple(new ObjTuple(obj, id));
		result.add(fvLen);
		System.out.println("ImageSer.absFunc() " + result);
		return result;
	}

	@Override
	public Object concrFunc(Object obj, FieldValue fieldValue) {
		System.out.println("ImageSer.concrFunc() object " + obj);
		System.out.println("ImageSer.concrFunc() fieldValue " + fieldValue);
		String fldName = fieldValue.jfield().name();
		// TODO Check that the object is really an Image ?
		if (ID.equals(fldName)) {
			return restoreImageID(obj, fieldValue);
		} else if (!fieldValue.jfield().isPureAbstract()) {
			return obj;
		} else {
			throw new RuntimeException("Unknown field name for Image class: "
					+ fldName);
		}
	}

	// Not sure this is really ok !
	private Object restoreImageID(Object concreteObj, FieldValue fieldValue) {
		try {
			ObjTupleSet value = fieldValue.tupleSet();

			assert value.arity() == 2;

			Image image = (Image) concreteObj;
			// Reset all the attributes
			ImageBuilder builder = ImageBuilder.fromImage(image);
			for (ObjTuple ot : value) {
				builder.id(ot.get(1).toString());
			}

			return builder.build();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Unknown error " + e.getMessage());
		}

	}
}
