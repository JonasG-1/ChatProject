package ServerChat;

import ServerConsole.*;
import Constants.GLOBAL_CONST;
import linearestrukturen.List;

import java.util.ArrayList;

public class ServerController {

    private final static int PORT = 8888;

    private final ChatServer hatServer;
    private final Debugger hatDebugger;
    private final BenutzerVerwaltung hatVerwaltung;

    private final String[] zVerboteneZeichen;
    private final String[] zVerboteneZeichenNachricht;
    private boolean zSendeListe = false;

    public ServerController(boolean pDebug) {
        hatDebugger = new Debugger();
        hatDebugger.setzeDebug(pDebug);
        Console hatConsole = new Console();
        hatConsole.addController(this);
        hatConsole.start();
        hatVerwaltung = new BenutzerVerwaltung();
        zVerboteneZeichen = new String[]{"\\s+", ",", ":", " ", ".", "Admin", "Konsole", "Console"};
        zVerboteneZeichenNachricht = new String[]{};

        hatServer = new ChatServer(PORT, false, this);
        hatDebugger.debug("Server gestartet.");
    }

    public String neueVerbindung(String pIP) {
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
        ArrayList<String> lAktionen = null;
        try {
            lAktionen = hatVerwaltung.gibAktionListe(lAdresse);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
        }
        String lAktion = null;
        if (lAktionen != null) {
            try {
                lAktion = lAktionen.get(0);
            } catch (Exception ignored) {
            }
        } else {
            return;
        }
        if (lAktion == null) {
            return;
        }

        lAktionen.remove(0);
        if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
            hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.AKTION_ERFOLGREICH, lAdresse, lAktion, pArgumente));
        } else if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
            hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.AKTION_FEHLGESCHLAGEN, lAdresse, lAktion, pArgumente));
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

        hatVerwaltung.benutzerHinzufuegen(lAdresse, pName);
        zSendeListe = true;
        hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.ANMELDUNG, lAdresse, pName));
        return GLOBAL_CONST.OK + String.format(GLOBAL_CONST.ANGEMELDET, pName);
    }

    public void broadcastList() {
        StringBuilder lAusgang = new StringBuilder(GLOBAL_CONST.SERVER_BEFEHLE.AUFLISTEN + " ");
        String[] lNamen = hatVerwaltung.gibNamen();
        lAusgang.append(lNamen.length).append(",");
        for (int i = 0; i < lNamen.length; i++) {
           lAusgang.append(lNamen[i]);
           if (i < lNamen.length - 1) {
                lAusgang.append(",");
           }
        }
        hatDebugger.debug(GLOBAL_CONST.DEBUG_NACHRICHTEN.LISTE);
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
        String lName = null;
        try {
            lName = hatVerwaltung.gibName(lAdresse);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
        }
        String lAusgang = GLOBAL_CONST.SERVER_BEFEHLE.NACHRICHT + " " + lName + " " + pNachricht;
        alleAnhaengen(lAusgang);
        hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.NACHRICHT, lAdresse, lName, pNachricht));
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
        String lAdresseEmpfaenger;
        try {
            lAdresseEmpfaenger = hatVerwaltung.gibAdresse(lNameEmpfaenger);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
            return GLOBAL_CONST.ERR + GLOBAL_CONST.FEHLER_FATAL;
        }
        String lIPEmpfaenger = gibIP(lAdresseEmpfaenger);
        int lPortEmpfaenger = gibPort(lAdresseEmpfaenger);

        String lNameSender;
        try {
            lNameSender = hatVerwaltung.gibName(lAdresseSender);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
            return GLOBAL_CONST.ERR + GLOBAL_CONST.FEHLER_FATAL;
        }

        String lAusgang = GLOBAL_CONST.SERVER_BEFEHLE.PRIVATE_NACHRICHT + " " + lNameSender + " " + lNachricht;

        try {
            hatVerwaltung.gibAktionListe(lAdresseEmpfaenger).add(lAusgang);
            hatVerwaltung.gibAktionListe(lAdresseSender).add(lAusgang);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
            return GLOBAL_CONST.ERR + GLOBAL_CONST.FEHLER_FATAL;
        }
        hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.PRIVATE_NACHRICHT, lAdresseSender, lNameSender, lNameEmpfaenger, lAdresseEmpfaenger, lNachricht));
        if (!lNameSender.equals(lNameEmpfaenger)) {
            hatServer.send(pIP, pPort, lAusgang);
        }
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
            hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.VERBINDUNG_TRENNEN, lAdresse));
        }

        String lName = null;
        if (hatVerwaltung.istAngemeldet(lAdresse)) {
            zSendeListe = true;
            try {
                lName = hatVerwaltung.gibName(lAdresse);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
                hatDebugger.print(
                        "Fataler Fehler in verbindungTrennen. " +
                                "Eine Verbindung konnte nicht richtig getrennt werden.",
                        0
                );
            }
        }
        hatVerwaltung.entferneClient(lAdresse);
        if (!hatVerwaltung.istGebannt(pIP)) {
            hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.VERBINDUNG_GETRENNT, lAdresse, lName));
        }
    }

    private void alleAnhaengen(String pAktion) {
        String[] lNamen = hatVerwaltung.gibNamen();
        for (String lNaman : lNamen) {
            try {
                hatVerwaltung.gibAktionListe(hatVerwaltung.gibAdresse(lNaman)).add(pAktion);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
            }
        }
    }

    public boolean istAdmin(String pAdresse, boolean pEntfernen) {
        boolean lIstAdmin = hatVerwaltung.istAdmin(pAdresse);
        if (lIstAdmin && pEntfernen) {
            try {
                hatVerwaltung.entferneAdmin(pAdresse);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                hatDebugger.print(
                        "Fataler Fehler in istAdmin. Ein Admin konnte nicht entfernt werden.", 0
                );
            }
        }
        return lIstAdmin;
    }

    public String addAdmin(String pName) {
        if (!hatVerwaltung.istAngemeldet(pName)) {
           return "-Name existiert nicht.";
        }
        String lAdresse;
        try {
            lAdresse = hatVerwaltung.gibAdresse(pName);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
            return "-Fataler Fehler.";
        }
        if (istAdmin(lAdresse, false)) {
            return "-Nutzer ist bereits ein Administrator.";
        }
        String lIP = gibIP(lAdresse);
        int lPort = gibPort(lAdresse);
        try {
            hatVerwaltung.gibAktionListe(lAdresse).add(GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ERTEILT);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
            return "-Fataler Fehler.";
        }
        hatServer.send(lIP, lPort, GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ERTEILT);
        hatVerwaltung.adminHinzufuegen(lAdresse);
        return "+Nutzer wurde erfolgreich hinzugefügt.";
    }

    public String removeAdmin(String pName) {
        if (!hatVerwaltung.istAngemeldet(pName)) {
            return "-Name existiert nicht.";
        }
        String lAdresse;
        try {
            lAdresse = hatVerwaltung.gibAdresse(pName);
            hatVerwaltung.entferneAdmin(lAdresse);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
            return "-Fataler Fehler.";
        }
        if (istAdmin(lAdresse, true)) {
            String lIP = gibIP(lAdresse);
            int lPort = gibPort(lAdresse);
            try {
                hatVerwaltung.gibAktionListe(lAdresse).add(GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ENTZOGEN);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
                return "-Fataler Fehler. Konnte Client nicht informieren. Admin wurde dennoch entfernt";
            }
            hatServer.send(lIP, lPort, GLOBAL_CONST.SERVER_BEFEHLE.ADMIN_ENTZOGEN);
            return "+Nutzer wurde erfolgreich entfernt.";
        }
        return "-Nutzer ist kein Administrator.";
    }

    public String listAdmins() {
        StringBuilder lAdmins = new StringBuilder();
        String[] lListe = hatVerwaltung.gibAdminAdressen();
        if (lListe == null) {
            return "";
        }
        for (String lAdresse : lListe) {
            String lName = null;
            try {
                lName = hatVerwaltung.gibName(lAdresse);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
            }
            lAdmins.append(lAdresse).append(" [").append(lName).append("]");
            lAdmins.append(", ");
        }
        lAdmins.replace(lAdmins.length() - 2, lAdmins.length(), "");
        return lAdmins.toString();
    }

    public void ausschalten() {
        String lBefehl = GLOBAL_CONST.CLIENT_BEFEHLE.VERBINDUNG_TRENNEN;
        alleAnhaengen(lBefehl);
        hatDebugger.debug("Sendet die Abschaltung an alle Verbindungen.");
        hatServer.sendToAll(lBefehl);
        hatDebugger.debug("Die Verbindung wird geschlossen.");
        hatServer.close();
        hatDebugger.debug("Der Port \"" + PORT + "\" wurde freigegeben.");
    }

    public boolean nutzerKicken(String pAdresse) {
        if (!hatVerwaltung.istVerbunden(pAdresse)) {
            return false;
        }
        String lIP = gibIP(pAdresse);
        int lPort = gibPort(pAdresse);
        hatServer.send(lIP, lPort, GLOBAL_CONST.SERVER_BEFEHLE.TRENNEN);
        hatServer.closeConnection(lIP, lPort);
        hatDebugger.debug("Test");
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
