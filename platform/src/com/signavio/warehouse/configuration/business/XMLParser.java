package com.signavio.warehouse.configuration.business;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class XMLParser {
	
	String xmlStr;
	
	public XMLParser(String xmlStr)
	{
		this.xmlStr = xmlStr;
	}

	public BPMNModel parser()
	{
		try {
			// creation d'une fabrique de documents 
	    	DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 
	    	  
	    	// creation d'un constructeur de documents 
	    	DocumentBuilder constructeur;
			
			constructeur = fabrique.newDocumentBuilder();
			
	    	// lecture du contenu d'un fichier XML avec DOM 
	    	InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xmlStr));
	        
	    	Document document = constructeur.parse(is);
	    	 
	    	//traitement du document 
	    	final Element racine = document.getDocumentElement();
	    	final NodeList racineNoeuds = racine.getChildNodes();
	    	final int nbRacineNoeuds = racineNoeuds.getLength();
	    	
	    	BPMNModel model;
	    	ArrayList<NodeProcess> nodes = new ArrayList<NodeProcess>();
	    	ArrayList<Flow> flows = new ArrayList<Flow>();
	    	
	    	for (int i = 0; i<nbRacineNoeuds; i++) {
	    	    if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
	    	        final Element element = (Element) racineNoeuds.item(i);
	    	        boolean conf = false;

	    	        if (element.getNodeName().equals("SequenceFlow"))
	    	        {
	    	        	String in = "";
	    	        	outerloop:
	    	        	for (int j = 0; j<nbRacineNoeuds; j++) {
	    	        		if(racineNoeuds.item(j).hasChildNodes()) {
	    	        			NodeList nchild = racineNoeuds.item(j).getChildNodes();
	    	        			for (int k = 0; k < nchild.getLength(); k++) {
	    	        				if(((Element)nchild.item(k)).getAttribute("resourceId").equals(element.getAttribute("resourceId")))
	    	        				{
	    	        					in = ((Element)racineNoeuds.item(j)).getAttribute("resourceId");
	    	        					break outerloop;
	    	        				}	
								}
	    	        		}
	    	        	}
	    	        	
	    	        	flows.add(new Flow(in,((Element)(element.getChildNodes().item(0))).getAttribute("resourceId")));	
	    	        }
	    	        else
	    	        {
	    	        	String name = element.getAttribute("name");
	    	        	String type = element.getNodeName();
						if (type.lastIndexOf("Configurable") != -1)
						{
							conf = true;
							type = element.getNodeName().replace("Configurable", "");							
						}
						
						if(type.equals("Exclusive_Databased_Gateway"))
							type = "XOR";
						else
							if(type.equals("ParallelGateway"))
								type = "AND";
							else
								if(type.equals("InclusiveGateway"))
									type = "OR";
						
						nodes.add(new NodeProcess(element.getAttribute("resourceId"), name, type, conf));
					}
	    	    }			
	    	}
	    	
	    	for(Flow f : flows)
	    	{
	    		for(NodeProcess n : nodes)
		    	{
		    		if(n.getId().equals(f.tmpS))
		    			f.setSource(n);
		    		if(n.getId().equals(f.tmpT))
		    			f.setTarget(n);
		    	}
	    	}
	    	
	    	model = new BPMNModel("", "", nodes, flows);
	    	
	    	return model;
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
