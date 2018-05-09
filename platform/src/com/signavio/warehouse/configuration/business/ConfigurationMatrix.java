package com.signavio.warehouse.configuration.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.NonSparseToSparse;
import weka.core.Instance;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */

public class ConfigurationMatrix {
	private BPMNModel configurableModel;
	private ArrayList<BPMNModel> configurationModelList;
	private HashMap<String,HashMap<String,String>> configurationMatrix;
	private HashMap<String,String> nameNode;
	
	public ConfigurationMatrix(){configurationModelList=new ArrayList<BPMNModel>();
			configurationMatrix = new HashMap<String,HashMap<String,String>>();}

	public ConfigurationMatrix(BPMNModel configurable, ArrayList<BPMNModel> configurationList){
		configurableModel = configurable;
		configurationModelList = new ArrayList<BPMNModel>();
		for(BPMNModel c : configurationList)
			configurationModelList.add(c);
		configurationMatrix = new HashMap<String,HashMap<String,String>>();
		nameNode = new HashMap<String, String>();
	}
	
	
	//String = id du processus, string = configurable element id, String = configuration ON, OFF, <confType, {,}> 
	public HashMap<String, ArrayList<String>> setConfigurationMatrix(String processName){
		ArrayList<NodeProcess> configurableActivities = configurableModel.getConfigurableActivities();
		ArrayList<NodeProcess> configurableGateways = configurableModel.getConfigurableGateways();
		
		ConfigurationSimilarity cf;
		//-----
		HashMap<String,ArrayList<String>> confMat = new HashMap<String, ArrayList<String>>();

		int i=1;
		int i_fct = 1;
		int i_or = 1;
		int i_xor = 1;
		int i_and = 1;
		
		if(configurableActivities.size()>0 || configurableGateways.size()>0){
			//get the corresponding configurations from each process variant
			for(BPMNModel configurationModel : configurationModelList){
				
				//System.out.println(configurationModel.toString());
				
				//System.out.println("in configuration:" +i);
								
				i++;
				HashMap<String,String> configurations = new HashMap<String,String>();
				//-----
				ArrayList<String> confs = new ArrayList<String>();
				cf = new ConfigurationSimilarity(configurableModel,configurationModel);
				
				//get the configuration of each function in the process variant P1
				if(configurableActivities.size()>0){
					HashMap<NodeProcess, HashMap<NodeProcess,Double>> activitiesConfigurations = cf.getconfigurableActivitiesBestMapping();
					// if there exists mappings between first variant functions and the configurable functions
					//get the mapped configurable functions with their configurations
					if(activitiesConfigurations.size()>0){
						Set<Entry<NodeProcess,HashMap<NodeProcess,Double>>> activityEntries = activitiesConfigurations.entrySet();
						Iterator<Entry<NodeProcess,HashMap<NodeProcess,Double>>> activityIter = activityEntries.iterator();
						while(activityIter.hasNext()){
							Map.Entry<NodeProcess, HashMap<NodeProcess,Double>> activityEntry = activityIter.next();
							NodeProcess configurableActivity = activityEntry.getKey();
							HashMap<NodeProcess,Double> activityConfiguration = activityEntry.getValue();
							if(activityConfiguration.size()>0){
								configurations.put(configurableActivity.getId(), "ON");
								//-----
								confs.add(configurableActivity.getName());
								confs.add("ON");
								
								if(!nameNode.containsKey(configurableActivity.getId())){
									nameNode.put(configurableActivity.getId(),configurableActivity.getNameType());//+" "+i_fct);
									//i_fct++;
								}
							}
							else{
								configurations.put(configurableActivity.getId(), "OFF");
								//-----
								confs.add(configurableActivity.getName());
								confs.add("OFF");
								
								if(!nameNode.containsKey(configurableActivity.getId())){
									nameNode.put(configurableActivity.getId(),configurableActivity.getNameType());//+" "+i_fct);
									//i_fct++;
								}
							}
						}
					}
				}
				
				//get the configuration of each gateway in the process variant P1
				if(configurableGateways.size()>0){
					HashMap<NodeProcess,HashMap<NodeProcess,ArrayList<NodeProcess>>> gatewaysConfigurations = cf.getConfigurableGatewaysBestMapping();
					if(gatewaysConfigurations.size()>0){
						Set<Entry<NodeProcess,HashMap<NodeProcess,ArrayList<NodeProcess>>>> gatewayEntries = gatewaysConfigurations.entrySet();
						Iterator<Entry<NodeProcess,HashMap<NodeProcess,ArrayList<NodeProcess>>>> gatewayIter = gatewayEntries.iterator();
						//for each configurable gateway get its configuration
						while(gatewayIter.hasNext()){
							Map.Entry<NodeProcess, HashMap<NodeProcess,ArrayList<NodeProcess>>> gatewayEntry = gatewayIter.next();
							NodeProcess configurableGateway = gatewayEntry.getKey();
							//contains the configuration of configurableGateway: <gateway , {,}> or -
							HashMap<NodeProcess,ArrayList<NodeProcess>> gatewayConfiguration = gatewayEntry.getValue();
							//there is a corresponding configuration
							if(gatewayConfiguration.size()>0){
									Set<Entry<NodeProcess,ArrayList<NodeProcess>>> gatewayConfigurationEntries = gatewayConfiguration.entrySet();
									Iterator<Entry<NodeProcess,ArrayList<NodeProcess>>> gatewayConfigurationIter = gatewayConfigurationEntries.iterator();
									while(gatewayConfigurationIter.hasNext()){
										Map.Entry<NodeProcess, ArrayList<NodeProcess>> gatewayConfigurationEntry = gatewayConfigurationIter.next();
										NodeProcess configurationNodeProcess = gatewayConfigurationEntry.getKey();
										String configuration="'<"+configurationNodeProcess.getType()+" , {";
										//-----
										String conf="'<"+configurationNodeProcess.getType()+" , {";
										ArrayList<NodeProcess> configurationActivitiesGateways = gatewayConfigurationEntry.getValue();
										if(configurationActivitiesGateways.size()>0){
											//String configuration="< "+configurationNodeProcess.getType()+" , {";
											
											ArrayList<String> orderConfiguration = new ArrayList<String>();
											//-----
											ArrayList<String> orderConf = new ArrayList<String>();
											for(NodeProcess a : configurationActivitiesGateways){
												orderConfiguration.add(a.getId());
												Collections.sort(orderConfiguration);
												//-----
												orderConf.add(a.getNameType());
												
												nameNode.put(a.getId(),a.getNameType());
												
												/*if(!nameNode.containsKey(a.getId())){
													switch(a.getNameType()){
														case "OR" :
															nameNode.put(a.getId(),a.getNameType()+" "+i_or);
															i_or++;
															break;
														case "XOR" :
															nameNode.put(a.getId(),a.getNameType()+" "+i_xor);
															i_xor++;
															break;
														case "AND" :
															nameNode.put(a.getId(),a.getNameType()+" "+i_and);
															i_and++;
															break;
														default :
															break;
													}													
												}*/
												
											}
											for(String id : orderConfiguration){
													configuration+=id+",";
											}
											//remove the last ","
											configuration=configuration.substring(0, configuration.length()-1);
											configuration+="}>'";
											//-----
											for(String name : orderConf){
												conf+=name+",";
											}
											//remove the last ","
											conf=conf.substring(0, conf.length()-1);
											conf+="}>'";
											
											configurations.put(configurableGateway.getId(), configuration);
											//-----
											confs.add(configurableGateway.getNameType());
											confs.add(conf);
											
											if(!nameNode.containsKey(configurableGateway.getId())){
												if(configurableGateway.getNameType().equals("OR")){
														nameNode.put(configurableGateway.getId(),configurableGateway.getNameType()+" "+i_or);
														i_or++;
												}else if(configurableGateway.getNameType().equals("XOR")){
														nameNode.put(configurableGateway.getId(),configurableGateway.getNameType()+" "+i_xor);
														i_xor++;
												}else if(configurableGateway.getNameType().equals("AND")){
														nameNode.put(configurableGateway.getId(),configurableGateway.getNameType()+" "+i_and);
														i_and++;
												}													
											}
											
											break;
										}
										break;
									}
							}
							else{
								System.out.println("no configuration");
								configurations.put(configurableGateway.getId(), "?");
								//-----
								confs.add(configurableGateway.getNameType());
								confs.add("?");
								
								if(!nameNode.containsKey(configurableGateway.getId())){
									if(configurableGateway.getNameType().equals("OR")){
											nameNode.put(configurableGateway.getId(),configurableGateway.getNameType()+" "+i_or);
											i_or++;
									}else if(configurableGateway.getNameType().equals("XOR")){
											nameNode.put(configurableGateway.getId(),configurableGateway.getNameType()+" "+i_xor);
											i_xor++;
									}else if(configurableGateway.getNameType().equals("AND")){
											nameNode.put(configurableGateway.getId(),configurableGateway.getNameType()+" "+i_and);
											i_and++;
									}													
								}
							}
						}
					}
				}

			   configurationMatrix.put(configurationModel.getProcessId(), configurations);
			   confMat.put(configurationModel.getProcessId(), confs);
			}
		}
		else
			System.out.println("no configurable elements");
		
		return confMat;
	}
	
