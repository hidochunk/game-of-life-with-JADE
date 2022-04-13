package gameoflife.strategy;

import gameoflife.life.Life;

import java.util.Hashtable;

public class BaseStrategy extends Strategy{
	
	@Override
	public boolean getNextGenerationState(Life life, Hashtable<String, Boolean> livesStateMap){

		String[] coordinate = life.getAID().getLocalName().split(",");
		int y = Integer.parseInt(coordinate[0]);
		int x = Integer.parseInt(coordinate[1]);

		String[] searchRange = new String[8];
		searchRange[0] = (y-1)+","+(x-1);
		searchRange[1] = (y-1)+","+x;
		searchRange[2] = (y-1)+","+(x+1);
		searchRange[3] = y+","+(x-1);
		searchRange[4] = y+","+(x+1);
		searchRange[5] = (y+1)+","+(x-1);
		searchRange[6] = (y+1)+","+x;
		searchRange[7] = (y+1)+","+(x+1);

		int aliveNeighbor = 0;
		for (int i = 0; i < searchRange.length; i++) {
			if (livesStateMap.containsKey(searchRange[i])) {
				if (livesStateMap.get(searchRange[i])) {
					aliveNeighbor++;
				}
			}
		}
		boolean nextState = life.getLifeState();
		if (life.getLifeState()) {
			if (aliveNeighbor > 3 || aliveNeighbor < 2) {
				nextState = false;
			}
		} else {
			if (aliveNeighbor == 3) {
				nextState = true;
			}
		}
		
		return nextState;
	}
}
