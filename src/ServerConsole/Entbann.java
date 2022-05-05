package ServerConsole;

import ServerChat.BenutzerVerwaltung;
import ServerChat.ServerController;

public class Entbann implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("unban");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        String[] lIPs = pArgumente.split(",");
        BenutzerVerwaltung lVerwaltung = pController.gibVerwaltung();
        for (String lIP : lIPs) {
            if (lVerwaltung.istGebannt(lIP)) {
                lVerwaltung.bannEntfernen(lIP);
                hatDebugger.print(String.format("\"%s\" wurde entbannt.", lIP), 1);
            } else {
                hatDebugger.print(String.format("\"%s\" ist nicht gebannt.", lIP), 0);
            }
        }
        return true;
    }
}