	public HashMap<String,HashMap<String,String>> getConfigurationMatrix() {
		return configurationMatrix;
	}
	
	public HashMap<String,String> getNameNode() {
		return nameNode;
	}
	
	//store for configurable functions then configurable gateways
	public ArrayList<ArrayList<String[]>> getConfigurationMatrixforArff(){
		ArrayList<ArrayList<String[]>> dataset = new ArrayList<ArrayList<String[]>>();
		Set<Entry<String,HashMap<String,String>>> configurationMatrixEntries = configurationMatrix.entrySet();
		Iterator<Entry<String,HashMap<String,String>>> configurationMatrixIter = configurationMatrixEntries.iterator();
		while(configurationMatrixIter.hasNext()){
			ArrayList<String[]> vector = new ArrayList<String[]>();
			Map.Entry<String, HashMap<String,String>> configurationMatrixEntry = configurationMatrixIter.next();
			String processId = configurationMatrixEntry.getKey();
			HashMap<String,String> configurations = configurationMatrixEntry.getValue();
			for(NodeProcess f : configurableModel.getConfigurableActivities()){
				String[] entry = new String[2];
				entry[0]= f.getId();
				entry[1]= configurations.get(f.getId());
				vector.add(entry);
			}
			for(NodeProcess c : configurableModel.getConfigurableGateways()){
				String[] entry = new String[2];
				entry[0]= c.getId();
				entry[1]= configurations.get(c.getId());
				vector.add(entry);
			}
			dataset.add(vector);
		}
		return dataset;
	}
	

