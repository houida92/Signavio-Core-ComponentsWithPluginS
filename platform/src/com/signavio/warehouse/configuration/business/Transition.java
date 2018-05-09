package com.signavio.warehouse.configuration.business;

import weka.associations.AssociationRule;
import weka.associations.Item;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class Transition {
	private State source;
	private State target;
	private String label;
	
	public Transition() {
		// TODO Auto-generated constructor stub
	}

	public Transition(State s, State t, AssociationRule r) {
		this.source = s;
		this.target = t;
		
		String txt = "";
		for(Item i1 : r.getPremise())
		{
			String it = i1.toString();
			
			txt += it + " , ";
			
		}
		txt = txt.substring(0, txt.length()-2);
		txt += " ==> ";
		for(Item i2 : r.getConsequence())
		{
			String it = i2.toString();
			
			txt += it + " , ";
		}
		txt = txt.substring(0, txt.length()-2);
		
		this.label = txt;
	}

	public Transition(State s, State t, String name) {
		this.source = s;
		this.target = t;
		this.label = name;
	}

	public State getSource() { return source; }
	public void setSource(State source) { this.source = source; }

	public State getTarget() { return target; }
	public void setTarget(State target) { this.target = target;	}

	public String getLabel() { return label; }
	public void setLabel(String label) { this.label = label; }

	public boolean equals(Transition t){
		if(!this.label.equals(t.label))
			return false;
		if(!this.source.equals(t.source))
			return false;
		if(!this.target.equals(t.target))
			return false;
		
		return true;
	}
	
	public String toString(){
		return source.toString()+" -/*\\- "+label+" -/*\\- "+target.toString();
	}
}
