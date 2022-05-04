package ClientChat;

import Constants.GLOBAL_CONST;
import linearestrukturen.List;
import netzwerk.Connection;

import java.util.concurrent.TimeUnit;

public class ClientController {

    private final Oberflaeche hatOberflaeche;
    private ChatClient hatClient;
    private List<String> zAktionen;
    private String zName;
    private boolean zBeenden = false;

    public ClientController() {
        hatOberflaeche = new Oberflaeche(this);
        zAktionen = new List<>();
    }

    public String processCommand(String pBefehl, String pArgumente) {
        switch (pBefehl) {
            case GLOBAL_CONST.CLIENT_BEFEHLE.OK:
            case GLOBAL_CONST.CLIENT_BEFEHLE.ERR:
                handleAnswer(pBefehl, pArgumente);
                break;
            case GLOBAL_CONST.SERVER_BEFEHLE.NACHRICHT:
                return empfangeNachricht(pArgumente, false);
            case GLOBAL_CONST.SERVER_BEFEHLE.PRIVATE_NACHRICHT:
                return empfangeNachricht(pArgumente, true);
            case GLOBAL_CONST.SERVER_BEFEHLE.AUFLISTEN:
                return liste(pArgumente);
            default:
                return GLOBAL_CONST.ERR + GLOBAL_CONST.BEFEHL_KEIN;
        }
        return null;
    }

