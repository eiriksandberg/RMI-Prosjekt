package rmiprosjekt;

import java.rmi.*;
import static javax.swing.JOptionPane.*;

public class Klient {

    public static void main(String[] args) throws Exception {
// Dersom rmi-registeret og tjenerprogrammet kjører på en annen maskin,
// må IP-adressen eller
// maskinnavnet settes inn i stedet for localhost på neste linje.
        Register register = (Register) Naming.lookup("rmi://localhost:2020/register");
        String lbInn = showInputDialog("Endre lagerbeholdning");
        while (lbInn != null) {
            int lb = Integer.parseInt(lbInn);
            register.endreLagerbeholdning(0, lb);
            System.out.println(register.lagDatabeskrivelse());
            lbInn = showInputDialog("Endre lagerbeholdning");
        }
        System.out.println(register.lagBestillingsliste());
    }
}
