import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class TransformationFactory 
{
	public static Document xmlDoc;
	public static List rollList = new ArrayList();
	public static List<TransformationType> tfNodes = new ArrayList();
	public static int dSize;
	
	//A Random number
	static Random rand = new Random();	
	
	//Constructor to set up the TransformationFactory. This should do the heavy lifting.
	public TransformationFactory(Document doc)
	{
		xmlDoc = doc;
		
	}
	
	public List<TransformationType> generateTFList()
	{
		//Get All Transformations in the Master List and count their Children
				NodeList tfCategories = xmlDoc.getFirstChild().getChildNodes();

				setDSize(Integer.parseInt(xmlDoc.getElementsByTagName(Constants.dieToUse).item(0).getTextContent()));
				
				//Iterate through each node in the transformation tag
				for (int i=0; i < tfCategories.getLength(); i++)
				{
					System.out.println(tfCategories.item(i).getNodeName());
					
					if(Constants.tfCategoiesTag.equals(tfCategories.item(i).getNodeName()))
					{
						//Create our Transformation item
						TransformationType thisTF = new TransformationType();
						
						//Set the Transformation's name
						thisTF.setTFName(tfCategories.item(i).getAttributes().getNamedItem("name").getNodeValue());
						
						//Set up the Roll List	
						thisTF.setTFRollList(getRollList(tfCategories.item(i).getChildNodes()));

						//Set up the Transformation List
						thisTF.setTFList(getTransformationList(tfCategories.item(i).getChildNodes()));
						
						tfNodes.add(thisTF);
					}
				}
				
				//End loop, return the list!
				return tfNodes;
	}
		
	private List<String> getRollList(NodeList nList)
	{
		List<String> rollList = new ArrayList<String>();
		
		//We have the Roll List level Node. Iterate through it to build our Roll List
		for(int i=0; i < nList.getLength(); i++ )
		{
			if(Constants.rollList.equals(nList.item(i).getNodeName()))
			{
				//We've hit our roll List. Enter it and get all the Rolls associated with this list.
				for(int j=0; j < nList.item(i).getChildNodes().getLength(); j++)
				{
					if(Constants.roll.equals(nList.item(i).getChildNodes().item(j).getNodeName()))
					{
						//We've found a roll listing. Get the value and add it on.
						rollList.add(nList.item(i).getChildNodes().item(j).getTextContent());
					}
				}
			}
		}
		
		return rollList;
	}
	
	private List<String> getTransformationList(NodeList nList)
	{
		List<String> tfList = new ArrayList<String>();
		
		//We have the Roll List level Node. Iterate through it to build our Roll List
		for(int i=0; i < nList.getLength(); i++ )
		{
			if(Constants.TF.equals(nList.item(i).getNodeName()))
			{

				//We've found a transformation listing. Get the value and add it on.
				tfList.add(nList.item(i).getTextContent());
			}
		}
		
		return tfList;
	}
	
	public int getDSize()
	{
		return Integer.parseInt(xmlDoc.getElementsByTagName(Constants.dieToUse).item(0).getTextContent());
	}
	
	public void setDSize(int diceSize)
	{
		dSize = diceSize;
	}
	
    public String getTrasnformation (int roll)
    {
    	String tfName = "";
    	int tfLocation = 0;
    	int tfListSize = 0;
    	
    	//We have a random roll. This is the work horse.
    	
    	//Step though our rolls for each transformation to determine which kind we have.
    	for(int i =0; i<tfNodes.size(); i++)
    	{
    		for(int j = 0; j < tfNodes.get(i).getTFRollListSize(); j++)
    		{
    			if (String.valueOf(roll).equals(String.valueOf(tfNodes.get(i).getTFRollList().get(j))))
    			{
    				tfName = tfNodes.get(i).getTFName();
    				tfLocation = i;
    			}
    		}
    	}
    	
    	//We have the name, now getting extra information will be easy!
    	tfListSize = tfNodes.get(tfLocation).getTFListCount();
    	
    	return "" + tfNodes.get(tfLocation).getTFName() + ":" + tfNodes.get(tfLocation).getTFAt(rand.nextInt(tfListSize));
    }
}