    private void handleAnswer(String pTyp, String pArgumente) {
        System.out.println(pTyp + " " + pArgumente);
        zAktionen.toFirst();
        if (!zAktionen.hasAccess()) {
            return;
        }
        String lAktion = zAktionen.getContent();
        zAktionen.remove();
        if (lAktion.startsWith(GLOBAL_CONST.CLIENT_BEFEHLE.NAME)) {
            if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
                hatOberflaeche.statusAngemeldet();
                hatOberflaeche.setzeStatus("Angemeldet.");
            } else if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
                hatOberflaeche.setzeStatus("Ein Fehler ist aufgetreten.");
                hatOberflaeche.setzeChatZurueck();
                hatOberflaeche.haengeChatAn("Fehler bei der Anmeldung: \n" + pArgumente);
            }
        } else if (lAktion.startsWith(GLOBAL_CONST.CLIENT_BEFEHLE.NACHRICHT)) {
            if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
                hatOberflaeche.setzeChatStatus("Gesendet.");
            } else if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
                hatOberflaeche.setzeChatStatus("Ein Fehler ist aufgetreten.");
            }
        } else if (lAktion.startsWith(GLOBAL_CONST.CLIENT_BEFEHLE.PRIVATE_NACHRICHT)) {
            if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
                System.out.println("a");
                hatOberflaeche.setzeChatStatus("Private Nachricht gesendet.");                
                int lLeer = lAktion.indexOf(" ");
                String lArgumente = lAktion.substring(lLeer + 1);
                lLeer = lArgumente.indexOf(" ");
                String lName = lArgumente.substring(0, lLeer);
                hatOberflaeche.haengeChatAn("Private Nachricht an " + lName + ":");
                String lNachricht = lArgumente.substring(lLeer + 1);
                hatOberflaeche.haengeChatAn(lNachricht);
            } else if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
                System.out.println("b");
                hatOberflaeche.setzeChatStatus("Ein Fehler ist aufgetreten: " + pArgumente);
            }
        } else if (lAktion.startsWith(GLOBAL_CONST.CLIENT_BEFEHLE.VERBINDUNG_TRENNEN)) {
            if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
                hatClient.close();
                hatOberflaeche.statusReset();
                hatOberflaeche.setzeStatus("Getrennt.");
                if (zBeenden) {
                    hatOberflaeche.ende();
                }
            } else if (pTyp.equals(GLOBAL_CONST.CLIENT_BEFEHLE.ERR)) {
                hatOberflaeche.setzeStatus("Ein Fehler ist aufgetreten.");
                hatOberflaeche.setzeChatZurueck();
                hatOberflaeche.haengeChatAn("Fehler bei der Abmeldung: \n" + pArgumente);
                hatOberflaeche.haengeChatAn("Trennung wird erzwungen in 2 Sekunden.");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignored) {
                }
                hatClient.close();
                hatOberflaeche.statusReset();
                hatOberflaeche.setzeStatus("Getrennt.");
                if (zBeenden) {
                    hatOberflaeche.ende();
                }
            }
        }
    }

    public void verbinden() {
        String lIP = hatOberflaeche.gibIP();
        String lStringPort = hatOberflaeche.gibPort();
        int lPort;
        try {
            lPort = Integer.parseInt(lStringPort);
        } catch (Exception e) {
            hatOberflaeche.setzeStatus("Ungültiger Port.");
            return;
        }
        Connection lConnection;
        try {
            lConnection = new Connection(lIP, lPort, false);
        } catch (Exception e) {
            hatOberflaeche.setzeStatus("Ungültige Adresse.");
            return;
        }
        String lAntwort = lConnection.receive();
        lConnection.close();
        //if (!lAntwort.startsWith(GLOBAL_CONST.CLIENT_BEFEHLE.OK)) {
            //hatOberflaeche.setzeStatus("Es konnte keine Verbindung hergestellt werden");
          //  return;
        //}
        hatClient = new ChatClient(lIP, lPort, this);
        hatOberflaeche.statusVerbunden();
        hatOberflaeche.setzeStatus("Verbunden");
    }

    public void anmelden() {
        zAktionen.toFirst();
        if (!zAktionen.hasAccess()) {
            String lName = hatOberflaeche.gibName();
            String lBefehl = GLOBAL_CONST.CLIENT_BEFEHLE.NAME + " " + lName;
            zAktionen.append(lBefehl);
            hatClient.send(lBefehl);
        } else {
            hatOberflaeche.setzeStatus("Eine Aktion läuft bereits.");
        }
    }

    public void trennen(boolean force) {
        zAktionen.toFirst();
        if (!zAktionen.hasAccess() || force) {
            String lBefehl = GLOBAL_CONST.CLIENT_BEFEHLE.VERBINDUNG_TRENNEN;
            zAktionen.append(lBefehl);
            hatClient.send(lBefehl);
        } else {
            hatOberflaeche.setzeStatus("Eine Aktion läuft bereits.");
        }
    }

    public String liste(String pListe) {
        String[] lListe = pListe.split(",");
        int lAnzahl = 0;
        try {
            lAnzahl = Integer.parseInt(lListe[0]);
        } catch (Exception e) {
            return GLOBAL_CONST.ERR;
        }
        StringBuilder lNamen = new StringBuilder();
        for (int i = 1; i < lListe.length; i++) {
            lNamen.append(lListe[i]).append("\n");
        }
        hatOberflaeche.setzeNutzer(lNamen.toString());
        hatOberflaeche.setzeNutzerZahl(lAnzahl);
        return GLOBAL_CONST.OK;
    }

    public void sendeNachricht(String pNachricht) {
        if (hatOberflaeche.gibStatus() == 2) {
            zAktionen.toFirst();
            if (!zAktionen.hasAccess()) {
                if (hatOberflaeche.gibPrivat()) {
                    
                    StringBuilder lBefehl = new StringBuilder(GLOBAL_CONST.CLIENT_BEFEHLE.PRIVATE_NACHRICHT);
                    lBefehl.append(" ").append(hatOberflaeche.gibPrivatNamen());
                    lBefehl.append(" ").append(pNachricht);
                    zAktionen.append(lBefehl.toString());
                    hatClient.send(lBefehl.toString()); 
                    return;
                }
                String lBefehl = GLOBAL_CONST.CLIENT_BEFEHLE.NACHRICHT + " " + pNachricht;
                zAktionen.append(lBefehl);
                hatClient.send(lBefehl);                
            } else {
                hatOberflaeche.setzeStatus("Eine Aktion läuft bereits.");
            }
        }
    }

    public String empfangeNachricht(String pArgumente, boolean pPrivat) {
        int lLeer = pArgumente.indexOf(" ");
        if (lLeer == -1) {
            return GLOBAL_CONST.ERR;
        }
        String lName = pArgumente.substring(0, lLeer);
        String lNachricht = pArgumente.substring(lLeer + 1);
        if (pPrivat) {
            hatOberflaeche.haengeChatAn("Private Nachricht von " + lName + ":");
        } else {
            hatOberflaeche.haengeChatAn("Nachricht von " + lName + ":");
        }
        
        hatOberflaeche.haengeChatAn(lNachricht);
        return GLOBAL_CONST.OK;
    }

    public void beenden() {
        zBeenden = true;
        trennen(true);
    }
}
