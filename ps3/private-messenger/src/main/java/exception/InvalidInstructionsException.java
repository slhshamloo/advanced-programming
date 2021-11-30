package exception;

import controller.handler.InstructionHandler;

public class InvalidInstructionsException extends Exception {
    private InstructionHandler instructionHandler = null;

    public InvalidInstructionsException() {
        super();
    }

    public InvalidInstructionsException(InstructionHandler instructionHandler) {
        super();
        this.instructionHandler = instructionHandler;
    }

    public InstructionHandler getInstructionHandler() {
        return instructionHandler;
    }
}
