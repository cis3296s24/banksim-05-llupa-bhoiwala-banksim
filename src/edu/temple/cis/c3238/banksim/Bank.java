package edu.temple.cis.c3238.banksim;

import javax.swing.plaf.TableHeaderUI;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */

public class Bank extends Thread {
    public static final int NTEST = 10;
    private final Account[] accounts;
    private long numTransactions = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open = true;
    private boolean testing = false;
    private int transfersInAction = 0;
    private boolean bankLock = false;

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(i, initialBalance, this);
        }
        numTransactions = 0;
    }
    synchronized boolean isOpen() {return open;}
    public void closeBank() {
        synchronized (this) {
            while (transfersInAction > 0) {
                try {
                    this.wait();
                } catch (InterruptedException ex) { /* ignore */ }
            }
        }
        testing = true;
        open = false;
        for(Account account : accounts) {
            synchronized (account) {
                account.notifyAll();
            }
        }
        System.out.println("Closing the bank");
    }
    public void transfer(int from, int to, int amount) {
        if (!open) return;

        accounts[from].waitForEnoughFund(amount);

        synchronized (this) {
            while(this.testing) {
                try {
                    this.wait();
                } catch (InterruptedException ex) { /*ignore*/ }
            }
        }

        synchronized (this) {
            transfersInAction++;
        }

        //System.out.println("Wait over");
        accounts[from].withdraw(amount);
        accounts[to].deposit(amount);

        if (shouldTest()){
            Thread testingThread = new TestThread(this);
            testingThread.start();
        }

        synchronized (this) {
            transfersInAction--;
            if (transfersInAction == 0) {
                // All transactions are complete, notify waiting threads
                this.notifyAll();
            }
        }

    }
    public synchronized void test() {

        synchronized (this) {
            while (transfersInAction > 0) {
                try {
                    this.wait();
                } catch (InterruptedException ex) { /* ignore */ }
            }
        }

        testing = true;
        int totalBalance = 0;
        for (Account account : accounts) {
            System.out.printf("%-30s %s%n",
                    Thread.currentThread().toString(), account.toString());
            totalBalance += account.getBalance();
        }
        System.out.printf("%-30s Total balance: %d\n", Thread.currentThread().toString(), totalBalance);
        if (totalBalance != numAccounts * initialBalance) {
            System.out.printf("%-30s Total balance changed!\n", Thread.currentThread().toString());
            System.exit(0);
        } else {
            System.out.printf("%-30s Total balance unchanged.\n", Thread.currentThread().toString());
        }
        testing = false;
        this.notifyAll();
    }



    public int getNumAccounts() {
        return numAccounts;
    }


    public boolean shouldTest() {
        return ++numTransactions % NTEST == 0;
    }

}