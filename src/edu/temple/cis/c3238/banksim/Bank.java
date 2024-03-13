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

public class Bank {
    private final ReentrantLock bankLock = new ReentrantLock();
    private final Condition canTransfer = bankLock.newCondition();
    private final Condition canTest = bankLock.newCondition();
    private int activeTransfers = 0;
    private boolean isTesting = false;
    public static final int NTEST = 10;
    private final Account[] accounts;
    private long numTransactions = 0;
    private final int initialBalance;
    private final int numAccounts;

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(i, initialBalance);
        }
        numTransactions = 0;
    }

    public void transfer(int from, int to, int amount) {
        bankLock.lock();
        try {
            if (!accounts[from].withdraw(amount)) {
                return;
            }
            accounts[to].deposit(amount);
        } finally {
            bankLock.unlock();
        }


       // }

        // Uncomment line when ready to start Task 3.
//         if (shouldTest()Ac){
//             test();
//         }
    }

    public void test() {

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

    }

    public int getNumAccounts() {
        return numAccounts;
    }


    public boolean shouldTest() {
        return ++numTransactions % NTEST == 0;
    }

}