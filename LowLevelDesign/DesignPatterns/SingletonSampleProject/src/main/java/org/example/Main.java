package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello and welcome! \n");
        System.out.printf("LazyApproach \n" );
        LazyApproach instanceLazy = LazyApproach.getLazyApproach();
        System.out.printf("LazyApproach ==> " + instanceLazy.hashCode() + "\n");


        System.out.printf("EagerApproach \n");
        EagerApproach instanceEager = EagerApproach.getEagerApproach();
        System.out.printf("EagerApproach ==> " + instanceEager.hashCode() + "\n");

    }
}