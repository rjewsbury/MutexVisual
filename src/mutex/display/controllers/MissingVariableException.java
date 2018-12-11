package mutex.display.controllers;

public class MissingVariableException extends RuntimeException {
    private String myVariableName;

    public MissingVariableException(String variableName) {
        myVariableName = variableName;
    }

    public String getVariableName() {
        return myVariableName;
    }
}