	public ArrayList<Object> saveConfigurationMatrixToARFFFile() throws Exception{
		
		ArrayList<Object> params = new ArrayList<Object>();
		
		//String fileName = "F:/Process_"+configurableModel.getProcessId()+".arff";
		String fileName = "/home/nour/repository/Process_"+configurableModel.getProcessId()+".arff";
		
		ArrayList<ArrayList<String[]>> configurationDataset = getConfigurationMatrixforArff();
		
		ArrayList<Attribute> attributes;
		Instances dataSet;
		double[] values;
		attributes = new ArrayList<Attribute>();
		
		int numAtts = configurationDataset.get(0).size();
		
		int i=0;
		for(NodeProcess f : configurableModel.getConfigurableActivities()){
			attributes.add(new Attribute(f.getId(),(ArrayList)null));
			i++;
		}

		for(NodeProcess c : configurableModel.getConfigurableGateways()){
			attributes.add(new Attribute(c.getId(),(ArrayList)null));
			i++;
		}
		
		dataSet = new Instances(configurableModel.getProcessName(), attributes, 0);


		for (ArrayList<String[]> vector : configurationDataset)
		{
			//print(vector);
			values = new double[dataSet.numAttributes()]; 
			for(String[] conf : vector){
				// Create empty instance with three attribute values
				
				// Set instance's values for the attributes
				for(int k =0; k<attributes.size();k++){
					Attribute a = (Attribute)attributes.get(k);
					if(a.name().equals(conf[0])){
						//System.out.println(a.name()+"***"+conf[1]);
						if(conf[1]!=null)
							values[k] = (double)dataSet.attribute(k).addStringValue(conf[1]);
						else
							values[k] = (double)dataSet.attribute(k).addStringValue("?");
						break;
					}
				}
			}
			dataSet.add(new DenseInstance(1.0, values));
		}
		
		
		NonSparseToSparse nonSparseToSparseInstance = new NonSparseToSparse(); 
		nonSparseToSparseInstance.setInputFormat(dataSet); 
		Instances sparseDataset = Filter.useFilter(dataSet, nonSparseToSparseInstance);
		//System.out.println(sparseDataset);  
		ArffSaver arffSaverInstance = new ArffSaver(); 
		arffSaverInstance.setInstances(sparseDataset); 
		//arffSaverInstance.setFile(new File("F:/ARFF-files/"+configurableModel.getProcessName()+".arff")); 
		arffSaverInstance.setFile(new File(fileName)); 
		arffSaverInstance.writeBatch();
		
		
		params.add(fileName);
		params.add(numAtts);
		
		return params;
	}
}
