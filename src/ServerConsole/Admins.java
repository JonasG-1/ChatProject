package ServerConsole;

import ServerChat.ServerController;

public class Admins implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("admins");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        String[] lArgumente = pArgumente.split("\\s+");
        String lErfolg = "";
        switch (lArgumente[0]) {
            case "add":
                if (lArgumente.length < 2) {
                    hatDebug.print("Bitte einen Namen angeben.", 2);
                    return true;
                }
                lErfolg = pController.addAdmin(lArgumente[1]);
                break;
            case "remove":
                if (lArgumente.length < 2) {
                    hatDebug.print("Bitte einen Namen angeben.", 2);
                    return true;
                }
                lErfolg = pController.removeAdmin(lArgumente[1]);
                break;
            case "list":
                String lListe = pController.listAdmins();
                if (lListe.equals("")) {
                    hatDebug.print("Keine Administratoren gefunden");
                    return true;
                }
                hatDebug.print("Administratoren im Chat: " + lListe);
                return true;
            default:
                hatDebug.print("Befehl \"admins\": Argumente sind: \"add\", \"remove\", \"list\"", 2);
                return true;
        }
        String lNachricht = lErfolg.substring(1);
        if (lErfolg.startsWith("-")) {
            hatDebug.print(lNachricht, 0);
        } else {
            hatDebug.print(lNachricht, 1);
        }
        return true;
    }
}
