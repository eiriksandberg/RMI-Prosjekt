/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rmiprosjekt;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 *
 * @author eiriksandberg
 */
public class Tjener {
        public static void main(String[] args) throws Exception {
        try {
            LocateRegistry.createRegistry(2020);
        } catch (Exception e) {
            System.out.println("Exeption " + e);
        }
        Register register = new RegisterImpl();
        String objektnavn = "rmi://localhost:2020/register";
        Naming.rebind(objektnavn, register);
        System.out.println("RMI-objekt 1 er registrert");
        javax.swing.JOptionPane.showMessageDialog(null, "Trykk OK for å stoppe tjeneren.");
        Naming.unbind(objektnavn);
        System.exit(0);
    }
}
