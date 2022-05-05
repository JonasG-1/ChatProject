package ServerConsole;

import ServerChat.BenutzerVerwaltung;
import ServerChat.ServerController;

public class Kick implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("kick");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        String[] lNamen = pArgumente.split(",");
        BenutzerVerwaltung lVerwaltung = pController.gibVerwaltung();
        for (String lNaman : lNamen) {
            if (lVerwaltung.nameExistiert(lNaman)) {
                boolean lErfolg = pController.nutzerKicken(lVerwaltung.gibVerbindung(lNaman));
                if (!lErfolg) {
                    hatDebugger.print(String.format("\"%s\" konnte nicht entfernt werden.", lNaman), 0);
                } else {
                    hatDebugger.print(String.format("\"%s\" wurde entfernt.", lNaman), 1);
                }
            } else {
                hatDebugger.print(String.format("\"%s\" ist nicht mit dem Server verbunden.", lNaman), 0);
            }
        }
        return true;
    }
}
