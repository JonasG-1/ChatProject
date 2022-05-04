package ServerChat;

import ServerConsole.*;
import Constants.GLOBAL_CONST;
import linearestrukturen.List;

public class ServerController {

    private final static int PORT = 8888;

    private final ChatServer hatServer;
    private final Debug hatDebug;
    private final Console hatConsole;
    private final BenutzerVerwaltung hatVerwaltung;


    private final String[] zVerboteneZeichen;
    private final String[] zVerboteneZeichenNachricht;
    private boolean zSendeListe = false;

    public ServerController(boolean pDebug) {
        hatServer = new ChatServer(PORT, false, this);
        Debug.setzeDebug(pDebug);
        hatDebug = new Debug();
        hatConsole = new Console();
        hatConsole.addController(this);
        hatConsole.start();
        hatVerwaltung = new BenutzerVerwaltung();

        zVerboteneZeichen = new String[]{"\\s+", ",", ":", " ", ".", "Admin", "Konsole", "Console"};
        zVerboteneZeichenNachricht = new String[]{};
    }

    public String neueVerbindung(String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        if (hatVerwaltung.istGebannt(pIP)) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NUTZER_GEBANNT;
        }
        return GLOBAL_CONST.OK;
    }

    public String processCommand(String pBefehl, String pArgumente, String pIP, int pPort) {
        switch (pBefehl) {
            case GLOBAL_CONST.CLIENT_BEFEHLE.OK:
            case GLOBAL_CONST.CLIENT_BEFEHLE.ERR:
                handleAnswer(pBefehl, pArgumente, pIP, pPort);
                break;
            case GLOBAL_CONST.CLIENT_BEFEHLE.NAME:
                return addConnection(pArgumente, pIP, pPort);
            case GLOBAL_CONST.CLIENT_BEFEHLE.NACHRICHT:
                return sendMessage(pArgumente, pIP, pPort);
            case GLOBAL_CONST.CLIENT_BEFEHLE.PRIVATE_NACHRICHT:
                return sendPrivateMessage(pArgumente, pIP, pPort);
            default:
                return GLOBAL_CONST.ERR + GLOBAL_CONST.BEFEHL_KEIN;
        }
        return null;
    }

    private void handleAnswer(String pTyp, String pArgumente, String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        if (!hatVerwaltung.istAngemeldet(lAdresse)) {
            return;
        }
        List<String> lAktionen = hatVerwaltung.gibAktionListe(lAdresse);
        lAktionen.toFirst();
        String lAktion = lAktionen.getContent();
        lAktionen.remove();
        if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
            hatDebug.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.AKTION_ERFOLGREICH, lAdresse, lAktion, pArgumente));
        } else if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
            hatDebug.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.AKTION_FEHLGESCHLAGEN, lAdresse, lAktion, pArgumente));
        }
    }

    private String addConnection(String pName, String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        for (String lZeichen : zVerboteneZeichen) {
            if (pName.contains(lZeichen)) {
                return GLOBAL_CONST.ERR + String.format(GLOBAL_CONST.NAME_ZEICHEN, lZeichen);
            }
        }
        if (hatVerwaltung.istAngemeldet(lAdresse)) {
            verbindungTrennen(pIP, pPort);
            return GLOBAL_CONST.ERR + GLOBAL_CONST.BEREITS_VERBUNDEN;
        } else if (hatVerwaltung.nameExistiert(pName)) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NAME_EXISTIERT;
        } else if (pName.isEmpty()) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NAME_ZEICHEN;
        }

        hatVerwaltung.verbindungHinzufuegen(lAdresse, pName);
        zSendeListe = true;
        hatDebug.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.ANMELDUNG, lAdresse, pName));
        return GLOBAL_CONST.OK + String.format(GLOBAL_CONST.ANGEMELDET, pName);
    }

    public void broadcastList() {
        StringBuilder lAusgang = new StringBuilder(GLOBAL_CONST.SERVER_BEFEHLE.AUFLISTEN + " ");
        List<String> lNamen = hatVerwaltung.gibNamenListe();
        lAusgang.append(lNamen.length()).append(",");
        lNamen.toFirst();
        while (lNamen.hasAccess()) {
            lAusgang.append(lNamen.getContent()).append(",");
            lNamen.next();
        }
        hatDebug.debug(GLOBAL_CONST.DEBUG_NACHRICHTEN.LISTE);
        lAusgang.replace(lAusgang.length() - 1, lAusgang.length(), "");
        alleAnhaengen(lAusgang.toString());
        hatServer.sendToAll(lAusgang.toString());
    }

    private String sendMessage(String pNachricht, String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        if (!hatVerwaltung.istAngemeldet(lAdresse)) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NICHT_ANGEMELDET;
        }
        String lNachrichtCheck = checkMessage(pNachricht);
        if (lNachrichtCheck != null) {
            return lNachrichtCheck;
        }
        String lName = hatVerwaltung.gibName(lAdresse);
        String lAusgang = GLOBAL_CONST.SERVER_BEFEHLE.NACHRICHT + " " + lName + " " + pNachricht;
        alleAnhaengen(lAusgang);
        hatDebug.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.NACHRICHT, lAdresse, lName, pNachricht));
        hatServer.sendToAll(lAusgang);
        return GLOBAL_CONST.OK + GLOBAL_CONST.NACHRICHT_UEBERMITTELT;
    }

    private String sendPrivateMessage(String pArgumente, String pIP, int pPort) {
        String lAdresseSender = erstelleAdresse(pIP, pPort);
        if (!hatVerwaltung.istAngemeldet(lAdresseSender)) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NICHT_ANGEMELDET;
        }
        int lLeer = pArgumente.indexOf(" ");
        if (lLeer == -1) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NAME_KEIN;
        }
        String lNameEmpfaenger = pArgumente.substring(0, lLeer);
        if (!hatVerwaltung.nameExistiert(lNameEmpfaenger)) {
            return GLOBAL_CONST.ERR + String.format(GLOBAL_CONST.NAME_FEHLT, lNameEmpfaenger);
        }
        String lNachricht;
        try {
            lNachricht = pArgumente.substring(lLeer + 1);
        } catch (Exception ignored) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NACHRICHT_LEER;
        }
        String lNachrichtCheck = checkMessage(lNachricht);
        if (lNachrichtCheck != null) {
            return lNachrichtCheck;
        }
        String lAdresseEmpfaenger = hatVerwaltung.gibVerbindung(lNameEmpfaenger);
        String lIPEmpfaenger = gibIP(lAdresseEmpfaenger);
        int lPortEmpfaenger = gibPort(lAdresseEmpfaenger);
        String lNameSender = hatVerwaltung.gibName(lAdresseSender);

        String lAusgang = GLOBAL_CONST.SERVER_BEFEHLE.PRIVATE_NACHRICHT + " " + lNameSender + " " + lNachricht;

        hatVerwaltung.gibAktionListe(lAdresseEmpfaenger).append(lAusgang);
        hatDebug.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.PRIVATE_NACHRICHT, lAdresseSender, lNameSender, lNameEmpfaenger, lAdresseEmpfaenger, lNachricht));
        hatServer.send(lIPEmpfaenger, lPortEmpfaenger, lAusgang);
        return GLOBAL_CONST.OK + GLOBAL_CONST.NACHRICHT_UEBERMITTELT;
    }

    private String checkMessage(String lNachricht) {
        if (lNachricht.isEmpty()) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NACHRICHT_LEER;
        }
        for (String lZeichen : zVerboteneZeichenNachricht) {
            if (lNachricht.contains(lZeichen)) {
                return GLOBAL_CONST.ERR + String.format(GLOBAL_CONST.NACHRICHT_ZEICHEN, lZeichen);
            }
        }
        return null;
    }

    public void verbindungTrennen(String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        if (!hatVerwaltung.istGebannt(pIP)) {
            hatDebug.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.VERBINDUNG_TRENNEN, lAdresse));
        }

        String lName = hatVerwaltung.gibName(lAdresse);
        if (hatVerwaltung.nameExistiert(lName)) {
            zSendeListe = true;
        }
        hatVerwaltung.verbindungEntfernen(lAdresse);

        if (!hatVerwaltung.istGebannt(pIP)) {
            hatDebug.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.VERBINDUNG_GETRENNT, lAdresse, lName));
        }
    }

    private void alleAnhaengen(String pAktion) {
        List<String> lNamen = hatVerwaltung.gibNamenListe();
        lNamen.toFirst();
        while (lNamen.hasAccess()) {
            try {
                hatVerwaltung.gibAktionListe(hatVerwaltung.gibVerbindung(lNamen.getContent())).append(pAktion);
            } catch (Exception ignored) {
            }
            lNamen.next();
        }
    }

    public boolean istAdmin(String pAdresse, boolean pEntfernen) {
        return hatVerwaltung.istAdmin(pAdresse, pEntfernen);
    }

    public String addAdmin(String pName) {
        if (!hatVerwaltung.istAngemeldet(pName)) {
           return "-Name existiert nicht.";
        }
        String lAdresse = hatVerwaltung.gibVerbindung(pName);
        if (istAdmin(lAdresse, false)) {
            return "-Nutzer ist bereits ein Administrator.";
        }
        String lIP = gibIP(lAdresse);
        int lPort = gibPort(lAdresse);
        hatVerwaltung.gibAktionListe(lAdresse).append(GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ERTEILT);
        hatServer.send(lIP, lPort, GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ERTEILT);
        hatVerwaltung.adminHinzufuegen(lAdresse);
        return "+Nutzer wurde erfolgreich hinzugefügt.";
    }

    public String removeAdmin(String pName) {
        if (!hatVerwaltung.istAngemeldet(pName)) {
            return "-Name existiert nicht.";
        }
        String lAdresse = hatVerwaltung.gibVerbindung(pName);
        if (istAdmin(lAdresse, true)) {
            String lIP = gibIP(lAdresse);
            int lPort = gibPort(lAdresse);
            hatVerwaltung.gibAktionListe(lAdresse).append(GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ENTZOGEN);
            hatServer.send(lIP, lPort, GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ENTZOGEN);
            return "+Nutzer wurde erfolgreich entfernt.";
        }
        return "-Nutzer ist kein Administrator.";
    }

    public String listAdmins() {
        StringBuilder lAdmins = new StringBuilder();
        String[] lListe = hatVerwaltung.gibAdmins();
        if (lListe == null) {
            return "";
        }
        for (int i = 0; i < lListe.length; i++) {
            String lAdresse = lListe[i];
            String lName = hatVerwaltung.gibName(lAdresse);
            lAdmins.append(lAdresse).append(" [").append(lName).append("]");
            lAdmins.append(", ");
        }
        lAdmins.replace(lAdmins.length() - 2, lAdmins.length(), "");
        return lAdmins.toString();
    }

    public void ausschalten() {
        String lBefehl = GLOBAL_CONST.CLIENT_BEFEHLE.VERBINDUNG_TRENNEN;
        alleAnhaengen(lBefehl);
        hatDebug.debug("Sendet die Abschaltung an alle Verbindungen.");
        hatServer.sendToAll(lBefehl);
        hatDebug.debug("Die Verbindung wird geschlossen.");
        hatServer.close();
        hatDebug.debug("Der Port \"" + PORT + "\" wurde freigegeben.");
    }

    public boolean nutzerKicken(String pAdresse) {
        if (!hatVerwaltung.istVerbunden(pAdresse)) {
            return false;
        }
        String lIP = gibIP(pAdresse);
        int lPort = gibPort(pAdresse);
        hatServer.send(lIP, lPort, GLOBAL_CONST.SERVER_BEFEHLE.TRENNEN);
        hatServer.closeConnection(lIP, lPort);
        hatDebug.debug("Test");
        return true;
    }

    public boolean gibSendeListe() {
        return zSendeListe;
    }

    public void setzeSendeListe(boolean pWert) {
        zSendeListe = pWert;
    }

    public BenutzerVerwaltung gibVerwaltung() {
        return hatVerwaltung;
    }

    public ChatServer gibServer() {
        return hatServer;
    }

    public String gibIP(String pAdresse) {
        String[] lAdresse = pAdresse.split(":");
        return lAdresse[0];
    }

    public int gibPort(String pAdresse) {
        String[] lAdresse = pAdresse.split(":");
        return Integer.parseInt(lAdresse[1]);
    }

    public String erstelleAdresse(String pIP, int pPort) {
        return pIP + ":" + pPort;
    }
}
