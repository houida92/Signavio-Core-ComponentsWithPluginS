package com.signavio.warehouse.configuration.business;

import java.util.ArrayList;

import weka.associations.AssociationRule;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class TransitionSystem {
	private ArrayList<State> states;
	private ArrayList<Transition> transitions;
	private String name;
	
	
	public TransitionSystem(String name) {
		this.name = name;
		this.states = new ArrayList<State>();
		this.transitions = new ArrayList<Transition>();
	}
	
	public void setTransitions(ArrayList<Transition> transitions) {	this.transitions = transitions;	}
	public ArrayList<Transition> getTransitions() {	return transitions;	}

	public void setStates(ArrayList<State> states) { this.states = states; }
	public ArrayList<State> getStates() { return states; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public boolean addState(State s) {
		for(State st : states){
			if(st.equals(s))
				return false;
		}
		states.add(s);
		return true;
	}

	public boolean addTransition(State s, State s2, AssociationRule r) {
		
		if(s.equals(s2))
			return false;
		
		Transition t = new Transition(s,s2,r);
		for(Transition tr : transitions){
			if(t.equals(tr))
				return false;
		}
		transitions.add(t);
		return true;
	}

	public void addTransition(State s, State s2, String name) {
		Transition t = new Transition(s,s2,name);
		transitions.add(t);		
	}
}
