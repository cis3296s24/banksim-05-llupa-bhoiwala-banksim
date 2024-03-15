package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */
public class Account {

    private volatile int balance;

    private final int id;
    private Bank bank;

    private static volatile boolean anyThreadFinished = false; //tracks if any thread has finished
    private boolean lockHeld = false;


    public Account(int id, int initialBalance, Bank bank) {
        this.id = id;
        this.balance = initialBalance;
        this.bank = bank;
    }

    public int getBalance() {
        return balance;
    }


    //Changed the withdraw method to return false if any thread has finished
    public synchronized boolean withdraw(int amount) {
        if (!bank.isOpen() || anyThreadFinished || amount > balance) {
            return false;
        }
        int currentBalance = balance;
//        Thread.yield(); // Try to force collision
        int newBalance = currentBalance - amount;
        balance = newBalance;
        return true;
    }

    //Changed the deposit method to return false if any thread has finished
    public synchronized void deposit(int amount) {
        if (!bank.isOpen() || anyThreadFinished) {
            return;
        }
        int currentBalance = balance;
//        Thread.yield(); // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        notify();
    }


    //Changed the waitForEnoughFund method to return if any thread has finished
    public synchronized void waitForEnoughFund(int amount) {
        if (!bank.isOpen() || anyThreadFinished) {
            return;
        }
        while (amount > this.balance) {
            try {
                wait();
            } catch (InterruptedException ex) {
                /* ignore */
            }
        }
    }



    //added a method that set the anyThreadFinished to true if a thread has finished
    public static void setAnyThreadFinished(boolean value) {
        anyThreadFinished = value;
    }

    //method that returns true if any thread is finished
    public static boolean isAnyThreadFinished() {
        return anyThreadFinished;
    }


    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
}