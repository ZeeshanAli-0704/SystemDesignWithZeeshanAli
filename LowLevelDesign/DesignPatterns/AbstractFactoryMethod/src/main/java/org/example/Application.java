package org.example;

public class Application {
    private Button button;
    private Checkbox checkbox;

    public Application(ComponentFactory factory){
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void render(){
        button.paint();
        checkbox.paint();
    }
}
