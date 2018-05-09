package com.signavio.warehouse.configuration.business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class ConfigurationSystem {
	public TransitionSystem transitionSystem;
	public List<Object> petriNet;
	public AssociationRules rules;
	private HashMap<String, String> nameNode;
	private ArrayList<String> confElemID;
	private HashMap<String,String> labels_G2;
	
	static int nb;
	
	public ConfigurationSystem(AssociationRules rules, HashMap<String, String> nameNode, ArrayList<String> confElemID) {
		this.rules = rules;
		this.nameNode = nameNode;
		nb = 0;
		this.confElemID = confElemID;
	}
	
	public ConfigurationSystem() {
		// TODO Auto-generated constructor stub
	}

	public TransitionSystem getTransitionSystem() {
		System.out.println("----*----*----*----*----*----");
		
		transitionSystem = new TransitionSystem("ts1");
		List<State> tmp = new ArrayList<State>();
		
		for(AssociationRule r : rules.getRules()){
			State s = new State(r,nameNode,confElemID);
			if(transitionSystem.addState(s)){
				tmp.add(s);
				transitionSystem.addTransition(new State(confElemID),s,"s");
			}
		}
		
		for(State st : tmp){
			transitionS(st);
		}
		//transitionS(tmp.get(0));
		
		return transitionSystem;
	}
	
	public void getPetriNet() {
		
	}

	public void transitionS(State s){
		for(AssociationRule r : rules.getRules()){
			boolean condi = true;
			for(Item it : r.getPremise()){
				if(!s.itemExist(it.toString())){
					condi = false;
					break;
				}
			}
			if(condi){
				State s2 = new State(s,r,nameNode,confElemID);
				boolean v = transitionSystem.addState(s2);
				if(transitionSystem.addTransition(s,s2,r)){
					System.out.println("Add Transition.");//this.arTOstring(r));
				}
				if(v){
					System.out.println("Add State.");
					this.transitionS(s2);
				}		
			}
		}
	}
	
	public void tsTOsg(TransitionSystem ts, String fileName) {
		try {
			HashMap<String,Integer> labels_G = new HashMap<String, Integer>();
			int i_lb = 0;
			labels_G2 = new HashMap<String, String>();
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName))));
			/**
			 * States will be identified by 's' followed by a unique number.
			 * This map will map any state to its identifier.
			 */
			HashMap<Object, Integer> map = new HashMap<Object, Integer>();
			/**
			 * This set will contain all transition labels.
			 */
			HashSet<String> labels = new HashSet<String>();

			/**
			 * Generate identifiers for all states.
			 */
			int id = 1;
			for (State state : ts.getStates()) {
				map.put(state.toString(), id);
				id++;
			}
			/**
			 * Store all transition labels.
			 */
			for (Transition trans : ts.getTransitions()) {
				labels.add(trans.getLabel());
				if(!labels_G.containsKey(trans.getLabel())) {
					labels_G.put(trans.getLabel(),i_lb);
					labels_G2.put("G"+i_lb, trans.getLabel());
					i_lb++;
				}
			}

			/**
			 * Write the file. 1. The name of the state graph.
			 */
			bw.write(".model " + ts.getName() + "\n");
			/**
			 * 2. A list of all transition labels. Perhaps ".dummy" should be
			 * ".outputs".
			 */
			bw.write(".dummy");
			
			i_lb = 0;
			for (String label : labels) {
				
				if (label.length() > 0) {
					if(i_lb != 0)
						bw.write(" G" + i_lb);
					i_lb++;
				} else {
					/**
					 * Use "_" for silent transitions. Perhaps some other
					 * string?
					 */
					bw.write(" _");
				}
			}
			bw.write(" s");
			bw.write("\n");
			/**
			 * 3. All transitions (state id, trans id, state id).
			 */
			bw.write(".state graph\n");
			for (Transition trans : ts.getTransitions()) {
				if(map.get(trans.getSource().toString()) == null)
					bw.write("si ");
				else
					bw.write("s" + map.get(trans.getSource().toString()) + " ");
				
				if (trans.getLabel().length() > 0) {
					if(trans.getLabel().equals("s"))
						bw.write("s ");
					else
						bw.write("G"+labels_G.get(trans.getLabel()) + " ");
				} else {
					bw.write("_ ");
				}
				bw.write("s" + map.get(trans.getTarget().toString()) + "\n");
			}
			/**
			 * 4. The initial 'marking'.
			 */
			bw.write(".marking {");
			
			bw.write(" si");
			
			bw.write(" }\n");
			/**
			 * 5. End.
			 */
			bw.write(".end\n");

			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String arTOstring(AssociationRule r){
		String s = "";
		
		for(Item i1 : r.getPremise())
		{
			String it = i1.toString();
			
			for (int index = it.indexOf("sid");
				     index >= 0;
				     index = it.indexOf("sid", index + 1))
			{
				String id = it.substring(index, index+40);
			    it = it.replaceAll(id, this.nameNode.get(id));
			}
			
			s += it + " , ";
			
		}
		s = s.substring(0, s.length()-2);
		s += " ==> ";
		for(Item i2 : r.getConsequence())
		{
			String it = i2.toString();
			
			for (int index = it.indexOf("sid");
				     index >= 0;
				     index = it.indexOf("sid", index + 1))
			{
				String id = it.substring(index, index+40);
			    it = it.replaceAll(id, this.nameNode.get(id));
			}
			
			s += it + " , ";
		}
		s = s.substring(0, s.length()-2);
		
		return s;
	}

	public String IDtoName(String s){

		for (int index = s.indexOf("sid");
				 index >= 0;
				 index = s.indexOf("sid", index + 1))
		{
			String id = s.substring(index, index+40);
			s = s.replaceAll(id, this.nameNode.get(id));
		}

		return s;
	}

	public void saveToFile()
	{
		try {
			String cheminDuFichier = "F:/st.txt";
			String contenu = "";
			
			File file = new File(cheminDuFichier);

			if (!file.exists())
				file.createNewFile();
			FileWriter writer = new FileWriter(file);		
			
			for(Transition st : transitionSystem.getTransitions()){
				contenu += st.toString()+"\n";
			}
			
			writer.write(contenu);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeScript(String file_ts, String filePath){
		try {	
			Process p = Runtime.getRuntime().exec("cmd /c start /wait C:\\petrify-4.1\\petrify4.1.exe -dead F:\\ts2.g -o F:\\petrinet.out");

			//Process p = Runtime.getRuntime().exec("/bin/sh -c petrify -dead "+file_ts+" -o "+filePath);
			
		    p.waitFor();
		    
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            StringBuffer output = new StringBuffer();
            
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
			
			//System.out.println("----------------*-----------------");
			//System.out.println(output.toString());
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JSONObject toJSON(JSONObject json, String filePath) {
		try {
			JSONArray jsons = new JSONArray();			 

			BufferedReader buff = new BufferedReader(new FileReader(filePath));
			 
			String line;

			while ((line = buff.readLine()) != null) {
				if(line.contains(".graph"))	{
					break;
				}
			}
			
			while ((line = buff.readLine()) != null) {
				if(line.contains(".marking")) {
					break;
				}
				
				String[] splited = line.split("\\s+");
				
				for(int i=1; i<splited.length; i++){
					JSONObject json2 = new JSONObject();
					
					json2.put("source", splited[0]);
					json2.put("target", splited[i]);
					if(labels_G2.containsKey(splited[0])){
						json2.put("labelS", IDtoName(this.labels_G2.get(splited[0])));
					}else
						json2.put("labelS","");
					
					if(labels_G2.containsKey(splited[i])){
						json2.put("labelT", IDtoName(this.labels_G2.get(splited[i])));
					}else
						json2.put("labelT","");
					
					jsons.put(json2);
				}
			}
			
			json.put("petrinet", jsons);
			
			buff.close();
			return json;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
