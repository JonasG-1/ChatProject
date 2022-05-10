package ClientChat;

 

import Constants.GLOBAL_CONST;
import netzwerk.Client;

public class ChatClient extends Client {

    ClientController kenntController;

    public ChatClient(String pIP, int pPort, ClientController pController) {
        super(pIP, pPort, false);
        kenntController = pController;
    }

    @Override
    public void processMessage(String pNachricht) {
        int lLeer = pNachricht.indexOf(" ");
        if (lLeer == -1) {
            if (pNachricht.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK) || pNachricht.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
                kenntController.processCommand(pNachricht, "");
            } else {
                send(GLOBAL_CONST.ERR + GLOBAL_CONST.BEFEHL_KEIN);
            }
            return;
        }
        String lBefehl = pNachricht.substring(0, lLeer);
        String lArgumente = "";
        try {
            lArgumente = pNachricht.substring(lLeer + 1);
        } catch (Exception ignored) {
        }
        String lAntwort = kenntController.processCommand(lBefehl, lArgumente);
        if (lAntwort != null) {
            send(lAntwort);
        }
    }
}
