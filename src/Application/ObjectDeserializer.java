package Application;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class ObjectDeserializer{
    public Object deserialize(byte[] arr, Object template) throws IllegalAccessException {
        ByteBuffer buffer = ByteBuffer.wrap(arr);

        Class<?> clazz = template.getClass();

        for(Field f: clazz.getDeclaredFields()) {
            switch (f.getType().getName()) {
                case "int": {
                    int v = buffer.getInt();
                    f.set(template, v);
                    break;
                }
                case "byte": {
                    byte v = buffer.get();
                    f.set(template, v);
                    break;
                }
                case "short": {
                    short v = buffer.getShort();
                    f.set(template, v);
                    break;
                }
                case "float": {
                    float v = buffer.getFloat();
                    f.set(template, v);
                    break;
                }
                default: {
                    System.out.printf("Skipping: %s\n", f.getType().getName());
                }
            }
        }

        return clazz;
    }
}