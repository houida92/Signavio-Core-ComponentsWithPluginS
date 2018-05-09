package com.signavio.warehouse.configuration.business;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class Flow {
	
	private NodeProcess source;
	private NodeProcess target;
	public String tmpS;
	public String tmpT;
	
	public Flow()
	{
		
	}
	
	public Flow(String s,String t)
	{
		tmpS=s;
		tmpT=t;
	}
	
	public Flow(NodeProcess s , NodeProcess t)
	{
		source=s; 
		target=t;
	}
	
	public NodeProcess getSource(){return source;}
	public NodeProcess getTarget(){return target;}
	
	public void setSource(NodeProcess s){source=s;}
	public void setTarget(NodeProcess t){target=t;}

}
