package ServerConsole;

import ServerChat.BenutzerVerwaltung;
import ServerChat.ServerController;
import linearestrukturen.List;


public class BannIP implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("ban-ip");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        String[] lIPs = pArgumente.split(",");
        BenutzerVerwaltung lVerwaltung = pController.gibVerwaltung();
        for (String lIP : lIPs) {
            if (lVerwaltung.istGebannt(lIP)) {
                hatDebugger.print(String.format("\"%s\" ist bereits gebannt.", lIP), 0);
                return true;
            }
            lVerwaltung.bannHinzufuegen(lIP);
            String[] lListe = lVerwaltung.gibAdressenMitIP(lIP);
            for (String s : lListe) {
                pController.nutzerKicken(s);
            }
            hatDebugger.print(String.format("\"%s\" wurde gebannt.", lIP), 1);
        }
        return true;
    }
}
