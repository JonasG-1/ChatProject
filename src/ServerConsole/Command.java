package ServerConsole;

import ServerChat.ServerController;

public interface Command {

    Debugger hatDebugger = new Debugger();

    boolean istBefehl(String pBefehl);

    boolean run(String pArgumente, ServerController pController, Console pConsole);

}
