package org.jclouds.compute.domain;

import java.util.LinkedList;
import java.util.List;

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
		// TODO Cannot create instances out of the blue right now
		throw new RuntimeException("cannot create a new Image");
	}

	@Override
	public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
		System.out.println("ImageSer.absFunc() START");
		ClassSpec cls = javaScene.classSpecForObj(obj);
		List<FieldValue> result = new LinkedList<FieldValue>();

		// TODO Not sure if this is valid
		Image concreteObject = (Image) obj;
		String imageID = concreteObject.getId();

		FieldValue fvLen = new FieldValue(cls.findField(ID), 2);
		fvLen.addTuple(new ObjTuple(obj, imageID));
		result.add(fvLen);
		System.out.println("ImageSer.absFunc() END");
		return result;
	}

	@Override
	public Object concrFunc(Object obj, FieldValue fieldValue) {
		System.out.println("ImageSer.concrFunc() START");
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
			// Overwrite the id
			System.out.println("ImageSer.restoreImageID() value.tuples "
					+ value.tuples());

			for (ObjTuple ot : value) {
				System.out
						.println("ImageSer.restoreImageID() ot.get(1).toString() "
								+ ot.get(1).toString());
				builder.id(ot.get(1).toString());
			}

			return builder.build();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Unknown error " + e.getMessage());
		}

	}
}
