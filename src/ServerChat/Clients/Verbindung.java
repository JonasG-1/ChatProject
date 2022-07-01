package ServerChat.Clients;

import ServerChat.Clients.Benutzer;

public class Verbindung {

    private final String zIP;
    private final int zPort;
    private Benutzer zBenutzer;


    public Verbindung(String pAdresse) {
        String[] lAdresse = pAdresse.split(":");
        zIP = lAdresse[0];
        try {
            zPort = Integer.parseInt(lAdresse[1]);
        } catch (Exception lFehler) {
            throw new RuntimeException(lFehler);
        }
    }

    public Verbindung(String pIP, int pPort) {
        zIP = pIP;
        zPort = pPort;
    }

    protected void verknuepfeBenutzer(Benutzer pBenutzer) {
        if (zBenutzer == null) {
            zBenutzer = pBenutzer;
        }
    }

    protected void entferneBenutzer() {
        zBenutzer = null;
    }

    public boolean istVerknuepft() {
        return zBenutzer != null;
    }

    public Benutzer getBenutzer() throws NoSuchFieldException {
        if (zBenutzer == null) {
            throw new NoSuchFieldException("Benutzer ist nicht verknuepft");
        }
        return zBenutzer;
    }

    public String getIP() {
        return zIP;
    }

    public int getPort() {
        return zPort;
    }

    /**
     * @return IP und Port als String mit einem ":" getrennt.
     */
    public String toString() {
          return zIP + ":" + zPort;
    }
}
