import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class TheTFBot extends ListenerAdapter {
	
	//Our transformation string, used to concat the total chang the player gets
	public static String transformation = "";
	
	public static List<TransformationType> tfList = new ArrayList();
	
	//A Random number
	static Random rand = new Random();
	
	//Creating a new round of a game
	static Round round = new Round();
	
	//A check to see if the same has been started. We can only have one game at a time.
	static boolean gameStart = false;
	
	//The current turn counter.
	static int turnNum = 0;
	
	//Allow the TF XML doc to be accessed in the rest of the class.
	static Document doc; 
	
	static TransformationFactory tfGenerator;
	
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException
	{
		try
		{	getAndParseXML();	
			tfGenerator = new TransformationFactory(getDoc());
			
			//Set up our Master List of Transformations
			tfList = tfGenerator.generateTFList();

		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		JDA discord = null;
		discord = new JDABuilder(AccountType.BOT).setToken(Constants.token).buildBlocking();
		discord.addEventListener(new TheTFBot());
		
		makeTransformation(false);
    }
    
	//The Master list of transformations
	//TODO: Implement this as a list imported from a flat file. It will make changes easier than modifying code, and can easily customize games.
	
	public static void makeTransformation (Boolean isReroll)
	{	
		
		//Initiate our random selection of the top level transformation.
	    int intStartRoll = tfGenerator.getDSize();
	    int roll = rand.nextInt(intStartRoll);

	    transformation = tfGenerator.getTrasnformation(roll);

	}
	
	//Discord Event handler. This handles the switches we get from the users.
    public void onMessageReceived(MessageReceivedEvent e)
    {
    	Message objMsg = e.getMessage();
    	MessageChannel objChannel = e.getChannel();
    	User objUser = e.getAuthor();
    	
    	//Add Players to the game. The round will have multiple players. Hopefully...
    	if (objMsg.getContent().equals("/add_me"))
    	{
    		if (!round.checkPresent(objUser))
    		{
    			//Create a new player object.
    			Player me = new Player(objUser);
    			
    			//Add the player to the round
    			round.addPlayer(me);
    			
    			//Message user
    			objChannel.sendMessage("Added!").complete();
    		}
    		else
    		{
    			objChannel.sendMessage("Don't be silly, %s, you're already on the player's list!", round.getUserTurn().getAsMention()).complete();
    		}
    	}
    	
    	
    	//Start the game command
    	if (objMsg.getContent().equals("/start"))
        {
            if (round.getNumberPlayers() == 0)
                objChannel.sendMessage("There are no players, add some first!").queue();
            if (round.getNumberPlayers() == 1)
            {
                round.createOrder();
                gameStart = true;
                objChannel.sendMessage("Having some fun alone?...ok then " + round.getUserAt(0).getAsMention() + " Lets see what you turn into.").queue();
            }
            else
            {
                round.createOrder();
                gameStart = true;
                objChannel.sendMessage("Time to begin! " + round.getUserAt(0).getAsMention() + " You're changing: " + round.getUserAt(1).getAsMention()).queue();
            }
        }
    	
    	//This command should end the game
    	if (objMsg.getContent().equals("/clear") || (objMsg.getContent().equals("/end")))
    	{
    		transformation = "";
    		gameStart = false;
    		round.clearPlayers();
    		round.setTurn(0);
    		objChannel.sendMessage("Game Reset!").complete();
    	}
    	
 
    	//A player command to get thteir list of TFs
    	//TODO: Make sure it's formatted nicely.
    	if(objMsg.getContent().equals("/get_tf"))
    	{
    	    	objUser.openPrivateChannel().queue((channel) -> sendAndLog(channel, round.getTurn().getAllTransformations()));    	
    	    	objChannel.sendMessage(objUser.getAsMention() + ", your transformations have been sent!").complete();
	   	}
    	
    	//Transform when the game has started
    	if(objMsg.getContent().equals("/tf") && gameStart == true)
    	{
    		if(objUser.getAsMention().equals(round.getUserTurn().getAsMention()))
    		{
        		this.makeTransformation(false);
        		round.getPlayerAt(round.softNextTurn()).addTransformation(transformation);;
    	    	objUser.openPrivateChannel().queue((channel) -> sendAndLog(channel, transformation + "\nEnjoy!"));    	
    	    	objChannel.sendMessage(round.getUserTurn().getAsMention() + "You transform " + round.getUserAt(round.softNextTurn()).getAsMention()).complete();
    	    	
		    	round.nextTurn();
		    	objChannel.sendMessage("Your turn, %s!", round.getUserTurn().getAsMention()).complete();
		    	
	    	}
    		else
    		{
    			objChannel.sendMessage("Now now, %s, it's %s's turn.", objUser.getAsMention(), round.getUserTurn().getAsMention()).complete();
    		}
	   	}
    	//Don't give a TF if we haven't started yet!    	
    	else if (objMsg.getContent().equals("/tf") && gameStart == false)
    	{
    		objChannel.sendMessage("Good that you want to start, but make sure all the players are added!").complete();
    	}

    	//Add in ordered, random, last-go and freestyle modes. Default to ordered.
    	//TODO: Implement these modes!
    	if (objMsg.getContent().equals("/playmode"))
    	{
    		round.toggleRandom();
    		objChannel.sendMessage("Random Mode toggled to: " + round.getRandom()).complete();
    	}
    	
    	//List our current players
    	if (objMsg.getContent().equals("/list"))
    	{

    		String allPlayers = "";
    		
    		for (int i =0; i < round.getNumberPlayers(); i++)
    		{
    			allPlayers = allPlayers + round.getUserAt(i).getAsMention() + "\n";
    		}
    		
    		objChannel.sendMessage(allPlayers).complete();
    		
    	}
    	
    	//Clears out the transformation sting we just made, prepping for the next player's round.
    	transformation = "";	
    }
    public static void sendAndLog(MessageChannel channel, String message)
    {
        // Here we use a lambda expressions which names the callback parameter -response- and uses that as a reference
        // in the callback body -System.out.printf("Sent Message %s\n", response)-
        Consumer<Message> callback = (response) -> System.out.printf("");
        channel.sendMessage(message).queue(callback); // ^ calls that
    }
    
    //Method to send the PM
    //TODO: Mute commands sent from here!
    public static void sendDM(User u, String message)
    {
    	u.openPrivateChannel().queue((channel) -> sendAndLog(channel, message + "\nEnjoy!"));
    }
    
    public static void getAndParseXML()
    {
		try
		{
			File fXmlFile = new File("src/TF.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			setDoc(dBuilder.parse(fXmlFile));
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
    }
    
	public static void setDoc(Document dXML)
	{
		doc = dXML;
		
	}
	
	public static Document getDoc()
	{
		return doc;
		
	}
	
	public static void setTFList(List<TransformationType> tfL)
	{
		tfList = tfL;
	}
	
	public static List<TransformationType> getTFList()
	{
		return tfList;
	}
}


