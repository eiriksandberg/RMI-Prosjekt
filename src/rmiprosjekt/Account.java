/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiprosjekt;

import java.io.Serializable;

/**
 *
 * @author eiriksandberg
 */
public class Account implements Serializable {

    private int accountnumber;
    private String fname;
    private String lname;
    private double balance;

    public Account(int accountnumber, String fname, String lname, double balance) {
        this.accountnumber = accountnumber;
        this.fname = fname;
        this.lname = lname;
        this.balance = balance;
    }

    public int getAccountnumber() {
        return accountnumber;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public double getBalance() {
        return balance;
    }

    public void setAccountnumber(int accountnumber) {
        this.accountnumber = accountnumber;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double newBalanceAdd(double amount) {
        this.balance = balance + amount;
        return balance;
    }

    public double newBalanceSub(double amount) {
        this.balance = balance - amount;
        return balance;
    }
}
