package edu.temple.cis.c3238.banksim;

class TestThread extends Thread {

    private final Bank bank;

    public TestThread(Bank b) {
        bank = b;
    }

    @Override
    public void run() {
        bank.test();
    }
}