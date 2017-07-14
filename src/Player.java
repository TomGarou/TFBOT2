import net.dv8tion.jda.core.entities.User;

public class Player {
	// TODO Auto-generated method stub
			/* 
			 * Player object will store information about the current set of players.
			 *  
			 */

	private static User user;
	private static int order;
	
	public static void setUser(User u)
	{
		user = u;
	}
	
	public static User getUser()
	{
		return user;
	}
	public static void setOrder(int o)
	{
		order = o;
	}
	public static int getOrder()
	{
		return order;
	}
	
}
