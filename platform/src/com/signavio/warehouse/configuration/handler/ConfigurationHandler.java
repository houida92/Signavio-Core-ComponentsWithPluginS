package com.signavio.warehouse.configuration.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.configuration.business.BPMNModel;
import com.signavio.warehouse.configuration.business.ConfigurationMatrix;
import com.signavio.warehouse.configuration.business.ConfigurationRules;
import com.signavio.warehouse.configuration.business.ConfigurationSystem;
import com.signavio.warehouse.configuration.business.ProcessBD;
import com.signavio.warehouse.configuration.business.XMLParser;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
@HandlerConfiguration(uri = "/configurationrule", rel = "configurationrule")
public class ConfigurationHandler extends BasisHandler  {

	public ConfigurationHandler(ServletContext servletContext) {
		super(servletContext);
		// TODO Auto-generated constructor stub
	}

	//////////////// DO GET ///////////////////////////////////////////////
	public <T extends FsSecureBusinessObject> void doGet(
			HttpServletRequest req, final HttpServletResponse res,
			FsAccessToken token, T sbo) {
	
		try {
			
			// Get the parameter list
			String xmlstr = (String)req.getParameter("xml");
			
			Double support = 0.01;
			if(!req.getParameter("support").equals(""))
				support = Double.parseDouble(req.getParameter("support"));
			Double confidence = 0.5;Double.parseDouble(req.getParameter("confidence"));
			if(!req.getParameter("confidence").equals(""))
				confidence = Double.parseDouble(req.getParameter("confidence"));
			int nbRules = 100;
			if(!req.getParameter("nbRules").equals(""))
				nbRules = Integer.parseInt(req.getParameter("nbRules"));

	    	//System.out.println(support + "--" + confidence);
	    	//System.out.println("---------------***---------------");
	    	
	    	XMLParser xml = new XMLParser(xmlstr);
	    	BPMNModel model = xml.parser();
	    	
	    	ProcessBD processBD = new ProcessBD();
	    	ArrayList<BPMNModel> models = processBD.getProcess();
	    	//int nb = models.size();
	    	//processBD.setProcess(model);
	    	
	    	ConfigurationMatrix mat = new ConfigurationMatrix(model, models);
	    	
	    	mat.setConfigurationMatrix("process_1");

	    	ArrayList<Object> params = mat.saveConfigurationMatrixToARFFFile();
	    	
	    	HashMap<String,String> nameNode = mat.getNameNode();
 
	    	ConfigurationRules cr = new ConfigurationRules(support,confidence,nbRules,nameNode);
	    	JSONObject rules = cr.getRulesJSON((String)params.get(0),(Integer)params.get(1));

	    	ArrayList<String> confElemID = model.getConfigurableElementsID();
	    	
	    	String file_ts = "F:\\ts2.g";
	    	//String file_ts = "/home/nour/repository/transitionsystem.g";
	    	
	    	String filePath = "F:\\petrinet.out";
	    	//String filePath = "/home/nour/repository/petrinet.out";
	    	
	    	ConfigurationSystem cs = new ConfigurationSystem(cr.getRules(),nameNode,confElemID);
	    	cs.getTransitionSystem();
	    	cs.tsTOsg(cs.getTransitionSystem(), file_ts);
	    	//cs.saveToFile();   	
	    	
	    	cs.executeScript(file_ts,filePath);
			
	    	String temp = cs.toJSON(rules, filePath).toString();
	    	
	    	temp = temp.replace("<", "&lt;");
	    	temp = temp.replace(">", "&gt;");

	    	res.getWriter().write(temp);

		}catch(Exception e){ 
			e.printStackTrace();
		}
	}
	
	JSONObject hashMapToJson(HashMap<String,HashMap<String,String>> matconf)
	{
		try {
			JSONObject json = new JSONObject();
			JSONArray jsons = new JSONArray();
			
			Set<Entry<String, HashMap<String, String>>> set1 = matconf.entrySet();
			for (Entry<String, HashMap<String, String>> e1 : set1) {
				String key1 = e1.getKey();
			    HashMap<String,String> map2 = e1.getValue();
			    Set<Entry<String, String>> set2 = map2.entrySet();
			    for (Entry<String, String> e2 : set2) {
			    	
			        JSONObject json2 = new JSONObject();
			        json2.put("process", key1);
					json2.put("nodeName", e2.getKey());
					json2.put("configuration", e2.getValue());//.replace("'", "")
					jsons.put(json2);
			    }
			}
			
			json.put("task", "test");
			json.put("ontology", "test2");
			json.put("results", jsons);
			
			return json;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	JSONObject hashMapArrayListToJson(HashMap<String,ArrayList<String>> matconf)
	{
		try {
			JSONObject json = new JSONObject();
			JSONArray jsons = new JSONArray();
			
			Set<Entry<String, ArrayList<String>>> set1 = matconf.entrySet();
	        for (Entry<String, ArrayList<String>> e1 : set1) {
				String key1 = e1.getKey();
			    ArrayList<String> map2 = e1.getValue();
			    for(int i = 0; i<map2.size(); i+=2)
	            {
			    	JSONObject json2 = new JSONObject();
			        json2.put("process", key1);
					json2.put("nodeName", map2.get(i));
					json2.put("configuration", map2.get(i+1));
					jsons.put(json2);
	            }
			}
			
			json.put("results", jsons);
			
			return json;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
