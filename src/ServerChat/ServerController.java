package ServerChat;

import ServerConsole.*;
import Constants.GLOBAL_CONST;

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
        starteKonsole();
        hatVerwaltung = new BenutzerVerwaltung();
        zVerboteneZeichen = new String[]{"\\s+", ",", ":", " ", ".", "Admin", "Konsole", "Console"};
        zVerboteneZeichenNachricht = new String[]{};

        hatServer = new ChatServer(PORT, false, this);
        hatDebugger.debug("Server gestartet.");
    }

    private void starteKonsole() {
        Console hatConsole = new Console();
        hatConsole.addController(this);
        hatConsole.start();
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
                bearbeiteClientAntwort(pBefehl, pArgumente, pIP, pPort);
                return "";
            case GLOBAL_CONST.CLIENT_BEFEHLE.NAME:
                return fuegeVerbindungHinzu(pArgumente, pIP, pPort);
            case GLOBAL_CONST.CLIENT_BEFEHLE.NACHRICHT:
                return sendeChatNachricht(pArgumente, pIP, pPort);
            case GLOBAL_CONST.CLIENT_BEFEHLE.PRIVATE_NACHRICHT:
                return sendePrivateChatNachricht(pArgumente, pIP, pPort);
            default:
                return GLOBAL_CONST.ERR + GLOBAL_CONST.BEFEHL_KEIN;
        }
    }

    private void bearbeiteClientAntwort(String pTyp, String pArgumente, String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        if (!hatVerwaltung.istAngemeldet(lAdresse)) {
            return;
        }
        String lAktion = gibAktuelleAktion(lAdresse);
        if (lAktion == null) {
            return;
        }
        loggeAktion(pTyp, pArgumente, lAdresse, lAktion);
    }

    private String fuegeVerbindungHinzu(String pName, String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        String lErgebnis = ueberpruefeName(pName, lAdresse);
        if (lErgebnis.startsWith(GLOBAL_CONST.ERR)) {
            return lErgebnis;
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

    private String sendeChatNachricht(String pNachricht, String pIP, int pPort) {
        String lAdresse = erstelleAdresse(pIP, pPort);
        if (!hatVerwaltung.istAngemeldet(lAdresse)) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NICHT_ANGEMELDET;
        }
        String lName = null;
        try {
            lName = hatVerwaltung.gibName(lAdresse);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
        }

        if (ueberpruefeNachricht(pNachricht)) {
            return GLOBAL_CONST.ERR;
        }
        sendeNachrichtAls(pNachricht, lAdresse, lName);
        return GLOBAL_CONST.OK + GLOBAL_CONST.NACHRICHT_UEBERMITTELT;
    }

    private String sendePrivateChatNachricht(String pArgumente, String pIP, int pPort) {
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
        if (ueberpruefeNachricht(lNachricht)) {
            return GLOBAL_CONST.ERR;
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

    public void sendeNachrichtAls(String pNachricht, String pAdresse, String pName) {
        String lAusgang = GLOBAL_CONST.SERVER_BEFEHLE.NACHRICHT + " " + pName + " " + pNachricht;
        alleAnhaengen(lAusgang);
        hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.NACHRICHT, pAdresse, pName, pNachricht));
        hatServer.sendToAll(lAusgang);
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
        if (lAdmins.length() > 2) {
            lAdmins.replace(lAdmins.length() - 2, lAdmins.length(), "");
        }
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

    public boolean kickeNutzer(String pAdresse) {
        if (!hatVerwaltung.istVerbunden(pAdresse)) {
            return false;
        }
        String lIP = gibIP(pAdresse);
        int lPort = gibPort(pAdresse);
        hatServer.send(lIP, lPort, GLOBAL_CONST.SERVER_BEFEHLE.TRENNEN);
        hatServer.closeConnection(lIP, lPort);
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


    private void loggeAktion(String pTyp, String pArgumente, String lAdresse, String lAktion) {
        if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
            hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.AKTION_ERFOLGREICH, lAdresse, lAktion, pArgumente));
        } else if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
            hatDebugger.debug(String.format(GLOBAL_CONST.DEBUG_NACHRICHTEN.AKTION_FEHLGESCHLAGEN, lAdresse, lAktion, pArgumente));
        }
    }

    private String gibAktuelleAktion(String pAdresse) {
        ArrayList<String> lAktionen = gibClientAktionenListe(pAdresse);
        String lAktion = null;
        if (lAktionen != null) {
            try {
                lAktion = lAktionen.get(0);
                lAktionen.remove(0);
            } catch (Exception ignored) {
            }
        }
        return lAktion;
    }

    private ArrayList<String> gibClientAktionenListe(String pAdresse) {
        ArrayList<String> lAktionen = null;
        try {
            lAktionen = hatVerwaltung.gibAktionListe(pAdresse);
        } catch (NoSuchFieldException lFehler) {
            lFehler.printStackTrace();
        }
        return lAktionen;
    }

    private String ueberpruefeName(String pName, String pAdresse) {
        String lZeichen = ueberpruefeNameZeichen(pName);
        if (lZeichen.startsWith(GLOBAL_CONST.ERR)) {
            return lZeichen;
        }
        String lGespeichert = ueberpruefeNameGespeichert(pName, pAdresse);
        if (lGespeichert.startsWith(GLOBAL_CONST.ERR)) {
            return lGespeichert;
        }
        return GLOBAL_CONST.OK;
    }

    private String ueberpruefeNameGespeichert(String pName, String pAdresse) {
        String lIP = gibIP(pAdresse);
        int lPort = gibPort(pAdresse);
        if (hatVerwaltung.istAngemeldet(pAdresse)) {
            verbindungTrennen(lIP, lPort);
            return GLOBAL_CONST.ERR + GLOBAL_CONST.BEREITS_VERBUNDEN;
        } else if (hatVerwaltung.nameExistiert(pName)) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NAME_EXISTIERT;
        }
        return GLOBAL_CONST.OK;
    }

    private String ueberpruefeNameZeichen(String pName) {
        if (pName.isEmpty()) {
            return GLOBAL_CONST.ERR + GLOBAL_CONST.NAME_ZEICHEN;
        }
        for (String lZeichen : zVerboteneZeichen) {
            if (pName.contains(lZeichen)) {
                return GLOBAL_CONST.ERR + String.format(GLOBAL_CONST.NAME_ZEICHEN, lZeichen);
            }
        }
        return GLOBAL_CONST.OK;
    }

    private boolean ueberpruefeNachricht(String pNachricht) {
        if (pNachricht.isEmpty()) {
            return true;
        }
        for (String lZeichen : zVerboteneZeichenNachricht) {
            if (pNachricht.contains(lZeichen)) {
                return true;
            }
        }
        return false;
    }
}
