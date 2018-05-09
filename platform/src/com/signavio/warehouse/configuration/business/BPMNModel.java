package com.signavio.warehouse.configuration.business;

import java.util.ArrayList;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class BPMNModel {

	private String id;
	private String processName; //used as id
	private ArrayList<NodeProcess> nodes;
	private ArrayList<Flow> flows;
	
	public BPMNModel()
	{
		nodes = new ArrayList<NodeProcess>();
		flows = new ArrayList<Flow>();
	}
	
	public BPMNModel(String id)
	{
		this.id=id;
		nodes = new ArrayList<NodeProcess>();
		flows = new ArrayList<Flow>();
	}
	
	public BPMNModel(String id, String name, ArrayList<NodeProcess> nodes, ArrayList<Flow> flows)
	{
		this.id=id;
		this.processName=name;
		this.nodes=nodes; 
		this.flows=flows;
	}
	
	public String getProcessId(){return id;}
	public String getProcessName(){return processName;}
	public ArrayList<NodeProcess> getNodes(){return nodes;}
	public ArrayList<Flow> getFlows(){return flows;}
	
	public void setProcessId(String id){this.id=id;}
	public void setProcessName(String name){processName = name;}
	public void setNodes(ArrayList<NodeProcess> n){nodes = n;}
	public void setFlows(ArrayList<Flow> f) {flows = f;}
	
	
	public ArrayList<NodeProcess> getActivities()
	{
		ArrayList<NodeProcess> activities = new ArrayList<NodeProcess>();
		for(NodeProcess node : nodes)
			if(node.getType().equals("Task"))
				activities.add(node);
		return activities;
	}
	
	public ArrayList<NodeProcess> getGateways()
	{
		ArrayList<NodeProcess> gateways = new ArrayList<NodeProcess>();
		for(NodeProcess node : nodes)
			if(node.getType().equals("XOR") 
					|| node.getType().equals("AND") 
					|| node.getType().equals("OR"))
				gateways.add(node);
		return gateways;
	}
	
	public ArrayList<String> getConfigurableElementsID(){
		ArrayList<String> configurableElements = new ArrayList<String>();

		for(NodeProcess f : nodes){
			if (f.isConfigurable() == true)
				configurableElements.add(f.getId());
		}
		return configurableElements;
	}
	
	public ArrayList<NodeProcess> getConfigurableActivities(){
		ArrayList<NodeProcess> configurableActivities = new ArrayList<NodeProcess>();
		ArrayList<NodeProcess> activities = getActivities();
		for(NodeProcess f : activities){
			if (f.isConfigurable() == true)
				configurableActivities.add(f);
		}
		return configurableActivities;
	}
	
	public ArrayList<NodeProcess> getConfigurableGateways(){
		ArrayList<NodeProcess> configurableGateways = new ArrayList<NodeProcess>();
		ArrayList<NodeProcess> gateways = getGateways();
		for(NodeProcess g : gateways){
			if(g.isConfigurable() == true)
				configurableGateways.add(g);
		}
		return configurableGateways;
	}
	
	public void addNode(NodeProcess n)	{
		nodes.add(n);
	}
	
	public void addFlow(Flow f)	{
		flows.add(f);
	}
	
	public String toString(){
		String str = "\nProcess\n";
		for(NodeProcess n : nodes)
		{
			str += "\n"+n.getId()+" - "+n.getType()+" - "+n.getName();
		}
		for(Flow f : flows)
		{
			str += "\n"+f.getSource().getId()+" - "+f.getTarget().getId();
			//str += "\n"+f.tmpS+" - "+f.tmpT;
		}
		return str;
	}
	
	public ArrayList<NodeProcess> getPreset(NodeProcess gateway)
	{
		ArrayList<NodeProcess> preset = new ArrayList<NodeProcess>();
		for(Flow flow : flows)
		{
			if(flow.getTarget().equalNode(gateway))
			{
				preset.add(flow.getSource());
			}
		}
		return preset;
	}
	
	public ArrayList<NodeProcess> getPostset(NodeProcess gateway)
	{
		ArrayList<NodeProcess> postset = new ArrayList<NodeProcess>();
		for(Flow flow : flows)
		{
			if(flow.getSource().equalNode(gateway))
			{
				postset.add(flow.getTarget());
			}
		}
		return postset;
	}
	
	public void getPresetActivity (NodeProcess gateway,ArrayList<NodeProcess> presetActivity)
	{
		ArrayList<NodeProcess> presets = getPreset(gateway);
		if(presets.size()>0)
		{
			for(NodeProcess preset : presets)
			{
				if(preset.getType().equals("Task"))
				{
					if(!presetActivity.contains(preset))
						presetActivity.add(preset);
				}
				else
				{
					getPresetActivity(preset,presetActivity);
				}
			}
		}
	}
	
	public void getPostsetActivity (NodeProcess gateway,ArrayList<NodeProcess> postsetActivity)
	{
		ArrayList<NodeProcess> postsets = getPostset(gateway);
		if(postsets.size()>0)
		{
			for(NodeProcess postset : postsets)
			{
				if(postset.getType().equals("Task"))
				{
					if(!postsetActivity.contains(postset))
						postsetActivity.add(postset);
				}
				else
				{
					getPostsetActivity(postset,postsetActivity);
				}
			
			}
		}
	}
	
	public void getPath(NodeProcess source, NodeProcess target, ArrayList<NodeProcess> path){
		for(Flow f: flows){
			if(f.getSource().getId().equals(source.getId())){
				if(!f.getTarget().getId().equals(target.getId())){
					path.add(f.getTarget());
					getPath(f.getTarget(),target,path);
				}
				break;
			}
		}
	}
}
