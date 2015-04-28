/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiprosjekt;

import java.sql.*;
import javax.sql.XADataSource;
import org.apache.derby.jdbc.ClientXADataSource;
import javax.sql.XAConnection;

/**
 *
 * @author eiriksandberg
 */
class Database {

    private XADataSource ds1;
    private XADataSource ds2;

    public Database() {
        try {
            this.ds1 = dataSource1();
            this.ds2 = dataSource2();
        } catch (Exception e) {
            System.out.println("Feil i Databasekonstruktør!" + e);
        }
    }

    public XADataSource dataSource1() throws Exception {
        String driver = "org.apache.derby.jdbc.ClientDataSource";
        ClientXADataSource ds = new ClientXADataSource();
        ds.setDatabaseName("bank1");
        ds.setServerName("localhost");
        ds.setPortNumber(1527);
        Class.forName(driver);
        return ds;
    }

    public XADataSource dataSource2() throws Exception {
        String driver = "org.apache.derby.jdbc.ClientDataSource";
        ClientXADataSource ds = new ClientXADataSource();
        ds.setDatabaseName("bank2");
        ds.setServerName("localhost");
        ds.setPortNumber(1527);
        Class.forName(driver);
        return ds;
    }

    public XADataSource getDs1() throws Exception {
        return ds1;
    }

    public XADataSource getDs2() throws Exception {
        return ds2;
    }

    public void setDs1(XADataSource ds1) throws Exception {
        this.ds1 = ds1;
    }

    public void setDs2(XADataSource ds2) throws Exception {
        this.ds2 = ds2;
    }
}
