import java.util.List;

public class TransformationType {
	
	private String tfName;
	private List<String> rollList;
	private List<String> tfList;
	
	public void setTFName(String name)
	{
		tfName = name;
		return;
	}

	public String getTFName()
	{
		return tfName;
	}
	
	public void setTFRollList(List<String> tfRollList)
	{
		rollList = tfRollList;
	}
	
	public List<String> getTFRollList()
	{
		return rollList;
	}
	
	public void setTFList(List<String> tfL)
	{
		tfList = tfL;
	}
	
	public List<String> getTFList()
	{
		return tfList;
	}
	
	public int getTFListCount()
	{
		return tfList.size();
	}
	
	public int getTFRollListSize()
	{
		return rollList.size();
	}
	
	public String getTFAt(int i)
	{
		return tfList.get(i);
	}
}
