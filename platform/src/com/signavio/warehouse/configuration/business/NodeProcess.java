package com.signavio.warehouse.configuration.business;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class NodeProcess {

	private String name; //name of activity
	private String id;
	private String type; //activity, or, xor, and
	private boolean isConfigurable;
	
	public NodeProcess()
	{
		
	}
	
	public NodeProcess(String id, String name, String type, boolean isConfigurable)
	{
		this.id=id;
		this.name=name;
		this.type=type; 
		this.isConfigurable=isConfigurable;
	}
	
	public String getName(){return name;}
	public String getId() {return id;}
	public String getType(){return type;}
	public String getNameType(){
		if(type.equals("OR") || type.equals("XOR") || type.equals("AND"))
			return type;
		else
			return name;
	}
	public boolean isConfigurable(){return isConfigurable;}
	
	public void setName(String n){name=n;}
	public void setId(String id){this.id=id;}
	public void setType(String t){type = t;}
	public void setConfigurable(boolean c){isConfigurable=c;}
	
	public boolean equalNode(NodeProcess node)
	{
		if(this.id.equals(node.id) && this.type.equals(node.type) && this.name.equals(node.name))
			return true;
		return false;
	}
}
