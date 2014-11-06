package edu.mit.csail.sdg.squander.serializer;

import java.util.LinkedList;
import java.util.List;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;

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
public class LocationSer implements IObjSer {

	public static final String ID = "id";

	@Override
	public boolean accepts(Class<?> clz) {
		return Location.class.isAssignableFrom(clz);
	}

	@Override
	public Location newInstance(Class<?> cls) {
		throw new RuntimeException("Cannot create a new instance of Location !");
	}

	@Override
	public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
		System.out.println("LocationSer.absFunc() " + obj);

		ClassSpec cls = javaScene.classSpecForObj(obj);
		List<FieldValue> result = new LinkedList<FieldValue>();

		// From the concrete object derive the abstract state (the id)
		Location concreteObject = (Location) obj;
		String id = concreteObject.getId();

		// Store the relation ?
		FieldValue fvLen = new FieldValue(cls.findField(ID), 2);
		fvLen.addTuple(new ObjTuple(obj, id));
		result.add(fvLen);
		System.out.println("LocationSer.absFunc() " + result);
		return result;
	}

	@Override
	public Object concrFunc(Object obj, FieldValue fieldValue) {
		System.out.println("LocationSer.concrFunc() object " + obj);
		System.out.println("LocationSer.concrFunc() fieldValue " + fieldValue);
		String fldName = fieldValue.jfield().name();
		// TODO Check that the object is really an Location ?
		if (ID.equals(fldName)) {
			return restoreID(obj, fieldValue);
		} else if (!fieldValue.jfield().isPureAbstract()) {
			return obj;
		} else {
			throw new RuntimeException(
					"Unknown field name for Location class: " + fldName);
		}
	}

	// Not sure this is really ok !
	private Object restoreID(Object concreteObj, FieldValue fieldValue) {
		try {
			ObjTupleSet value = fieldValue.tupleSet();

			assert value.arity() == 2;

			Location location = (Location) concreteObj;
			// Reset all the attributes
			LocationBuilder builder = LocationBuilder.fromLocation(location);
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
