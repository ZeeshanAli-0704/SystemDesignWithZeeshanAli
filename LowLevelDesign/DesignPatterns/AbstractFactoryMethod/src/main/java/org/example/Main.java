package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ComponentFactory factory;
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("mac")){
            factory = new MacFactory();
        }else{
            factory = new WindowFactory();
        }

        Application app = new Application(factory);
        app.render();
    }
}