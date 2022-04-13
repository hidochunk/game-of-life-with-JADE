package gameoflife.life;

import java.io.IOException;
import java.util.Hashtable;

import gameoflife.strategy.Strategy;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class BaseLife extends Life {

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            try {
                Class<? extends Strategy> strategyClass = (Class<? extends Strategy>) Class.forName((String) args[0]);
                strategy = strategyClass.newInstance();
            } catch (InstantiationException e) {
                System.out.println("Can't new Strategy instance: " + args[0]);
                e.printStackTrace();
                doDelete();
                return;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                doDelete();
                return;
            } catch (ClassNotFoundException e) {
                System.out.println("Can't found Strategy class: " + args[0]);
                e.printStackTrace();
                doDelete();
                return;
            }
        }
        System.out.println("Agent started successfully!! Name: " + getAID().getName() + ", Stragey: "
                + strategy.getClass().getName());
        addBehaviour(new CyclicBehaviour(this) {

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    switch (msg.getProtocol()) {
                        case "start generation":
//                            System.out.println(getAID().getLocalName() + " start running generation");

                            Hashtable<String, Boolean> livesStateMap = null;
                            try {
                                livesStateMap = (Hashtable<String, Boolean>) msg.getContentObject();
                            } catch (UnreadableException e) {
                                System.out.println("Error while get dictionary from content");
                                e.printStackTrace();
                            }
                            boolean nextState = getNextGenerationState(livesStateMap);
                            if (nextState != lifeState) {
                                if (nextState) {
                                    becomeLive();
//                                    System.out.println(getAID().getLocalName() + " revive");
                                } else {
                                    becomeDead();
//                                    System.out.println(getAID().getLocalName() + " dead");
                                }
                            }
//                            System.out.println(getAID().getLocalName() + " finish generation");
                            ACLMessage replyFinishFlag = new ACLMessage(ACLMessage.INFORM);
                            replyFinishFlag.addReceiver(msg.getSender());
                            replyFinishFlag.setProtocol("finish generation");
                            send(replyFinishFlag);
                            break;

                        case "get lives state":
//                            System.out.println(getAID().getLocalName() + " start send back state");
                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                            reply.addReceiver(msg.getSender());
                            reply.setSender(getAID());
                            try {
                                reply.setContentObject(lifeState);
                            } catch (IOException e) {
                                System.out.println("Error while sending back state");
                                e.printStackTrace();
                            }
                            send(reply);
//                            System.out.println(getAID().getLocalName() + " finish send back state");
                            break;

                        case "become live":
                            System.out.println(getAID().getLocalName() + " back to alive");
                            becomeLive();
                            break;

                        default:
                            System.out.println(getAID().getLocalName() + " got a unrecognizable message!!");
                    }
                } else {
                    block();
                }
            }
        });
    }

    @Override
    public void click() {
        becomeLive();
    }

    @Override
    public void becomeLive() {
        lifeState = true;

    }

    @Override
    public void becomeDead() {
        lifeState = false;

    }

    @Override
    public boolean getNextGenerationState(Hashtable<String, Boolean> livesStateMap) {
        boolean newState = strategy.getNextGenerationState(this, livesStateMap);
        return newState;
    }

    @Override
    public boolean getLifeState() {
        return super.getLifeState();
    }
}
