package ServerConsole;

import ServerChat.BenutzerVerwaltung;
import ServerChat.ServerController;


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
                if (pController.kickeNutzer(s)) {
                    hatDebugger.print(String.format("\"%s\" wurde gebannt.", s), 1);
                } else {
                    hatDebugger.print(String.format("\"%s\" konnte nicht gebannt werden.", s), 0);
                }
            }
            hatDebugger.print(String.format("\"%s\" wurde gebannt.", lIP), 1);
        }
        return true;
    }
}
