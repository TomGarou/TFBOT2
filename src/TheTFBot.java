import java.util.Random;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class TheTFBot extends ListenerAdapter {
	
	public static String transformation = "";
	static Random rand = new Random();
	static Round round = new Round();
	static boolean gameStart = false;
	static int turnNum = 0;
	
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException{
				
		JDA discord = null;
		discord = new JDABuilder(AccountType.BOT).setToken(Constants.token).buildBlocking();
		discord.addEventListener(new TheTFBot());
    }
    
	//The Master list of transformations
	//TODO: Implement this as a list imported from a flat file. It will make changes easier than modifying code, and can easily customize games.
	
	public void makeTransformation (Boolean isReroll)
	{
	    int intStartRoll = 20;
	    int roll = rand.nextInt(intStartRoll);
	    		
	    while (isReroll && (roll == 18 || roll == 19))
	    	roll = rand.nextInt(intStartRoll);
	    
		switch(roll)
    	{
	    	case 1: case 20:
	    		int unCommonRoll = rand.nextInt(4);
	    		switch(unCommonRoll)
	    		{
	    			case 0: bodyChange(); break;
	    			case 1: vocalChange(); break;
	    			case 2: 
	    				if(rand.nextBoolean()) 
	    					mentalChange();
	    				else
	    					clothingChange(); 
	    				break;
	    			case 3: transformation = "gains an additional row of breasts or additional genetalia."; break;
	    		}
	    		break; //4
	    		
	    	case 2: case 3: case 4: creatureChange(); break; //80
	    	case 5: case 6: mentalChange(); break; //28
	    	case 7: case 8: clothingChange(); break; //20
	    	case 9: case 10: mentalChange(); break; //21
	    	case 11: case 12: miscChange(); break; //25
	    	case 13: case 14: case 15: ageChange(); break; //6
	    	case 16: case 17: genderChange(); break; //6
	    	case 18: case 19: 

		    				transformation = "Bonus Rerolls! Do the Following: ";
		    				
		    				int reRollVal = rand.nextInt(5);
		    				
		    				transformation = transformation + " " + Constants.reroll[reRollVal] + "\n";
		    				
		    				switch(reRollVal)
		    				{
		    					case 0: case 1: case 3: case 4: makeTransformation(true); makeTransformation(true); makeTransformation(true); break;
		    					case 2: makeTransformation(true); break;
		    				}
		    				
		    				reRollVal = rand.nextInt();			
	    }

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
    		gameStart = true;
    		if (round.getNumberPlayers() == 0)
    			objChannel.sendMessage("There are no players, add some first!").queue();
    		else
    		{
    			round.createOrder();
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
    
    
    
    //Uncommon Change set.

    public static void bodyChange()
    {
    	transformation = "Body Type: " + Constants.bodyType[rand.nextInt(11)] + "\n";
    }
    
    public static void vocalChange()
    {
    	transformation = "Vocal Change: " + Constants.speech[rand.nextInt(10)] + "\n";
    }
    
    public static void mentalChange()
    {
    	transformation = "Mental: " + Constants.mental[rand.nextInt(28)] + "\n";
    }
    
    public static void supernaturalChange()
    {
    	transformation = "Body Type: " + Constants.supernatural[rand.nextInt(11)] + "\n";
    }
    
    public static void clothingChange()
    {
    	transformation = "Clothing: " + Constants.clothing[rand.nextInt(20)] + "\n";
    }
    
    //Main Changes tables
    public static void miscChange()
    {
    	transformation = "Misc Change: " +  Constants.misc[rand.nextInt(25)] + "\n";
    }
    
    public static void ageChange()
    {
    	transformation = "Age Change: " + Constants.age[rand.nextInt(6)] + "\n";
    }
    
    public static void genderChange()
    {
    	transformation = "Gender:" + Constants.gender[rand.nextInt(4)] + "\n";
    }
    
    public static void creatureChange()
    {
    	transformation = "Creature Feature Change: " + Constants.animal[rand.nextInt(80)] + "\n";
    }
    
}


