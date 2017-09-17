import net.dv8tion.jda.core.entities.User;

public class thePlayer {

	
	public static User u ;
	public static String transformList = "";
	
	public void addTransformation (String strTF)
	{
		transformList = transformList + " " + strTF;
	}
	
	public String getAllTransformations()
	{
		return transformList;
	}
	public void setUser (User tfUser)
	{
		u = tfUser;
		
	}
	public User getUser ()
	{
		return u;
	}
	
	
}
