package edu.mit.csail.sdg.squander.serializer;

import java.util.LinkedList;
import java.util.List;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.domain.Location;

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
public class HardwareSer implements IObjSer {

	public static final String ID = "id";
	public static final String LOCATION = "location";

	@Override
	public boolean accepts(Class<?> clz) {
		return Hardware.class.isAssignableFrom(clz);
	}

	@Override
	public Hardware newInstance(Class<?> cls) {
		// return new HardwareBuilder()
		// .ids("HW-" + "Default")
		// .name("default")
		// .processors(ImmutableList.of(new Processor(1, 1.0)))
		// .ram(1740)
		// .volumes(
		// ImmutableList.<Volume> of(new VolumeImpl((float) 160,
		// true, false))).build();
		throw new RuntimeException("Cannot create a new instance of Hardware !");
	}

	@Override
	public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
		System.out.println("HardwareSer.absFunc() " + obj);

		ClassSpec cls = javaScene.classSpecForObj(obj);
		List<FieldValue> result = new LinkedList<FieldValue>();

		// From the concrete object derive the abstract state (the id)
		Hardware concreteObject = (Hardware) obj;
		String id = concreteObject.getId();
		Location location = concreteObject.getLocation();

		// Store the relation ?
		FieldValue fvLen = new FieldValue(cls.findField(ID), 2);
		fvLen.addTuple(new ObjTuple(obj, id));
		result.add(fvLen);

		FieldValue locationFvLen = new FieldValue(cls.findField(LOCATION), 2);
		locationFvLen.addTuple(new ObjTuple(obj, location));
		result.add(locationFvLen);

		System.out.println("HardwareSer.absFunc() " + result);
		return result;
	}

	@Override
	public Object concrFunc(Object obj, FieldValue fieldValue) {
		System.out.println("HardwareSer.concrFunc() object " + obj);
		System.out.println("HardwareSer.concrFunc() fieldValue " + fieldValue);
		String fldName = fieldValue.jfield().name();
		// TODO Check that the object is really an Hardware ?
		if (ID.equals(fldName)) {
			return restoreID(obj, fieldValue);
		} else if (LOCATION.equals(fldName)) {
			return restoreLocation(obj, fieldValue);
		} else if (!fieldValue.jfield().isPureAbstract()) {
			return obj;
		} else {
			throw new RuntimeException(
					"Unknown field name for Hardware class: " + fldName);
		}
	}

	// Not sure this is really ok !
	private Object restoreID(Object concreteObj, FieldValue fieldValue) {
		try {
			ObjTupleSet value = fieldValue.tupleSet();

			assert value.arity() == 2;

			Hardware hardware = (Hardware) concreteObj;
			// Reset all the attributes
			HardwareBuilder builder = HardwareBuilder.fromHardware(hardware);
			for (ObjTuple ot : value) {
				builder.id(ot.get(1).toString());
			}

			return builder.build();
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

			Hardware hardware = (Hardware) concreteObj;
			// Reset all the attributes
			HardwareBuilder builder = HardwareBuilder.fromHardware(hardware);
			for (ObjTuple ot : value) {
				builder.location((Location) ot.get(1));
			}

			return builder.build();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Unknown error " + e.getMessage());
		}

	}
}
