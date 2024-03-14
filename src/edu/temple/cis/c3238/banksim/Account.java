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
    private boolean lockHeld = false;


    public Account(int id, int initialBalance, Bank bank) {
        this.id = id;
        this.balance = initialBalance;
        this.bank = bank;
    }

    public int getBalance() {
        return balance;
    }


    public synchronized boolean withdraw(int amount) {
        if (bank.isOpen() && amount <= balance) {
            int currentBalance = balance;
//             Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }

    }

    public synchronized void deposit(int amount) {
        if(!bank.isOpen()){
            return;
        }
        int currentBalance = balance;
//         Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        notify();
    }
    public synchronized  void waitForEnoughFund(int amount) {
        if(!bank.isOpen()){
            return;
        }
        while (bank.isOpen() && amount > this.balance) {
            try {
                wait();
            } catch (InterruptedException ex) {
                /* ignore */
            }
        }
        lockHeld = false;
        notifyAll();
    }


    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
}