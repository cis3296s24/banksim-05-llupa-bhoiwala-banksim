package edu.temple.cis.c3238.banksim;

public class TestThread extends Thread {
    private final Bank bank;
    private volatile boolean running = true;
    private final long testInterval;

    public TestThread(Bank bank, long testInterval) {
        this.bank = bank;
        this.testInterval = testInterval;
    }
    @Override
    public void run() {
        while (running) {
            bank.test();
            try {
                Thread.sleep(testInterval); //testing every second
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
