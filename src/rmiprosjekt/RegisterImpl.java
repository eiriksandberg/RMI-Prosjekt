/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiprosjekt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.sql.*;
import javax.sql.DataSource;
import javax.transaction.xa.*;
import org.apache.derby.jdbc.ClientXADataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author eiriksandberg
 */
class RegisterImpl extends UnicastRemoteObject implements Register {

    private ArrayList<Bank> banks = new ArrayList<>();
    private Database db;
    private long txGenerator;

    public RegisterImpl() throws RemoteException {
        this.db = new Database();
    }

    @Override
    public boolean transfer(double amount, Account toAccount, Account fromAccount) throws RemoteException {
        try {
            // Prøver å etablere fysiske forbindelser til ressursene
            XAConnection c1 = db.dataSource1().getXAConnection("bank1", "bank1");
            XAConnection c2 = db.dataSource2().getXAConnection("bank2", "bank2");
            // De faktiske forbindelsene 
            Connection conn1 = c1.getConnection();
            Connection conn2 = c2.getConnection();
            // Ressursene som er involvert
            XAResource res1 = c1.getXAResource();
            XAResource res2 = c2.getXAResource();
            // Lager xid
            Xid xid1 = createXid(1);
            Xid xid2 = createXid(2);
            //Starter ressursene
            res1.start(xid1, XAResource.TMNOFLAGS);
            res2.start(xid2, XAResource.TMNOFLAGS);
            // Utfører sql setningene lokalt
            trekk(conn1, fromAccount, amount);
            leggTil(conn2, toAccount, amount);
            // Avslutter ressursene
            res1.end(xid1, XAResource.TMSUCCESS);
            res2.end(xid2, XAResource.TMSUCCESS);
            //Entrer preparephase
            int prp1 = res1.prepare(xid1);
            int prp2 = res2.prepare(xid2);
            write(("Return value of prepare 1 is " + prp1));
            write(("Return value of prepare 2 is " + prp2));
            
            
            boolean do_commit = true;
            if (!((prp1 == XAResource.XA_OK) || (prp1 == XAResource.XA_RDONLY))) {
                do_commit = false;
            }
            if (!((prp2 == XAResource.XA_OK) || (prp2 == XAResource.XA_RDONLY))) {
                do_commit = false;
            }
            
            write(("do_commit is " + do_commit));
            write(("Er resource 1 den samme som resource 2?" + res1.isSameRM(res2)));

            if (prp1 == XAResource.XA_OK) {
                if (do_commit) {
                    res1.commit(xid1, false);
                } else {
                    res1.rollback(xid1);
                }
            }
            if (prp2 == XAResource.XA_OK) {
                if (do_commit) {
                    res2.commit(xid2, false);
                } else {
                    res2.rollback(xid2);
                }
            }
            // Stenger forbindelsen
            conn1.close();
            conn1 = null;
            conn2.close();
            conn2 = null;
            c1.close();
            c1 = null;
            c2.close();
            c2 = null;
        } catch (Exception e) {
            write(("FEIL I TRANSFER!!! " + e));
        }
        return true;
    }

    static Xid createXid(int bids)
            throws XAException {
        final byte[] gid = new byte[1];
        gid[0] = (byte) 9;
        byte[] bid = new byte[1];
        bid[0] = (byte) bids;
        byte[] gtrid = new byte[64];
        final byte[] bqual = new byte[64];
        System.arraycopy(gid, 0, gtrid, 0, 1);
        System.arraycopy(bid, 0, bqual, 0, 1);
        return new Xid() {
            public int getFormatId() {
                return 0x1234;
            }
            public byte[] getGlobalTransactionId() {
                return gid;
            }
            public byte[] getBranchQualifier() {
                return bqual;
            }
        };
    }
                

    private static void trekk(Connection forb, Account fromAccount, double amount) throws SQLException {
        Statement stmt = forb.createStatement();
        int cnt = stmt.executeUpdate("update accounts set balance =" + fromAccount.newBalanceSub(amount) + " where accountnumber =" + fromAccount.getAccountnumber());
        write(("Ingen av radene påvirket" + cnt));
        stmt.close();
        stmt = null;
    }

    private static void leggTil(Connection forb, Account toAccount, double amount) throws SQLException {
        Statement stmt = forb.createStatement();
        int cnt = stmt.executeUpdate("update accounts set balance = " + toAccount.newBalanceAdd(amount) + " where accountnumber =" + toAccount.getAccountnumber());
        write(("Ingen av radene påvirket" + cnt));
        stmt.close();
        stmt = null;
    }
    

     public static void write(String txt) {
        try {
            System.out.println(txt);
            File file = new File("src/rmiprosjekt/log.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(txt);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
     public ArrayList<Account> getAccounts(String name) throws Exception{
         name = name.toLowerCase();
         XAConnection c = null;
         if(name == "bank1"){
            c = db.getDs1().getXAConnection("bank1", "bank1");
         }
         if(name == "bank2"){
             c = db.getDs2().getXAConnection("bank2", "bank2");
         }
         if(c != null){
         Connection conn1 = c.getConnection();
         Statement stmt = conn1.createStatement();
         stmt.execute("select * from accounts");
         ResultSet res = stmt.getResultSet();
         ArrayList <Account> a = new ArrayList<>();
         while(res.next()){
             String fname = res.getString("fname");
             String lname = res.getString("lname");
             int an = res.getInt("accountnumber");
             double balance = res.getDouble("balance");
             a.add(new Account(an, fname, lname, balance));
         }
         stmt.close();
         return a;
         }
         return null;
     }
}
