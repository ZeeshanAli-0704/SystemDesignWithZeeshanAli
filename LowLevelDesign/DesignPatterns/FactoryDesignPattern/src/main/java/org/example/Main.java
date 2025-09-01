package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        EmployeeFactoryClass empFactory = new EmployeeFactoryClass();
        Employee emp = empFactory.getEmployee("WEB DEVELOPER");
        System.out.println(emp.getEmployeeSalary());
    }
}