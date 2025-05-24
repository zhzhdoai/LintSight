package org.joychou.omni.checkmark.validation.checktype;

@TypeInputAnnotation
public class TypeInput {
    private String input;
    private String noInput;
    public String getInput() {
        return input;
    }
    public void setInput(String input) {
        this.input = input;
    }
    public String getNoInput() {
        return noInput;
    }
    public void setNoInput(String noInput) {
        this.noInput = noInput;
    }
}
