package ServerConsole;

import Constants.GLOBAL_CONST;
import ServerChat.ServerController;
import linearestrukturen.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console extends Thread {

    private static ServerController kenntController;
    private List<Command> hatBefehle;
    boolean lLaeuft = true;

    public void addController(ServerController pController) {
        kenntController = pController;
    }

    private void setupCommands() {
        hatBefehle = new List<>();
        hatBefehle.append(new Admins());
        hatBefehle.append(new Stop());
        hatBefehle.append(new Kick());
        hatBefehle.append(new Bann());
        hatBefehle.append(new Entbann());
        hatBefehle.append(new BannIP());
        hatBefehle.append(new ListUsers());
        hatBefehle.append(new Nachricht());
        hatBefehle.append(new Debug());
    }

    public void run() {
        setupCommands();
        Debugger hatDebugger = new Debugger();
        try (BufferedReader lLeser = new BufferedReader(new InputStreamReader(System.in))) {
            hatDebugger.print(GLOBAL_CONST.CONSOLE_NACHRICHTEN.GESTARTET);
            String lEingabe;
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
                    hatDebugger.print("Der Eingegebene Befehl konnte nicht verarbeitet werden.", 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kenntController.ausschalten();
            hatDebugger.print("Der Server wurde heruntergefahren.", 1);
        }
    }

    public boolean befehl(String pBefehl, String pArgumente) {
        hatBefehle.toFirst();
        while (hatBefehle.hasAccess()) {
            Command lBefehl = hatBefehle.getContent();
            if (lBefehl.istBefehl(pBefehl)) {
                return lBefehl.run(pArgumente, kenntController, this);
            }
            hatBefehle.next();
        }
        return false;
    }
}
