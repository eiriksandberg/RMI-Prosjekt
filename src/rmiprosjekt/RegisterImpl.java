/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiprosjekt;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import javax.sql.*;
import oracle.jdbc.driver.*;
import oracle.jdbc.pool.*;
import oracle.jdbc.xa.OracleXid;
import oracle.jdbc.xa.OracleXAException;
import oracle.jdbc.xa.client.*;
import javax.transaction.xa.*;

/**
 *
 * @author eiriksandberg
 */
class RegisterImpl extends UnicastRemoteObject implements Register {

    private ArrayList<Bank> banks = new ArrayList<>();
    private Database db;

    public RegisterImpl() throws RemoteException {
        this.db = new Database();
    }

    @Override
    public boolean transfer(double amount, Account toAccount, Account fromAccount) throws RemoteException {
        try {
            DriverManager.getConnection("jdbc:derby://localhost:1527/bank1", "bank1", "bank1");
            DriverManager.getConnection("jdbc:derby://localhost:1527/bank2", "bank2", "bank2");
            // Opprett forbindelse
            XAConnection forb1 = db.getDs1().getXAConnection();
            XAConnection forb2 = db.getDs2().getXAConnection();
            Connection conn1 = forb1.getConnection();
            Connection conn2 = forb2.getConnection();

            // Opprett resources
            XAResource res1 = forb1.getXAResource();
            XAResource res2 = forb2.getXAResource();

            //Oppretter xid
            Xid xid1 = createXid(1);
            Xid xid2 = createXid(2);

            // Starter resourcene
            res1.start(xid1, XAResource.TMNOFLAGS);
            res2.start(xid2, XAResource.TMNOFLAGS);

            // Utfører sql
            trekk(conn1, fromAccount, amount);
            leggTil(conn2, fromAccount, amount);

            res1.end(xid1, XAResource.TMSUCCESS);
            res2.end(xid2, XAResource.TMSUCCESS);

            // Prepare the RMs
            int prep1 = res1.prepare(xid1);
            int prep2 = res2.prepare(xid2);

            System.out.println("Returverdien til prepare: " + prep1); // Printer ut return value
            System.out.println("Returverdien til prepare: " + prep2);

            boolean do_commit = true;

            if (!((prep1 == XAResource.XA_OK) || (prep1 == XAResource.XA_RDONLY))) {
                do_commit = false;
            }

            if (!((prep2 == XAResource.XA_OK) || (prep2 == XAResource.XA_RDONLY))) {
                do_commit = false;
            }

            System.out.println("do_commit: " + do_commit);
            System.out.println("Er resource 1 det samme som resource 2? " + res1.isSameRM(res2));

            if (prep1 == XAResource.XA_OK) {
                if (do_commit) {
                    res1.commit(xid1, false);
                } else {
                    res1.rollback(xid1);
                }
            }

            if (prep2 == XAResource.XA_OK) {
                if (do_commit) {
                    res2.commit(xid2, false);
                } else {
                    res2.rollback(xid2);
                }
            }
            //Lukker forbindelsen
            conn1.close();
            conn1 = null;
            conn2.close();
            conn2 = null;
            forb1.close();
            forb1 = null;
            forb2.close();
            forb2 = null;
        } catch (Exception ex) {
            Logger.getLogger(RegisterImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    //Metode hentet fra oracles nettsider for å lage XID
    static Xid createXid(int bids) throws XAException {
        byte[] gid = new byte[1];
        gid[0] = (byte) 9;
        byte[] bid = new byte[1];
        bid[0] = (byte) bids;
        byte[] gtrid = new byte[64];
        byte[] bqual = new byte[64];
        System.arraycopy(gid, 0, gtrid, 0, 1);
        System.arraycopy(bid, 0, bqual, 0, 1);
        Xid xid = new OracleXid(0x1234, gtrid, bqual);
        return xid;
    }

    private static void trekk(Connection forb, Account fromAccount, double amount) throws SQLException {
        Statement stmt = forb.createStatement();
        int cnt = stmt.executeUpdate("update balance where accountnumber =" + fromAccount.getAccountnumber() + " set balance =" + fromAccount.newBalanceSub(amount));
        System.out.println("Ingen av radene påvirket " + cnt);
        stmt.close();
        stmt = null;
    }

    private static void leggTil(Connection forb, Account toAccount, double amount) throws SQLException {
        Statement stmt = forb.createStatement();
        int cnt = stmt.executeUpdate("update balance where accountnumber =" + toAccount.getAccountnumber() + " set balance = " + toAccount.newBalanceAdd(amount));
        System.out.println("Ingen av radene påvirket " + cnt);
        stmt.close();
        stmt = null;
    }

}
