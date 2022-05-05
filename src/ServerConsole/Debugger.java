package ServerConsole;

import Constants.GLOBAL_CONST;

import java.util.concurrent.TimeUnit;

public class Debugger {
    private static boolean zDebug = false;
    private static boolean zAngehalten = false;
    private static final Object hatObject = new Object();

    public void setzeDebug(boolean pDebug) {
        if (zAngehalten) {
            print("Debugger wird fortgesetzt.", 2);
            zAngehalten = false;
        }
        zDebug = pDebug;
    }

    public void ausgabeAnhalten() {
        if (!zDebug || zAngehalten) {
            return;
        }
        synchronized (hatObject) {
            new Thread(() -> {
                zAngehalten = true;
                print("Debugger in der Konsole für 20 Sekunden angehalten.", 2);
                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (Exception ignored) {
                }
                if (zDebug && zAngehalten) {
                    print("Debugger wird fortgesetzt.", 2);
                }
                zAngehalten = false;
            }).start();
        }
    }

    public void debug(String pNachricht) {
        if (zDebug && !zAngehalten) {
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
