import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.dv8tion.jda.core.entities.User;

public class Round {

	private static ArrayList<Player> players = new ArrayList();
	private static int turn = 0;
	private static boolean r_toggle = false;
	
	//Add the player to our round's list
	public void addPlayer(Player u)
	{
		if (!players.contains(u))
			players.add(u);
	}
	
	//Returns the number of players
	public int getNumberPlayers()
	{
		return players.size();
	}
	
	//Of the players we have, create a play order.
	public void createOrder()
	{
		long seed = System.nanoTime();
		
		Collections.shuffle(players, new Random(seed) );
	}
	
	//Not used yet.
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
	
	//Check if out player list has this user already added
	public boolean checkPresent(User u)
	{
		if (players.isEmpty())
			return false;
		
		if (players.contains(u))
			return true;
		return false;
	}
	
	//Clears players
	public void clearPlayers()
	{
		players.clear();
	}
	
	//Returns the current turn counter
	public Player getTurn()
	{
		return getPlayerAt(turn);
	}
	
	//Get the discord user at the current turn
	public User getUserTurn()
	{
		return this.getTurn().getUser();
	}
	
	//Get the discord user of a specific player
	public User getUserAt(int index)
	{
		return players.get(index).getUser();
	}
	
	//Returns the player at the specific index
	public Player getPlayerAt(int index)
	{
		return players.get(index);
	}
	
	//Set up for the next turn. Impliments the random playmode. 
	public void nextTurn()
	{
		if ((turn < getNumberPlayers()-1) && ! (r_toggle))
			turn++;
		else if (r_toggle)
		{
			createOrder();
		}
		else
		{
			setTurn(0);				
		}
			
	}
	
	//Peek at the next round's counter without changing it
	public int softNextTurn()
	{
		if (turn < getNumberPlayers()-1)
			return turn + 1;
		else
		{
			return 0;
		}
			
	}
	
	//Set the turn.
	public void setTurn(int i)
	{
		turn = i;
	}
	

}
