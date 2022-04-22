package Application;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * This class allows for easy deserialization of byte array into an object.
 *
 * Supported: All generic types; complex and nested, non-protected objects (Integer is NOT supported); arrays of primitive objects
 *  <br>
 * <br>Example of valid data type:<br>
 * <pre>{@code
 * public class TestClass {
 *	 int abcd;
 *
 *	 final TestClass2 xyz = new TestClass2();
 *
 *	 public static class TestClass2 {
 *		 char arr1[] = new char[2];
 *		 short[] xyz; //note
 *	 }
 * }
 * }</pre>
 * @apiNote Uninitialized array will be filled greedly. no data can follow.
 * @implNote Uses 8-bit (1 byte) characters for compatibility with arduino!!!!
 */
class GenericObjectDeserializer {
	/**
	 * Primary deserialization function. Works in-place
	 * @param arr byte array to deserialize
	 * @param template instance of the desired object
	 * @throws IllegalAccessException when cannot access one of the fileds of the object
	 * @throws BufferUnderflowException when there's not enough data in the array
	 */
	public static void deserialize(byte[] arr, Object template) throws IllegalAccessException, BufferUnderflowException {
		ByteBuffer buffer = ByteBuffer.wrap(arr);

		_deserialize(buffer, template);
	}

	/**
	 * Deserializes and returns an array of primitive types (byte, int...)
	 * @param buff instance of {@link java.nio.ByteBuffer} with data
	 * @param n size of resulting array. -1 will consume all available data
	 * @param type class representing the primitive type
	 * @return the array
	 * @throws BufferUnderflowException if not enough data is present in the bytebuffer
	 */
	static Object deserializePrimitiveArray(ByteBuffer buff, int n, Class<?> type) throws BufferUnderflowException {
		if(Objects.isNull(type)) return null;
		if(!type.isPrimitive()) return null;

		if(n == -1){
			int size = switch (type.getTypeName()) {
				case "byte" -> Byte.SIZE;
				case "char" -> 8; //c-compatibility
				case "boolean" -> 8; //c-compatibility
				case "short" -> Short.SIZE;
				case "int" -> Integer.SIZE;
				case "long" -> Long.SIZE;
				case "float" -> Float.SIZE;
				case "double" -> Double.SIZE;
				default -> 0;
			} / Byte.SIZE;
			n = buff.remaining() / size;
		}

		Object arr = Array.newInstance(type, n);

		for(int i = 0; i < n; i++) {
			switch (type.getTypeName()) {
				//Primitive types
				case "byte" -> Array.setByte(arr, i, buff.get());
				case "char" -> Array.setChar(arr,i , (char)buff.get()); //c-compatibility
				case "boolean" -> Array.setBoolean(arr, i, buff.get() != 0); //c-compatibility
				case "short" -> Array.setShort(arr, i, buff.getShort());
				case "int" -> Array.setInt(arr, i, buff.getInt());
				case "long" -> Array.setLong(arr, i, buff.getLong());
				case "float" -> Array.setFloat(arr, i, buff.getFloat());
				case "double" -> Array.setDouble(arr, i, buff.getDouble());
			}
		}

		return arr;
	}

	private static void _deserialize(ByteBuffer buff, Object template) throws IllegalAccessException, BufferUnderflowException {
		Class<?> clazz = template.getClass();

		for(Field f: clazz.getDeclaredFields()) {
			//Skip static or private fields
			if(Modifier.isStatic(f.getModifiers()) || Modifier.isPrivate(f.getModifiers())) continue;
			//Make final fields accessible
			if(Modifier.isFinal(f.getModifiers())) f.setAccessible(true);

			Class<?> type = f.getType();

			if(type.isPrimitive()) {
				switch (type.getTypeName()) {
					case "byte" -> f.setByte(template, buff.get());
					case "char" -> f.set(template, buff.get()); //c-compatible
					case "boolean" -> f.setBoolean(template, buff.get() != 0); //c-compatible
					case "short" -> f.setShort(template, buff.getShort());
					case "int" -> f.setInt(template, buff.getInt());
					case "long" -> f.setLong(template, buff.getLong());
					case "float" -> f.setFloat(template, buff.getFloat());
					case "double" -> f.setDouble(template, buff.getDouble());
				}
			} else if(type.isArray()) {
				Class<?> atype = type.getComponentType();

				if(!atype.isPrimitive())
					throw new RuntimeException("Cannot deserialize non-primitive(" +  atype.getTypeName() + ") arrays!");

				int n = -1;
				if(Objects.nonNull(f.get(template))) n = Array.getLength(f.get(template));
				f.set(template, deserializePrimitiveArray(buff, n, atype));
			} else {
				//Some object, lets, try to recurse down
				_deserialize(buff, f.get(template));
			}
		}
	}
}

