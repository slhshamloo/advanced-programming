package controller;

import controller.annotation.Attribute;
import controller.annotation.Instruction;
import controller.annotation.Label;
import controller.handler.InstructionHandler;
import exception.InvalidInstructionsException;
import exception.MessengerException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Parser {
    public static String removeExtraWhitespace(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }

    public static void parse(Object object, String input) throws MessengerException, InvalidInstructionsException {
        List<String> splitInput = new ArrayList<>(Arrays.asList(input.split(" ")));
        String handlerName = splitInput.get(0);

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            InstructionHandler instructionHandler = getHandler(object, field, handlerName);
            if (instructionHandler != null) {
                splitInput.remove(0);
                try {
                    parseInstructions(instructionHandler, splitInput);
                } catch (InvalidInstructionsException | MessengerException exception) {
                    resetFields(instructionHandler);
                    throw exception;
                }
                return;
            }
        }
        throw new InvalidInstructionsException();
    }

    public static InstructionHandler getHandler(Object object, Field field, String handlerName) {
        Instruction instructionAnnotation = field.getDeclaredAnnotation(Instruction.class);
        if (instructionAnnotation != null
                && instructionAnnotation.name().equals(handlerName)) {
            try {
                return (InstructionHandler) field.get(object);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static void parseInstructions(InstructionHandler instructionHandler, List<String> instructions)
            throws MessengerException, InvalidInstructionsException {
        AtomicBoolean setRequiredArgument = new AtomicBoolean(false);

        for (Field field : instructionHandler.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(Label.class))
                    parseLabel(instructionHandler, field, instructions, setRequiredArgument);
                else if (field.isAnnotationPresent(Attribute.class))
                    parseAttribute(instructionHandler, field, instructions, setRequiredArgument);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
        if (instructions.size() > 0
                || (hasRequiredArgument(instructionHandler)
                && !setRequiredArgument.get()))
            throw new InvalidInstructionsException(instructionHandler);
        instructionHandler.handle();
        resetFields(instructionHandler);
    }

    private static void parseLabel(InstructionHandler instructionHandler, Field field, List<String> instructions,
            AtomicBoolean setRequiredArgument) throws InvalidInstructionsException, IllegalAccessException {
        Label label = field.getDeclaredAnnotation(Label.class);

        for (Iterator<String> iterator = instructions.iterator(); iterator.hasNext();) {
            String segment = iterator.next();
            if (segment.startsWith(label.prefix())
                    && label.name().equals(segment.substring(label.prefix().length()))) {
                if (label.required()) {
                    if (setRequiredArgument.get())
                        throw new InvalidInstructionsException(instructionHandler);
                    else
                        setRequiredArgument.set(true);
                }
                iterator.remove();
                field.setBoolean(instructionHandler, true);
            }
        }
    }

    private static void parseAttribute(InstructionHandler instructionHandler, Field field, List<String> instructions,
            AtomicBoolean setRequiredArgument) throws InvalidInstructionsException, IllegalAccessException {
        Attribute attribute = field.getDeclaredAnnotation(Attribute.class);

        for (Iterator<String> iterator = instructions.iterator(); iterator.hasNext();) {
            String segment = iterator.next();
            if (segment.startsWith(attribute.prefix())
                    && attribute.name().equals(segment.substring(attribute.prefix().length()))) {
                if (attribute.standalone()) {
                    if (setRequiredArgument.get())
                        throw new InvalidInstructionsException(instructionHandler);
                    else
                        setRequiredArgument.set(true);
                }
                iterator.remove();
                try {
                    field.set(instructionHandler, getArgument(iterator));
                } catch (NoSuchElementException exception) {
                    throw new InvalidInstructionsException(instructionHandler);
                }
            }
        }
    }

    private static String getArgument(Iterator<String> iterator) {
        String segment = iterator.next();

        if (segment.startsWith("\"")) {
            List<String> argumentSegments = new ArrayList<>();

            segment = segment.substring(1);
            while (!segment.endsWith("\"")) {
                iterator.remove();
                argumentSegments.add(segment);
                segment = iterator.next();
            }

            segment = segment.substring(0, segment.length() - 1);
            iterator.remove();
            argumentSegments.add(segment);

            return String.join(" ", argumentSegments);
        } else {
            iterator.remove();
            return segment;
        }
    }

    public static boolean hasRequiredArgument(InstructionHandler instructionHandler) {
        for (Field field : instructionHandler.getClass().getDeclaredFields()) {
            if ((field.isAnnotationPresent(Label.class)
                    && field.getDeclaredAnnotation(Label.class).required())
                    || (field.isAnnotationPresent(Attribute.class)
                    && field.getDeclaredAnnotation(Attribute.class).standalone()))
                return true;
        }
        return false;
    }

    public static void resetFields(InstructionHandler instructionHandler) {
        for (Field field : instructionHandler.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(Label.class))
                    field.setBoolean(instructionHandler, false);
                else if (field.isAnnotationPresent(Attribute.class))
                    field.set(instructionHandler, null);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }
}
