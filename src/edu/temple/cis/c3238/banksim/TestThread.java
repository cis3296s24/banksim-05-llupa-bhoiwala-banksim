package edu.temple.cis.c3238.banksim;

public class TestThread extends Thread {
    private final Bank bank;
    private volatile boolean running = true;

    public TestThread(Bank bank) {
        this.bank = bank;
    }
    @Override
    public void run() {
        while (running) {
            bank.test();
            try {
                Thread.sleep(1000); //testing every second
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }
    public void shutdown() {
        running = false;
        this.interrupt(); //making sure thread exists if sleeping
    }
}
