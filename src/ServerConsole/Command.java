package ServerConsole;

import ServerChat.ServerController;

public interface Command {

    Debug hatDebug = new Debug();

    boolean istBefehl(String pBefehl);

    boolean run(String pArgumente, ServerController pController, Console pConsole);

}
