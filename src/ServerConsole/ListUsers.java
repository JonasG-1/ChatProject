package ServerConsole;

import ServerChat.BenutzerVerwaltung;
import ServerChat.ServerController;
import linearestrukturen.List;

public class ListUsers implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("list");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        BenutzerVerwaltung lVerwaltung = pController.gibVerwaltung();
        StringBuilder lAusgang = new StringBuilder();
        String[] lNamen = lVerwaltung.gibNamen();
        String[] lAdressen = lVerwaltung.gibAdressen();
        int lAngemeldete = lNamen.length;
        int lVerbundene = lAdressen.length;
        for (String lNaman : lNamen) {
            lAusgang.append(lNaman).append(", ");
        }
        if (lAusgang.length() > 2) {
            lAusgang.replace(lAusgang.length() - 2, lAusgang.length(), "");
        }
        if (lVerbundene == 0) {
            hatDebugger.print("Niemand ist mit dem Server verbunden.", 2);
            return true;
        }
        StringBuilder lVerbundenAusgabe = new StringBuilder(" verbundene");
        StringBuilder lAngemeldetAusgabe = new StringBuilder(" angemeldete");
        if (lVerbundene == 1) {
            lVerbundenAusgabe.append("r");
        }
        lVerbundenAusgabe.append(" Nutzer. Davon ");
        if (lAngemeldete == 1) {
            lAngemeldetAusgabe.append("r");
        }
        lAngemeldetAusgabe.append(" Nutzer.");
        hatDebugger.print(lVerbundene + lVerbundenAusgabe.toString() + lAngemeldete + lAngemeldetAusgabe, 2);
        if (lAngemeldete > 0) {
            hatDebugger.print("Liste der angemeldeten Nutzer: " + lAusgang);
        }
        return true;
    }
}
