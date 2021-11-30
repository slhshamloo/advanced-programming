import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Agent {
    public List<String> getMethodNames(Object object) {
        return Arrays.stream(object.getClass().getDeclaredMethods()).map(Method::getName).collect(Collectors.toList());
    }

    public Object getFieldContent(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        if (Modifier.isStatic(field.getModifiers()))
            return field.get(null);
        else
            return field.get(object);
    }

    public void setFieldContent(Object object, String fieldName, Object content)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");

        field.setAccessible(true);
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        try {
            if (Modifier.isStatic(field.getModifiers()))
                field.set(null, content);
            else
                field.set(object, content);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public Object call(Object object, String methodName, Object[] parameters)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = object.getClass().getDeclaredMethod(methodName,
                Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new));
        method.setAccessible(true);
        try {
            return method.invoke(object, parameters);
        } catch (IllegalArgumentException exception) {
            throw new NoSuchMethodException(exception.getMessage());
        }
    }

    public Object createANewObject(String fullClassName, Object[] initials)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?>[] initialsClasses = Arrays.stream(initials).map(Object::getClass).toArray(Class[]::new);
        Constructor<?> constructor = Class.forName(fullClassName).getConstructor(initialsClasses);
        return constructor.newInstance(initials);
    }

    public String debrief(Object object) {
        StringBuilder debriefBuilder = new StringBuilder();

        debriefBuilder.append("Name: ").append(object.getClass().getSimpleName())
                .append("\nPackage: ").append(object.getClass().getPackage().getName())
                .append("\nNo. of Constructors: ").append(object.getClass().getDeclaredConstructors().length);

        debriefBuilder.append("\n===\nFields:");
        appendFieldsStringToBuilder(object, debriefBuilder);

        debriefBuilder.append("\n===\nMethods:");
        appendMethodsStringToBuilder(object, debriefBuilder);

        return debriefBuilder.toString();
    }

    private void appendFieldsStringToBuilder(Object object, StringBuilder debriefBuilder) {
        Field[] fields = object.getClass().getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));

        for (Field field : fields) {
            field.setAccessible(true);
            debriefBuilder.append("\n")
                    .append(Modifier.toString(field.getModifiers())).append(" ")
                    .append(field.getType().getSimpleName()).append(" ")
                    .append(field.getName());
        }
        debriefBuilder.append("\n(").append(fields.length).append(" fields)");
    }

    private void appendMethodsStringToBuilder(Object object, StringBuilder debriefBuilder) {
        Method[] methods = object.getClass().getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));

        for (Method method : methods) {
            method.setAccessible(true);
            debriefBuilder.append("\n")
                    .append(method.getReturnType().getSimpleName()).append(" ")
                    .append(method.getName()).append("(");

            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length - 1; i++)
                debriefBuilder.append(parameterTypes[i].getSimpleName()).append(", ");
            if (parameterTypes.length > 0)
                debriefBuilder.append(parameterTypes[parameterTypes.length - 1].getSimpleName());
            debriefBuilder.append(")");
        }
        debriefBuilder.append("\n(").append(methods.length).append(" methods)");
    }

    public Object clone(Object toClone)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Class<?> klass = toClone.getClass();
        Object cloned = getNewInstanceWithNoArgConstructor(klass);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);

        while (klass != null) {
            for (Field field : klass.getDeclaredFields())
                cloneField(field, modifiersField, toClone, cloned);
            klass = klass.getSuperclass();
        }

        return cloned;
    }

    private Object getNewInstanceWithNoArgConstructor(Class<?> klass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            Constructor<?> constructor = klass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException exception) {
            return klass.newInstance();
        }
    }

    private void cloneField(Field field, Field modifiersField, Object toClone, Object cloned)
            throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, InstantiationException {
        field.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        if (!Modifier.isStatic(field.getModifiers())) {
            if (isNotCloneable(field.getType()))
                field.set(cloned, field.get(toClone));
            else if (field.getType().isArray()) {
                cloneArray(field, toClone, cloned);
            } else {
                Object attribute = field.get(toClone);
                if (attribute == toClone)
                    field.set(cloned, cloned);
                else if (attribute != null)
                    field.set(cloned, clone(attribute));
            }
        }
    }

    private boolean isNotCloneable(Class<?> type) {
        return type.isPrimitive()
                || type.equals(String.class)
                || (type.getSuperclass() != null
                && type.getSuperclass().equals(Number.class))
                || type.equals(Boolean.class)
                || type.equals(Type.class)
                || type.isEnum();
    }

    private void cloneArray(Field arrayField, Object toClone, Object cloned)
            throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, InstantiationException {
        Object toCloneArray = arrayField.get(toClone);
        int arrayLength = Array.getLength(toCloneArray);
        arrayField.set(cloned, Array.newInstance(arrayField.getType().getComponentType(), arrayLength));
        Object clonedArray = arrayField.get(cloned);

        if (isNotCloneable(arrayField.getType().getComponentType()))
            for (int i = 0; i < arrayLength; i++)
                Array.set(clonedArray, i, Array.get(toCloneArray, i));
        else
            for (int i = 0; i < arrayLength; i++)
                Array.set(clonedArray, i, clone(Array.get(toCloneArray, i)));
    }
}
