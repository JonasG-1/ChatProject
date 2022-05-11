package ServerChat;

import java.util.concurrent.TimeUnit;

public class StartServer {

    private static boolean zSchleife = true;

    public static void main(String[] args) {
        new ServerController(true);

        while (zSchleife) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void setzeSchleife(boolean zSchleife) {
        StartServer.zSchleife = zSchleife;
    }
}
