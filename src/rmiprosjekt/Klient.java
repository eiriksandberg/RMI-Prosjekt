package rmiprosjekt;

import java.rmi.*;
import static javax.swing.JOptionPane.*;

public class Klient {

    public static void main(String[] args) throws Exception {
// Dersom rmi-registeret og tjenerprogrammet kjører på en annen maskin,
// må IP-adressen eller
// maskinnavnet settes inn i stedet for localhost på neste linje.
        Register register = (Register) Naming.lookup("rmi://localhost:2020/register");
        Account konto1 = new Account(1, "Harald", "Eriksen", 100000);
        Account konto2 = new Account(1, "Sigurd", "Hansen", 80000);
        String lbInn = showInputDialog("Overfør penger");
        while (lbInn != null) {
            double lb = Double.parseDouble(lbInn);
            register.transfer(lb, konto1, konto2);
        }
    }
}
