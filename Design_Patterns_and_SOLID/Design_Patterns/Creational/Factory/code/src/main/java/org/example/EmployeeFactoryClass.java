package org.example;

public class EmployeeFactoryClass {

    public Employee getEmployee(String type) {
        String TYPE = type.trim().toUpperCase(); // normalize input

        if (TYPE.equals("WEB DEVELOPER")) {
            return new WebDeveloper();
        } else if (TYPE.equals("ANDROID DEVELOPER")) {
            return new AndroidDeveloper();
        }

        // Could throw exception or return null if type is unknown
        throw new IllegalArgumentException("Unknown employee type: " + type);
    }
}
