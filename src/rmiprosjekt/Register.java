/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rmiprosjekt;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author eiriksandberg
 */
public interface Register extends Remote{
    public boolean transfer(double amount, Account toAccount, Account fromAccount) throws RemoteException;
    public ArrayList<Account> getAccounts(String name) throws Exception;
}
