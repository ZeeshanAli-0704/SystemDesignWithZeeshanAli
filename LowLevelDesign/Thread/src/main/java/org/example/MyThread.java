package org.example;

public class MyThread extends Thread{

    @Override
    public void run(){
        for(int i =0; i<5; i++){
            System.out.println("Hi Thread " + Thread.currentThread().threadId());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        MyThread th1 = new MyThread();
        MyThread th2 = new MyThread();
        th1.start();
        th2.start();

    }
}
