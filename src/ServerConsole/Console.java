package ServerConsole;

import Constants.GLOBAL_CONST;
import ServerChat.ServerController;
import linearestrukturen.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console extends Thread {

    private static ServerController kenntController;
    private Debug hatDebug;
    private List<Command> zBefehle;
    boolean lLaeuft = true;

    public void addController(ServerController pController) {
        kenntController = pController;
    }

    private void setupCommands() {
        zBefehle = new List<>();
        zBefehle.append(new Admins());
        zBefehle.append(new Stop());
        zBefehle.append(new Kick());
        zBefehle.append(new Bann());
        zBefehle.append(new Entbann());
        zBefehle.append(new BannIP());
        zBefehle.append(new ListUsers());
        zBefehle.append(new Nachricht());
    }

    public void run() {
        setupCommands();
        hatDebug = new Debug();
        try (BufferedReader lLeser = new BufferedReader(new InputStreamReader(System.in))) {
            hatDebug.print(GLOBAL_CONST.CONSOLE_NACHRICHTEN.GESTARTET);
            String lEingabe = "a";
            while (lLaeuft) {
                lEingabe = lLeser.readLine();
                int lLeer = lEingabe.indexOf(" ");
                boolean lAusgabe;
                if (lLeer == -1) {
                    lAusgabe = befehl(lEingabe, "");
                } else {
                    String lBefehl = lEingabe.substring(0, lLeer);
                    String lArgumente = lEingabe.substring(lLeer + 1);
                    lAusgabe = befehl(lBefehl, lArgumente);
                }
                if (!lAusgabe) {
                    hatDebug.print("Der Eingegebene Befehl konnte nicht verarbeitet werden.", 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kenntController.ausschalten();
            hatDebug.print("Der Server wurde heruntergefahren.", 1);
        }
    }

    public boolean befehl(String pBefehl, String pArgumente) {
        zBefehle.toFirst();
        while (zBefehle.hasAccess()) {
            Command lBefehl = zBefehle.getContent();
            if (lBefehl.istBefehl(pBefehl)) {
                return lBefehl.run(pArgumente, kenntController, this);
            }
            zBefehle.next();
        }
        return false;
    }
}
