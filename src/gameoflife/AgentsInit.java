package gameoflife;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import static jade.core.Profile.DEFAULT_PORT;
import static java.lang.Thread.sleep;

public class AgentsInit {
    public static void main(String[] args) {
        int gameMapSize = 20;
        Runtime runtime = Runtime.instance();
        Profile pMain = new ProfileImpl("localhost", DEFAULT_PORT, "gameOfLife");
        AgentContainer mainContainerRef = runtime.createMainContainer(pMain);

        AgentController newAgent = null;
        for (int y = 0; y < gameMapSize; y++) {
            for (int x = 0; x < gameMapSize; x++) {
                try {
                    newAgent = mainContainerRef.createNewAgent(y + "," + x, "gameoflife.life.BaseLife", null);
                    newAgent.start();
                } catch (StaleProxyException e) {
                    System.out.println("Error while creating Agent");
                    e.printStackTrace();
                }
            }
        }
        try {
            sleep(2000);

            newAgent = mainContainerRef.createNewAgent("generation controller", "gameoflife.controller.GenerationController", null);
            newAgent.start();

            newAgent = mainContainerRef.createNewAgent("rma", "jade.tools.rma.rma", null);
            newAgent.start();
        } catch (InterruptedException | StaleProxyException e) {
            System.out.println("Error while creating Agent");
            e.printStackTrace();
        }
        return;
    }
}
