import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.dv8tion.jda.core.entities.User;

public class Round {

	private static ArrayList<User> players = new ArrayList();
	private static int turn = 0;
	
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
	
	public boolean checkPresent(User u)
	{
		if (players.isEmpty())
			return false;
		
		if (players.contains(u))
			return true;
		return false;
	}
	
	public User getPlayerAt(int index)
	{
		return players.get(index);
	}
	
	public void nextTurn()
	{
		turn++;
	}
	

}
