package gameoflife.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import static java.lang.Thread.sleep;

public class GenerationController extends Agent {
    ArrayList<AID> lives = new ArrayList<AID>();

    public void setup() {
        AMSAgentDescription[] agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(10000000));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < agents.length; i++) {
            String name = agents[i].getName().getLocalName();
            if (name.equals(getAID().getLocalName()) || name.equals("ams") || name.equals("rma") || name.equals("df")) {
                continue;
            }
            lives.add(agents[i].getName());
//            System.out.println("find agent Name: " + name);
        }

        addBehaviour(new CyclicBehaviour(this) {

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                Hashtable<String, Boolean> livesStateMap;
                if (msg != null) {
                    switch (msg.getProtocol()) {
                        case "start generation":
                            livesStateMap = getLivesStateMap(myAgent);
                            System.out.println("Controller get message: Generation start");
                            ACLMessage tellLifesGoToNextgeneration = new ACLMessage(ACLMessage.INFORM);
                            tellLifesGoToNextgeneration.setSender(getAID());
                            tellLifesGoToNextgeneration.setProtocol("start generation");
                            try {
                                tellLifesGoToNextgeneration.setContentObject((Serializable) livesStateMap);
                            } catch (IOException e) {
                                System.out.println("Error while put dictionary into content");
                                e.printStackTrace();
                            }
                            for (int i = 0; i < lives.size(); i++) {
                                tellLifesGoToNextgeneration.addReceiver(lives.get(i));
                            }
                            send(tellLifesGoToNextgeneration);
                            System.out.println("Controller finish message sending");
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            int counter = 0;
                            while (counter != lives.size()) {
                                msg = myAgent.receive();
                                if (msg != null) {
                                    if (msg.getProtocol().equals("finish generation")) {
                                       counter++;
                                    }
                                }
                            }
                            System.out.println("All lives finish generation");
                            break;

                        case "get lives state":
                            System.out.println("Controller get message: Asking lives state");
                            ACLMessage tellLivesSendState = new ACLMessage(ACLMessage.INFORM);
                            tellLivesSendState.setSender(getAID());
                            tellLivesSendState.setProtocol("get lives state");
                            for (int i = 0; i < lives.size(); i++) {
                                tellLivesSendState.addReceiver(lives.get(i));
                            }
                            send(tellLivesSendState);
                            System.out.println("Controller finish message sending, wait a second to start receive message");
                            try {
                                sleep(2000);
                            } catch (InterruptedException e) {
                                System.out.println("Error when sleeping");
                                e.printStackTrace();
                            }
                            Dictionary<String, Boolean> livesState = new Hashtable<>();
                            while (true) {
                                ACLMessage livesStateMsg = myAgent.receive();
                                if (livesStateMsg != null) {
                                    try {
//                                        System.out.println(livesStateMsg.getSender().getLocalName() + " is " + (boolean) livesStateMsg.getContentObject());
                                        livesState.put(livesStateMsg.getSender().getLocalName(), (boolean) livesStateMsg.getContentObject());
                                    } catch (UnreadableException e) {
                                        System.out.println("Error while sending back state");
                                        e.printStackTrace();
                                    }
                                } else {
                                    break;
                                }
                            }
                            break;

                        case "random half become live":
                            ACLMessage tellBecomeLive = new ACLMessage(ACLMessage.INFORM);
                            tellBecomeLive.setProtocol("become live");
                            int random = 0;
                            for (int i = 0; i < lives.size() / 2; i++) {
                                random = (int) (Math.random() * lives.size());
                                tellBecomeLive.addReceiver(lives.get(random));
                            }
                            send(tellBecomeLive);
                            break;

                        case "print lives state":
                            livesStateMap = getLivesStateMap(myAgent);
                            int width = (int) Math.sqrt(lives.size());
                            for (int y = 0; y < width; y++) {
                                System.out.print(String.format("%" + String.valueOf(width).length() + "s", (y+1))+" ");
                                for (int x = 0; x < width; x++) {

                                    if (livesStateMap.get(y + "," + x)) {
                                        System.out.print("■ ");
                                    } else {
                                        System.out.print("□ ");
                                    }
                                }
                                System.out.println();
                            }
                            break;

                        default:
                            System.out.println(getAID().getLocalName() + " got a unrecognizable message!! ");

                    }
                } else {
                    block();
                }
            }
        });
    }

    private Hashtable<String, Boolean> getLivesStateMap(Agent myAgent) {
        System.out.println("Controller get message: Asking lives state");
        ACLMessage tellLivesSendState = new ACLMessage(ACLMessage.INFORM);
        tellLivesSendState.setSender(getAID());
        tellLivesSendState.setProtocol("get lives state");
        for (int i = 0; i < lives.size(); i++) {
            tellLivesSendState.addReceiver(lives.get(i));
        }
        send(tellLivesSendState);
        System.out.println("Controller finish message sending, wait a second to start receive message");
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error when sleeping");
            e.printStackTrace();
        }
        Hashtable<String, Boolean> livesState = new Hashtable<>();
        while (true) {
            ACLMessage livesStateMsg = myAgent.receive();
            if (livesStateMsg != null) {
                try {
//                    System.out.println(livesStateMsg.getSender().getLocalName() + " is " + (boolean) livesStateMsg.getContentObject());
                    livesState.put(livesStateMsg.getSender().getLocalName(), (boolean) livesStateMsg.getContentObject());
                } catch (UnreadableException e) {
                    System.out.println("Error while sending back state");
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return livesState;
    }
}
