package com.example.perkins.myapplication;

/**
 * Created by Perkins on 2/18/2015.
 */
public class BankAccount {

    double accountBalance;

    public BankAccount() {
        accountBalance = 0;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    //42 IF NOT SYNCHRONIZED THEN CHAOS
    public synchronized  boolean  deposit(double amount){
    //public   boolean  deposit(double amount) {
        double newAccountBalance;

        if (amount < 0.0)
            return false; // can not deposit a negative amount
        else {
            newAccountBalance = accountBalance + amount;
            accountBalance = newAccountBalance;
            return true;
        }
    }

    public double getAccountBalance() {
        return accountBalance;
    }
}