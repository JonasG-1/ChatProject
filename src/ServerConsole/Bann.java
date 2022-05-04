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
        for (String lNaman : lNamen) {
            if (lVerwaltung.nameExistiert(lNaman)) {
                lVerwaltung.bannHinzufuegen(pController.gibIP(lVerwaltung.gibVerbindung(lNaman)));
                boolean lErfolg = pController.nutzerKicken(lVerwaltung.gibVerbindung(lNaman));
                if (!lErfolg) {
                    hatDebug.print(String.format("\"%s\" konnte nicht gebannt werden.", lNaman), 0);
                } else {
                    hatDebug.print(String.format("\"%s\" wurde gebannt.", lNaman), 1);
                }
            } else {
                hatDebug.print(String.format("\"%s\" ist nicht mit dem Server verbunden.", lNaman), 0);
            }
        }
        return true;
    }
}
