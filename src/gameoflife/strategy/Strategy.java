package gameoflife.strategy;

import gameoflife.life.Life;

import java.util.Hashtable;

public abstract class Strategy {
	public abstract boolean getNextGenerationState(Life life, Hashtable<String, Boolean> livesStateMap);
}
