package controller;

import connection.Sender;
import controller.annotation.Attribute;
import controller.annotation.Instruction;
import controller.annotation.Label;
import controller.handler.*;
import exception.InvalidInstructionsException;
import exception.MessengerException;
import user.Contact;
import user.Userbase;
import user.User;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Scanner;

public class Controller {
    private final Sender sender = new Sender();

    private final Userbase userbase = new Userbase();
    private User user = null;
    private int port = -1;

    private String focusIP = null;
    private int focusPort = -1;

    @SuppressWarnings({"UnusedDeclaration"})
    @Instruction(name = "userconfig", description = "instructions related to users")
    private final UserConfig userConfig = new UserConfig(this);

    @SuppressWarnings({"UnusedDeclaration"})
    @Instruction(name = "portconfig", description = "instructions related to port listening")
    private final PortConfig portConfig = new PortConfig(this);

    @SuppressWarnings({"UnusedDeclaration"})
    @Instruction(name = "contactconfig", description = "instructions related to port listening")
    private final ContactConfig contactConfig = new ContactConfig(this);

    @SuppressWarnings({"UnusedDeclaration"})
    @Instruction(name = "show", description = "instructions related to showing data")
    private final Show show = new Show(this);

    @SuppressWarnings({"UnusedDeclaration"})
    @Instruction(name = "send", description = "instructions related to showing data")
    private final Send send = new Send(this);

    @SuppressWarnings({"UnusedDeclaration"})
    @Instruction(name = "focus",
            description = "instructions related to focusing on a particular receiver for sending messages")
    private final Focus focus = new Focus(this);

    public static void printSuccess() {
        System.out.println("success");
    }

    public Userbase getUserbase() {
        return userbase;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFocusIP() {
        return focusIP;
    }

    public void setFocusIP(String focusIP) {
        this.focusIP = focusIP;
    }

    public int getFocusPort() {
        return focusPort;
    }

    public void setFocusPort(int focusPort) {
        this.focusPort = focusPort;
    }

    public Sender getSender() {
        return sender;
    }

    public void run() {
        Scanner inputStream = new Scanner(System.in);
        String input = Parser.removeExtraWhitespace(inputStream.nextLine());

        while (!input.equals("exit")) {
            try {
                Parser.parse(this, input);
            } catch (MessengerException exception) {
                System.out.println(exception.getMessage());
            } catch (InvalidInstructionsException exception) {
                handleInvalidInstructions(exception);
            } catch (NumberFormatException exception) {
                System.out.println("invalid port number");
            }
            input = Parser.removeExtraWhitespace(inputStream.nextLine());
        }
    }

    private void handleInvalidInstructions(InvalidInstructionsException exception) {
        InstructionHandler instructionHandler = exception.getInstructionHandler();
        if (instructionHandler == null) {
            System.out.println("invalid instruction type");
            printInstructions();
        } else {
            System.out.println("invalid instructions");
            printInstructionHelp(instructionHandler);
        }
    }

    public void printInstructionHelp(InstructionHandler instructionHandler) {
        try {
            StringBuilder outputBuilder = new StringBuilder();

            Field instructionHandlerField = getInstructionHandlerField(instructionHandler);
            outputBuilder.append(instructionHandlerField.getDeclaredAnnotation(Instruction.class).name())
                    .append(" (help):\n");

            for (Field field : instructionHandler.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Label.class)) {
                    Label label = field.getDeclaredAnnotation(Label.class);
                    outputBuilder.append("label ").append(label.prefix()).append(label.name()).append(" : ")
                            .append(label.description()).append("\n");
                } else if (field.isAnnotationPresent(Attribute.class)) {
                    Attribute attribute = field.getDeclaredAnnotation(Attribute.class);
                    outputBuilder.append("attribute ").append(attribute.prefix()).append(attribute.name()).append(" : ")
                            .append(attribute.description()).append("\n");
                }
            }

            System.out.print(outputBuilder);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    public Field getInstructionHandlerField(InstructionHandler instructionHandler)
            throws IllegalAccessException, NoSuchFieldException {
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (instructionHandler.equals(field.get(this))) {
                return field;
            }
        }
        throw new NoSuchFieldException();
    }

    public void printInstructions() {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("instruction types:\n");

        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Instruction instruction = field.getDeclaredAnnotation(Instruction.class);
            if (instruction != null)
                outputBuilder.append(instruction.name()).append(" : ").append(instruction.description()).append("\n");
        }

        System.out.print(outputBuilder);
    }

    public void printContact(String contactUsername) {
        Contact contact = user.getContactByUsername(contactUsername);
        if (contact == null)
            System.out.println("no contact with such username was found");
        else
            System.out.println(contact.getHostIP() + ":" + contact.getPort());
    }

    public void printCollection(Collection<?> collection) {
        if (collection.size() == 0)
            System.out.println("no item is available");
        else
            collection.forEach(System.out::println);
    }

    public void printSizeOfCollection(Collection<?> collection) {
        System.out.println(collection.size());
    }
}
