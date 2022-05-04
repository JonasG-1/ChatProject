package ServerConsole;

import Constants.GLOBAL_CONST;

public class Debug {
    private static boolean zDebug = false;

    public static void setzeDebug(boolean pDebug) {
        zDebug = pDebug;
    }

    public void debug(String pNachricht) {
        if (zDebug) {
            System.out.println(GLOBAL_CONST.DEBUG_PREFIX + pNachricht);
        }
    }

    public void print(String pNachricht) {
        System.out.println(GLOBAL_CONST.CONSOLE_PREFIX + pNachricht);
    }

    public void print(String pNachricht, int pTyp) {
        if (pTyp == 0) {
            System.out.println(GLOBAL_CONST.CONSOLE_PREFIX + GLOBAL_CONST.CONSOLE_NACHRICHTEN.FEHLER + pNachricht);
        } else if (pTyp == 1) {
            System.out.println(GLOBAL_CONST.CONSOLE_PREFIX + GLOBAL_CONST.CONSOLE_NACHRICHTEN.ERFOLG + pNachricht);
        } else if (pTyp == 2) {
            System.out.println(GLOBAL_CONST.CONSOLE_PREFIX + GLOBAL_CONST.CONSOLE_NACHRICHTEN.INFO + pNachricht);
        }
    }
}
