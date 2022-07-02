package ServerChat;

import ServerConsole.Debugger;
import Constants.GLOBAL_CONST;
import netzwerk.Server;

public class ChatServer extends Server {

    ServerController kenntController;
    Debugger hatDebugger;

    public ChatServer(int pPort, boolean pMitProtokoll, ServerController pController) {
        // 10.22.2.6
        super(pPort, pMitProtokoll);
        kenntController = pController;
        hatDebugger = new Debugger();
    }

    @Override
    public void processNewConnection(String pIP, int pPort) {
        String lAusgabe = kenntController.neueVerbindung(pIP);
        send(pIP, pPort, lAusgabe);
        if (lAusgabe.startsWith(GLOBAL_CONST.ERR)) {
            closeConnection(pIP, pPort);
            return;
        }
        kenntController.gibVerwaltung().neueVerbindung(kenntController.erstelleAdresse(pIP, pPort));
        hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.VERBINDUNG, kenntController.erstelleAdresse(pIP, pPort)));
    }

    @Override
    public void processMessage(String pIP, int pPort, String pNachricht) {
        //kenntController.debug("Neue Nachricht: " + pNachricht);
        int lLeer = pNachricht.indexOf(" ");
        if (lLeer == -1) {
            if (pNachricht.equals(GLOBAL_CONST.CLIENT_BEFEHLE.VERBINDUNG_TRENNEN)) {
                send(pIP, pPort, GLOBAL_CONST.OK + GLOBAL_CONST.VERBINDUNG_GETRENNT);
                closeConnection(pIP, pPort);
            } else if (pNachricht.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK) || pNachricht.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
                kenntController.processCommand(pNachricht, "", pIP, pPort);
            } else {
                send(pIP, pPort, GLOBAL_CONST.ERR + GLOBAL_CONST.BEFEHL_KEIN);
            }
            return;
        }
        String lBefehl = pNachricht.substring(0, lLeer);
        String lArgumente = "";
        try {
            lArgumente = pNachricht.substring(lLeer + 1);
        } catch (Exception ignored) {
        }
        String lAntwort = kenntController.processCommand(lBefehl, lArgumente, pIP, pPort);
        if (lAntwort != null && !lAntwort.isEmpty()) {
            send(pIP, pPort, lAntwort);
        }
        if (kenntController.gibSendeListe()) {
            kenntController.broadcastList();
            kenntController.setzeSendeListe(false);
        }
    }

    @Override
    public void processClosingConnection(String pIP, int pPort) {
        kenntController.verbindungTrennen(pIP, pPort);
        if (kenntController.gibSendeListe()) {
            kenntController.broadcastList();
            kenntController.setzeSendeListe(false);
        }
    }
}
