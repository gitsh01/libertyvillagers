package com.gitsh01.libertyvillagers;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static Object getPrivateField(Object instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access private field: " + fieldName, e);
        }
    }
}
