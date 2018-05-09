package com.signavio.warehouse.configuration.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weka.associations.Apriori;
import weka.associations.AprioriItemSet;
import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.Item;
import weka.associations.ItemSet;
import weka.core.Instances;
import weka.filters.Filter;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class ConfigurationRules {
	
	private double support = 0.01f;
	private double confidence = 0.5f;
	private int nbRules = 10;
	private HashMap<String, String> nameNode;
	private AssociationRules rules;
	
	public ConfigurationRules(double support, double confidence, int nbRules, HashMap<String, String> nameNode) {
		this.support = support;
		this.confidence = confidence;
		this.nbRules = nbRules;
		this.nameNode = nameNode;
	}

	public AssociationRules getRules() { return rules; }
	public void setRules(AssociationRules rules) { this.rules = rules; }

	public JSONObject getRulesJSON(String file, Integer nbAtt)
	{
		try {
			BufferedReader reader = new BufferedReader( new FileReader(file));
			Instances data = new Instances(reader);
			reader.close();
			// setting class attribute
			//data.setClassIndex(data.numAttributes() - 1);
			
			String[] options = new String[2];
			options[0] = "-R";                // "range"
			
			String temp = "";
			for(int i=0;i<nbAtt;i++) {
				temp+=(i+1)+"-";
			}
			temp = temp.substring(0, temp.length()-1);
			options[1] = temp;                 

			weka.filters.unsupervised.attribute.StringToNominal ff = new weka.filters.unsupervised.attribute.StringToNominal(); // new instance of filter

			ff.setOptions(options);                           // set options
			ff.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
			Instances data2 = Filter.useFilter(data, ff);
			   
			Apriori ApObj = new weka.associations.Apriori();
			ApObj.setLowerBoundMinSupport(this.support); // min Support
			ApObj.setOutputItemSets(true);
			ApObj.setMinMetric(this.confidence); //min confidence
			
			ApObj.setNumRules(this.nbRules);

			ApObj.buildAssociations(data2);
			
			//System.out.println(	ApObj.getNumRules());
			// get the extracted association rules
			String aprioriRules = ApObj.toString();
			// send the extracted rules to the Console
			//System.out.println ("Extracted rules :  \n\n"+aprioriRules);
			
			this.rules = ApObj.getAssociationRules();
			
			JSONObject json = associationsRulesToJSON(rules,ApObj.getAllTheRules().length);
			
			/*
			//save to file
			String cheminDuFichier = "F:/Rules_5.txt";
			
			File file2 = new File(cheminDuFichier);

			if (!file2.exists())
				file2.createNewFile();
			FileWriter writer = new FileWriter(file2);

			writer.write(aprioriRules);
			writer.flush();
			writer.close();
			*/
			
			return json;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject associationsRulesToJSON(AssociationRules rules, int length)
	{
		try {
			
			JSONObject json = new JSONObject();
			JSONArray jsons = new JSONArray();

			List<AssociationRule> rulesList = rules.getRules();

			for(AssociationRule r : rulesList)
			{
				String txt = "";
				
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
					
					txt += it + " , ";
				}
				txt = txt.substring(0, txt.length()-2);
				txt += " ==> ";
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
					
					txt += it + " , ";
				}
				txt = txt.substring(0, txt.length()-2);
					
				
				Double support = ((double)r.getTotalSupport())/((double)length);
				
				//System.out.println("supT = "+r.getTotalSupport()+" length = "+length+" res = "+support);
				
				Double confidence = r.getNamedMetricValue("Confidence");
				
				JSONObject json2 = new JSONObject();
				
				json2.put("rule", txt);
				json2.put("support", support);
				json2.put("confidence", confidence);
				jsons.put(json2);
			}
			
			json.put("results", jsons);
			
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

	/*
	public void rules2itemset(){

		try {
			String file = "";
			
			List<AssociationRule> rulesList = rules.getRules();

			for(AssociationRule r : rulesList)
			{
				if(r.getPremise().size() == 1 && r.getConsequence().size() == 1){
					
					String txt = "";
					
					for(Item i1 : r.getPremise())
					{
						String it = i1.toString();

						txt += it;
						
					}
					
					txt += " ==> ";
					
					for(Item i2 : r.getConsequence())
					{
						String it = i2.toString();
						
						txt += it;
					}
						
					Double support = (double) r.getTotalSupport();
					Double confidence = r.getNamedMetricValue("Confidence");
					
					file += txt +" S="+support+" C="+confidence+"\n";
				}
			}
			
			
			String cheminDuFichier = "F:/rules_2itemset.txt";
			
			File filename = new File(cheminDuFichier);

			if (!filename.exists())
				filename.createNewFile();
			FileWriter writer = new FileWriter(filename);
			
			writer.write(file);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}*/
}
