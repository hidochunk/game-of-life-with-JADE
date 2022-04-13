package gameoflife.life;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import gameoflife.strategy.BaseStrategy;
import gameoflife.strategy.Strategy;
import jade.core.Agent;

public abstract class Life extends Agent{
	
	//	False means dead, Ture means live 
	protected boolean lifeState = false;
	protected Strategy strategy = new BaseStrategy();
	
	public abstract void click();
	public abstract void becomeLive();
	public abstract void becomeDead();
	public abstract boolean getNextGenerationState(Hashtable<String, Boolean> livesStateMap);
	
	public boolean getLifeState(){
		return lifeState;
	}
	
}
