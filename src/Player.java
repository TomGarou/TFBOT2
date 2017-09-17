import net.dv8tion.jda.core.entities.User;

public class Player {
	// TODO Auto-generated method stub
			/* 
			 * Player object will store information about the players.
			 *  
			 */

	private User user;
	private  int order;
	public String transformList = "";
	
	public Player(User tfUser)
	{
		this.setUser(tfUser);
	}
	
	public void addTransformation (String strTF)
	{
		transformList = transformList + " " + strTF;
	}
	
	public String getAllTransformations()
	{
		return transformList;
	}
	
	private void setUser(User u)
	{
		user = u;
	}
	
	public User getUser()
	{
		return user;
	}
	public void setOrder(int o)
	{
		order = o;
	}
	public int getOrder()
	{
		return order;
	}
	
}
