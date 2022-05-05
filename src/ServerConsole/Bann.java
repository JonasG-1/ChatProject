package ServerConsole;

import ServerChat.BenutzerVerwaltung;
import ServerChat.ServerController;

public class Bann implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("ban");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        String[] lNamen = pArgumente.split(",");
        BenutzerVerwaltung lVerwaltung = pController.gibVerwaltung();
        for (String lName : lNamen) {
            if (lVerwaltung.nameExistiert(lName)) {
                lVerwaltung.bannHinzufuegen(pController.gibIP(lVerwaltung.gibVerbindung(lName)));
                boolean lErfolg = pController.nutzerKicken(lVerwaltung.gibVerbindung(lName));
                if (!lErfolg) {
                    hatDebugger.print(String.format("\"%s\" konnte nicht gebannt werden.", lName), 0);
                } else {
                    hatDebugger.print(String.format("\"%s\" wurde gebannt.", lName), 1);
                }
            } else {
                hatDebugger.print(String.format("\"%s\" ist nicht mit dem Server verbunden.", lName), 0);
            }
        }
        return true;
    }
}
