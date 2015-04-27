/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmiprosjekt;

import java.sql.*;
import javax.sql.*;
import javax.sql.XADataSource;
import org.apache.commons.dbcp.BasicDataSource;
import oracle.jdbc.driver.*;
import oracle.jdbc.pool.*;
import oracle.jdbc.xa.OracleXid;
import oracle.jdbc.xa.OracleXAException;
import oracle.jdbc.xa.client.*;

/**
 *
 * @author eiriksandberg
 */
class Database {
    private XADataSource ds1;
    private XADataSource ds2;
    
    public Database(){
        try{
        this.ds1 = dataSource1();
        this.ds2 = dataSource2();
        DriverManager.registerDriver(new OracleDriver());
        } catch(Exception e){
            System.out.println("Feil i Databasekonstruktør!" + e);
        }
    }

    public XADataSource dataSource1() throws Exception {
        String url = "jdbc:derby://localhost:1527/bank1";
        String username = "bank1";
        String password = "bank1";
        OracleXADataSource ds = new OracleXADataSource();
        ds.setURL(url);
        ds.setUser(username);
        ds.setPassword(password);
        return ds;
    }

    public XADataSource dataSource2() throws Exception {
        String url = "jdbc:derby://localhost:1527/bank2";
        String username = "bank2";
        String password = "bank2";
        OracleXADataSource ds = new OracleXADataSource();
        ds.setURL(url);
        ds.setUser(username);
        ds.setPassword(password);
        return ds;
    }

    public XADataSource getDs1() throws Exception{
        return ds1;
    }

    public XADataSource getDs2() throws Exception{
        return ds2;
    }

    public void setDs1(XADataSource ds1) throws Exception{
        this.ds1 = ds1;
    }

    public void setDs2(XADataSource ds2) throws Exception{
        this.ds2 = ds2;
    }
    
}
