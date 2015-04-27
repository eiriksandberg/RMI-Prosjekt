/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rmiprosjekt;

import java.util.ArrayList;

/**
 *
 * @author eiriksandberg
 */
public class Bank {
    private String name;
    private ArrayList<Account> accounts = new ArrayList<>();

    public Bank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}
