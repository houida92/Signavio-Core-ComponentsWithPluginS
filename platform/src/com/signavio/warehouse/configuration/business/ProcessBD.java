package com.signavio.warehouse.configuration.business;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.signavio.warehouse.configuration.util.BDconnection;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class ProcessBD {

	public ArrayList<BPMNModel> getProcess()
	{
		try {
			BDconnection bd = new BDconnection();
			Connection con = bd.doConnection();
			
			ArrayList<BPMNModel> models = new ArrayList<BPMNModel>();
			BPMNModel model = null;
			int p = -1;
			
			String query = "SELECT * FROM bpmn_exemple ORDER BY processid";
 
			// create the java statement
			Statement st = con.createStatement();
			   
			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);
			   
			// iterate through the java resultset
			while (rs.next())
			{
				String id = rs.getString("id");
				String type = rs.getString("type");
				String name = rs.getString("name");
				String source = rs.getString("sourceref");
				String target = rs.getString("targetref");
				int processid = rs.getInt("processid");
				
				if(p != processid)
				{
					if (p != -1) {
						for(Flow f : model.getFlows())
				    	{
				    		for(NodeProcess n : model.getNodes())
					    	{
					    		if(n.getId().equals(f.tmpS))
					    			f.setSource(n);
					    		if(n.getId().equals(f.tmpT))
					    			f.setTarget(n);
					    	}
				    	}
						
						models.add(model);
						
					}
					p = processid;
					model = new BPMNModel(p+"");
				}
				
				if(type.equals("SequenceFlow"))
					model.addFlow(new Flow(source,target));
				else
					model.addNode(new NodeProcess(id,name,type,false));
				
			}
			
			if(p != -1)
			{
				for(Flow f : model.getFlows())
		    	{
		    		for(NodeProcess n : model.getNodes())
			    	{
			    		if(n.getId().equals(f.tmpS))
			    			f.setSource(n);
			    		if(n.getId().equals(f.tmpT))
			    			f.setTarget(n);
			    	}
		    	}
				
				models.add(model);
			}
				
				
			st.close();
			
			return models;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	public void setProcess(BPMNModel model)
	{
		try {
			BDconnection bd = new BDconnection();
			Connection con = bd.doConnection();
			
			// create the java statement
			Statement st = con.createStatement();
						
			String query = "";

			
			int maxID = 0;
			st.execute("SELECT MAX(processid) FROM bpmn_exemple");    
			ResultSet rs2 = st.getResultSet();  
			while ( rs2.next() ){
			  maxID = rs2.getInt(1);
			}
			maxID++;
			
			for(NodeProcess n : model.getNodes())
			{
				query = " INSERT INTO bpmn_exemple (id,type,name,processid) VALUES "
						+ "('" + n.getId()
						+ "','" + n.getType()
						+ "','" + n.getName()
						+ "', "+maxID+" );";
				
				st.executeUpdate(query);
				query = "";
			}
			
			for(Flow f : model.getFlows())
			{
				query = " INSERT INTO bpmn_exemple (id,type,sourceref,targetref,processid) VALUES "
						+ "('','SequenceFlow'"
						+ ",'" + f.tmpS
						+ "','" + f.tmpT
						+ "', "+maxID+" );";
				
				st.executeUpdate(query);
				query = "";
			}
 
			//System.out.println(query);
			
			/*String cheminDuFichier = "F:/rqt3.sql";
			
			File file = new File(cheminDuFichier);

			if (!file.exists())
				file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(query);
			writer.flush();
			writer.close();

			*/
			   
			st.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
