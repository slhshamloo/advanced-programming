package controller.handler;

import controller.annotation.Attribute;
import controller.annotation.Label;
import exception.InvalidInstructionsException;
import exception.MessengerException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class InstructionHandler {
    public void handle() throws InvalidInstructionsException, MessengerException {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (isRequiredArgument(field)) {
                    Method method = this.getClass().getDeclaredMethod(field.getName());
                    method.setAccessible(true);
                    method.invoke(this);
                    break;
                }
            } catch (IllegalAccessException | NoSuchMethodException exception) {
                exception.printStackTrace();
            } catch (InvocationTargetException exception) {
                Throwable cause = exception.getCause();
                if (cause instanceof InvalidInstructionsException)
                    throw (InvalidInstructionsException) cause;
                else if (cause instanceof MessengerException)
                    throw (MessengerException) cause;
            }
        }
    }

    private boolean isRequiredArgument(Field field) throws IllegalAccessException {
        return (field.isAnnotationPresent(Label.class)
                && field.getDeclaredAnnotation(Label.class).required()
                && field.getBoolean(this))
                || (field.isAnnotationPresent(Attribute.class)
                && field.getDeclaredAnnotation(Attribute.class).standalone()
                && field.get(this) != null);
    }
}
