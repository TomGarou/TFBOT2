import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.dv8tion.jda.core.entities.User;

public class Round {

	private static ArrayList<User> players = new ArrayList();
	private static int turn = 0;
	private static boolean r_toggle = false;
	
	public void addPlayer(User u)
	{
		if (!players.contains(u))
			players.add(u);
	}
	
	public int getNumberPlayers()
	{
		return players.size();
	}
	
	public void createOrder()
	{
		long seed = System.nanoTime();
		
		Collections.shuffle(players, new Random(seed) );
	}
	
	public void toggleRandom()
	{
		if (r_toggle)
			r_toggle = false;
		else
			r_toggle = true;
	}
	
	public boolean getRandom()
	{
		return r_toggle;
	}
	
	public boolean checkPresent(User u)
	{
		if (players.isEmpty())
			return false;
		
		if (players.contains(u))
			return true;
		return false;
	}
	
	public void clearPlayers()
	{
		players.clear();
	}
	
	public User getTurn()
	{
		return getPlayerAt(turn);
	}
	
	public User getPlayerAt(int index)
	{
		return players.get(index);
	}
	
	public void nextTurn()
	{
		if (turn < getNumberPlayers()-1)
			turn++;
		else
		{
			setTurn(0);
			if(r_toggle)
				createOrder();
		}
			
	}
	
	public void setTurn(int i)
	{
		turn = i;
	}
	

}