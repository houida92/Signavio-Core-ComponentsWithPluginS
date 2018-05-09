package com.signavio.warehouse.configuration.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import weka.associations.AssociationRule;
import weka.associations.Item;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class State {
	private String[] configuration;
	private int n;
	ArrayList<String> confElemID;
	
	public State(State s, AssociationRule r, HashMap<String, String> nameNode, ArrayList<String> confElemID) {
		this.n = s.n;
		this.configuration = new String[confElemID.size()];
		this.confElemID = confElemID;
		
		for(int i=0; i<this.configuration.length; i++)
		{
			this.configuration[i] = s.configuration[i];
		}
        
        for(Item i2 : r.getConsequence()){
        	 String it = i2.toString();
     		/*
     		for (int index = it.indexOf("sid");
     			     index >= 0;
     			     index = it.indexOf("sid", index + 1))
     		{
     			String id = it.substring(index, index+40);
     		    it = it.replaceAll(id, nameNode.get(id));
     		}
     		*/
        	this.addConfiguration(it);
		}
	}

	public State(AssociationRule r, HashMap<String, String> nameNode, ArrayList<String> confElemID) {
		this.configuration = new String[confElemID.size()];
		this.confElemID = confElemID;
		
		for(int i=0; i<this.configuration.length; i++)
		{
			this.configuration[i] = "-";
		}
		
		for(Item i1 : r.getPremise())
		{
			String it = i1.toString();
			
			/*for (int index = it.indexOf("sid");
				     index >= 0;
				     index = it.indexOf("sid", index + 1))
			{
				String id = it.substring(index, index+40);
			    it = it.replaceAll(id, nameNode.get(id));
			}*/

			this.addConfiguration(it);
		}
	}
	
	public State(ArrayList<String> confElemID) {
		this.configuration = new String[confElemID.size()];
		this.confElemID = confElemID;
		
		for(int i=0; i<this.configuration.length; i++)
		{
			this.configuration[i] = "-";
		}
	}

	public void addConfiguration(String config) {
		int index = config.indexOf("sid");
		String id = config.substring(index, index+40);
		
		int pos = 0;
		for(String s : confElemID){
			if(s.equals(id))
				break;
			pos++;
		}
		
		if(pos < confElemID.size())
			this.configuration[pos] = config.substring(index+41,config.length());
		else
			System.out.println(id + "/**/" + config.substring(index+41,config.length()));
	}

	public boolean itemExist(String it) {		
		int index = it.indexOf("sid");
		String id = it.substring(index,index+40);
		
		int pos = 0;
		for(String s : confElemID){
			if(s.equals(id))
				break;
			pos++;
		}
		
		if(pos < confElemID.size())
			if(this.configuration[pos].equals(it.substring(index+41,it.length())))
				return true;
		return false;
	}

	public boolean equals(State s){
		for(int i=0; i<this.configuration.length; i++)
		{
			if(!this.configuration[i].equals(s.configuration[i]))
				return false;
		}

		return true;
	}
	
	public String toString(){
		String s = "";
		for(int i=0; i<this.configuration.length; i++)
		{
			s += this.configuration[i]+" -*- ";
		}
		
		s = s.substring(0, s.length()-5);
		
		return s;
	}
}
