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
        for (String lName : lNamen) {
            if (lVerwaltung.nameExistiert(lName)) {
                boolean lErfolg = false;
                try {
                    lErfolg = pController.nutzerKicken(lVerwaltung.gibAdresse(lName));
                } catch (NoSuchFieldException lFehler) {
                    lFehler.printStackTrace();
                }
                if (!lErfolg) {
                    hatDebugger.print(String.format("\"%s\" konnte nicht entfernt werden.", lName), 0);
                } else {
                    hatDebugger.print(String.format("\"%s\" wurde entfernt.", lName), 1);
                }
            } else {
                hatDebugger.print(String.format("\"%s\" ist nicht mit dem Server verbunden.", lName), 0);
            }
        }
        return true;
    }
}
