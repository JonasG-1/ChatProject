package ServerChat;

public class StartServer {
    public static void main(String[] args) {
        new ServerController(true);
        while(true) {}
    }
}
