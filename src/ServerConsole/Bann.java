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
                try {
                    String lIP = pController.gibIP(lVerwaltung.gibAdresse(lName));
                    lVerwaltung.bannHinzufuegen(lIP);
                    String[] lListe = lVerwaltung.gibAdressenMitIP(lIP);
                    for (String s : lListe) {
                        if (pController.kickeNutzer(s)) {
                            hatDebugger.print(String.format("\"%s\" wurde gebannt.", s), 1);
                        } else {
                            hatDebugger.print(String.format("\"%s\" konnte nicht gebannt werden.", s), 0);
                        }
                    }
                } catch (NoSuchFieldException lFehler) {
                    lFehler.printStackTrace();
                }
            } else {
                hatDebugger.print(String.format("\"%s\" ist nicht mit dem Server verbunden.", lName), 0);
            }
        }
        return true;
    }
}
