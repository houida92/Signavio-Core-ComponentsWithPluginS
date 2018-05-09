package com.signavio.warehouse.configuration.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.signavio.warehouse.configuration.util.SimilarityCalculator;

public class ConfigurationSimilarity {

	protected BPMNModel configurableModel;
	protected BPMNModel configurationModel;
	SimilarityCalculator calc;
	
	public ConfigurationSimilarity(){calc = new SimilarityCalculator(0.5,1.0,1.0);}
	public ConfigurationSimilarity(BPMNModel configurable, BPMNModel configuration){
		configurableModel = configurable;
		configurationModel = configuration;
		calc = new SimilarityCalculator(0.47,1.0,1.0);
	}
	
	public BPMNModel getConfigurableModel(){return configurableModel;}
	public BPMNModel getConfigurationModel(){return configurationModel;}
	
	//get configurable activities similarities vector
	public HashMap<NodeProcess,HashMap<NodeProcess,Double>> getConfigurableActivitySimilarityVector(){
		HashMap<NodeProcess, HashMap<NodeProcess,Double>> activitySimilarityVector = new HashMap<NodeProcess,HashMap<NodeProcess,Double>>();
		ArrayList<NodeProcess> configurableActivities = configurableModel.getConfigurableActivities();
		if(configurableActivities.size()>0){
			ArrayList<NodeProcess> activities = configurationModel.getActivities();
			
			//System.out.println("ii-"+activities.size());
			
			if(activities.size()>0){
				//for each configurable function compute its similarity with each function in the configuration model
				for(NodeProcess cf : configurableActivities){
					HashMap<NodeProcess, Double> similarityVector = new HashMap<NodeProcess,Double>();
					for(NodeProcess f : activities){
						Double sim = calc.getSimilarityLabel(cf.getName(), f.getName());
						similarityVector.put(f, sim);
					}
					activitySimilarityVector.put(cf, similarityVector);
				}
			}
		}
		return activitySimilarityVector;
	}
	
	//get the best mapping for each function or null hashmap if no mapping
	public HashMap<NodeProcess, HashMap<NodeProcess,Double>> getconfigurableActivitiesBestMapping(){
		HashMap<NodeProcess,HashMap<NodeProcess,Double>> activitySimilarityVector  = getConfigurableActivitySimilarityVector();
		HashMap<NodeProcess, HashMap<NodeProcess,Double>> activityBestMappings = new HashMap<NodeProcess, HashMap<NodeProcess,Double>>();
		if(activitySimilarityVector.size()>0){
			Set<Map.Entry<NodeProcess, HashMap<NodeProcess,Double>>> entries = activitySimilarityVector.entrySet();
			Iterator<Map.Entry<NodeProcess, HashMap<NodeProcess,Double>>> iter = entries.iterator();
			while(iter.hasNext()){
				Map.Entry<NodeProcess, HashMap<NodeProcess,Double>> entry = iter.next();
				NodeProcess cf = entry.getKey();
				HashMap<NodeProcess,Double> simVector = entry.getValue();
				HashMap<NodeProcess,Double> bestMapping = calc.getBestMapping(cf, simVector);
				
				//System.out.println("iii-"+bestMapping.size());
				
				activityBestMappings.put(cf, bestMapping);
			}
		}
		return activityBestMappings;
	}
	
	//get similarity vector of a function in the configurable process model
	public HashMap<NodeProcess,Double> getactivitySimilarityVector(NodeProcess cf){
		 HashMap<NodeProcess,Double> activitySimilarityVector = new HashMap<NodeProcess,Double>();
		ArrayList<NodeProcess> activities = configurationModel.getActivities();
		if(activities.size()>0){
			for(NodeProcess f : activities){
				Double sim = calc.getSimilarityLabel(cf.getName(), f.getName());
				activitySimilarityVector.put(f, sim);
			}
		}
		return activitySimilarityVector;
	}
	
	//get the best mapping for the function cf
	public HashMap<NodeProcess,Double> getActivityBestMapping(NodeProcess cf){
		HashMap<NodeProcess,Double> activitySimilarityVector  = getactivitySimilarityVector(cf);
		HashMap<NodeProcess,Double> activityBestMappings = new  HashMap<NodeProcess,Double>();
		if(activitySimilarityVector.size()>0){
			activityBestMappings = calc.getBestMapping(cf, activitySimilarityVector);
		}
		return activityBestMappings;
	}
	
	//get gateway similarity vector
	//similarity based on preset activity best mapping or postset activity best mapping
	//if best mapping appartient au preset then add
	//if not then add path[0] which is a gateway certainly
	//if sim vector is empty then we have to search the sequences
	public HashMap<NodeProcess,ArrayList<NodeProcess>> getGatewaySimilarityVector(NodeProcess cg){
		HashMap<NodeProcess,ArrayList<NodeProcess>> gatewaySimilarityVector = new HashMap<NodeProcess,ArrayList<NodeProcess>>();
		//join case
		HashMap<NodeProcess,HashMap<NodeProcess,Double>> activityEventBestMappings = new HashMap<NodeProcess,HashMap<NodeProcess,Double>>();
		
		//join case
		if(configurableModel.getPreset(cg).size()>1){
			ArrayList<NodeProcess> presetActivity = new ArrayList<NodeProcess>();
			configurableModel.getPresetActivity(cg, presetActivity);
			for(NodeProcess presetA : presetActivity){
				if(presetA.getType().equals("Task")){
						HashMap<NodeProcess,Double> bestcfMapping = getActivityBestMapping(presetA);
						activityEventBestMappings.put(presetA, bestcfMapping);
				}
				
				//compute the number of mappings between g and the configurable gateway --> this is the similarity
				ArrayList<NodeProcess> gateways = configurationModel.getGateways();
				if(gateways.size()>0){
					for(NodeProcess g : gateways){
						ArrayList<NodeProcess> BM = new ArrayList<NodeProcess>();
						//if g is a split
						if((cg.getType().equals("OR") || (cg.getType().equals(g.getType()))) && configurationModel.getPreset(g).size()>1){
							ArrayList<NodeProcess> configurationPresetActivity = new ArrayList<NodeProcess>();
							configurationModel.getPresetActivity(g, configurationPresetActivity);
							//for each best mapping above test if it belongs to g preset activity
							//if yes then add to BM
							//first test the event and activities
							if(activityEventBestMappings.size()>0){
								Set<Entry<NodeProcess,HashMap<NodeProcess,Double>>> activityEventBestMappingsEntries = activityEventBestMappings.entrySet();
								Iterator<Entry<NodeProcess,HashMap<NodeProcess,Double>>> activityEventBestMappingsIter = activityEventBestMappingsEntries.iterator();
								while(activityEventBestMappingsIter.hasNext()){
									Map.Entry<NodeProcess, HashMap<NodeProcess, Double>> activityEventBestMappingsEntry = activityEventBestMappingsIter.next();
									//the preset activity in the configurable model
									NodeProcess configurablePresetActivity = activityEventBestMappingsEntry.getKey();
									HashMap<NodeProcess,Double> configurationPresetBestMapping = activityEventBestMappingsEntry.getValue();
									//contains the mapping activity in the configuration model and the similarity
									if(configurationPresetBestMapping.size()>0){
										Set<Entry<NodeProcess,Double>> configurationPresetBestMappingEntries = configurationPresetBestMapping.entrySet();
										Iterator<Entry<NodeProcess,Double>> configurationPresetBestMappingIter = configurationPresetBestMappingEntries.iterator();
										while(configurationPresetBestMappingIter.hasNext()){
											Map.Entry<NodeProcess, Double> configurationPresetBestMappingEntry = configurationPresetBestMappingIter.next();
											//a preset activity in the configuration model
											NodeProcess configurationPresetNodeProcess = configurationPresetBestMappingEntry.getKey();
											boolean found = false;
											//search if the configuration preset NodeProcess is in the preset activity of the configuration gateway
											for(NodeProcess configurationPreset : configurationPresetActivity){
												if(configurationPresetNodeProcess.getId().equals(configurationPreset.getId())){
													//if configurablePresetActivity belongs to the preset and BM does not contain it then add it
													ArrayList<NodeProcess> configurablePreset = configurableModel.getPreset(cg);
													for(NodeProcess preset : configurablePreset){
														if(configurablePresetActivity.getId().equals(preset.getId())){
															if(!BM.contains(configurablePresetActivity)){
																BM.add(configurablePresetActivity);
																found = true;
																break;
															}
															found = true;
															break;
														}
													}
													//configurablePresetActivity is not in the direct preset, then we must take the preset of the corresponding path
													if(found==false){
														ArrayList<NodeProcess> path = new ArrayList<NodeProcess>();
														configurableModel.getPath(configurablePresetActivity, cg, path);
														if(path.size()>0){
															for(NodeProcess p : path){
																if(configurablePreset.contains(p)){
																	if(!BM.contains(p)){
																		BM.add(p);
																		found = true;
																		break;
																	}
																	found = true;
																	break;
																}
															}
														}
													}
												}
											}
											if(found == true)
												break;
										}
									}
								}
							}
							
							if(BM.size()>1)
								gatewaySimilarityVector.put(g, BM);
							else if(BM.size()==1 && !cg.getType().equals("AND")){
								NodeProcess bm = BM.get(0);
								NodeProcess sequence = new NodeProcess("Seq-"+bm.getId(),"Seq","Seq",false);
								gatewaySimilarityVector.put(sequence, BM);
							}
							//else no mapping has been found for the configurable gateway ==> search for sequence
							//we have to create a new NodeProcess with type "Seq"
							//the id of this NodeProcess is Seq-idPreset or postset
						}
					}
				}
			}
		}
		
		//split case
		else{
			ArrayList<NodeProcess> postsetActivity = new ArrayList<NodeProcess>();
			configurableModel.getPostsetActivity(cg, postsetActivity);
			for(NodeProcess postsetA : postsetActivity){
				if(postsetA.getType().equals("Task")){
					HashMap<NodeProcess,Double> bestcfMapping = getActivityBestMapping(postsetA);
					activityEventBestMappings.put(postsetA, bestcfMapping);
				}
			}
			
			//compute the number of mappings between g and the configurable gateway --> this is the similarity
			ArrayList<NodeProcess> gateways = configurationModel.getGateways();
			if(gateways.size()>0){
				for(NodeProcess g : gateways){
					ArrayList<NodeProcess> BM = new ArrayList<NodeProcess>();
					//if g is a split
					if((cg.getType().equals("OR") || (cg.getType().equals(g.getType()))) && configurationModel.getPostset(g).size()>1){
						ArrayList<NodeProcess> configurationPostsetActivity = new ArrayList<NodeProcess>();
						configurationModel.getPostsetActivity(g, configurationPostsetActivity);
						//for each best mapping above test if it belongs to g preset activity
						//if yes then add to BM
						//first test the event and activities
						if(activityEventBestMappings.size()>0){
							Set<Entry<NodeProcess,HashMap<NodeProcess,Double>>> activityEventBestMappingsEntries = activityEventBestMappings.entrySet();
							Iterator<Entry<NodeProcess,HashMap<NodeProcess,Double>>> activityEventBestMappingsIter = activityEventBestMappingsEntries.iterator();
							while(activityEventBestMappingsIter.hasNext()){
								Map.Entry<NodeProcess, HashMap<NodeProcess, Double>> activityEventBestMappingsEntry = activityEventBestMappingsIter.next();
								//the preset activity in the configurable model
								NodeProcess configurablePostsetActivity = activityEventBestMappingsEntry.getKey();
								HashMap<NodeProcess,Double> configurationPostsetBestMapping = activityEventBestMappingsEntry.getValue();
								//contains the mapping activity in the configuration model and the similarity
								if(configurationPostsetBestMapping.size()>0){
									Set<Entry<NodeProcess,Double>> configurationPostsetBestMappingEntries = configurationPostsetBestMapping.entrySet();
									Iterator<Entry<NodeProcess,Double>> configurationPostsetBestMappingIter = configurationPostsetBestMappingEntries.iterator();
									while(configurationPostsetBestMappingIter.hasNext()){
										Map.Entry<NodeProcess, Double> configurationPostsetBestMappingEntry = configurationPostsetBestMappingIter.next();
										//a preset activity in the configuration model
										NodeProcess configurationPostsetNodeProcess = configurationPostsetBestMappingEntry.getKey();
										boolean found = false;
										//search if the configuration preset NodeProcess is in the preset activity of the configuration gateway
										for(NodeProcess configurationPostset : configurationPostsetActivity){
											if(configurationPostsetNodeProcess.getId().equals(configurationPostset.getId())){
												//if configurablePresetActivity belongs to the preset and BM does not contain it then add it
												ArrayList<NodeProcess> configurablePostset = configurableModel.getPostset(cg);
												for(NodeProcess postset : configurablePostset){
													if(configurablePostsetActivity.getId().equals(postset.getId())){
														if(!BM.contains(configurablePostsetActivity)){
															BM.add(configurablePostsetActivity);
															found = true;
															break;
														}
														found = true;
														break;
													}
												}
												//configurablePresetActivity is not in the direct preset, then we must take the preset of the corresponding path
												if(found==false){
													ArrayList<NodeProcess> path = new ArrayList<NodeProcess>();
													configurableModel.getPath(cg, configurablePostsetActivity, path);
													if(path.size()>0){
														for(NodeProcess p : path){
															if(configurablePostset.contains(p)){
																if(!BM.contains(p)){
																	BM.add(p);
																	found = true;
																	break;
																}
																found = true;
																break;
															}
														}
													}
												}
											}
										}
										if(found == true)
											break;
									}
								}
							}
						}
						
						if(BM.size()>1)
							gatewaySimilarityVector.put(g, BM);
						else if(BM.size()==1 && !cg.getType().equals("AND")){
							NodeProcess bm = BM.get(0);
							NodeProcess sequence = new NodeProcess("Seq-"+bm.getId(),"Seq","Seq",false);
							gatewaySimilarityVector.put(sequence, BM);
						}
						//else no mapping has been found for the configurable gateway ==> search for sequence
						//we have to create a new NodeProcess with type "Seq"
						//the id of this NodeProcess is Seq-idPreset or postset
					}
				}
			}
		}
		
		if(gatewaySimilarityVector.size()==0 && !cg.getType().equals("AND")){
			if(activityEventBestMappings.size()>0){
				Set<Map.Entry<NodeProcess, HashMap<NodeProcess,Double>>> activityEventBestMappingsEntries = activityEventBestMappings.entrySet();
				Iterator<Map.Entry<NodeProcess, HashMap<NodeProcess,Double>>> activityEventBestMappingsIter = activityEventBestMappingsEntries.iterator();
				while(activityEventBestMappingsIter.hasNext()){
					Map.Entry<NodeProcess, HashMap<NodeProcess,Double>> activityEventBestMappingsEntry = activityEventBestMappingsIter.next();
					NodeProcess cgFunctionEvent = activityEventBestMappingsEntry.getKey();
					NodeProcess sequence= new NodeProcess("Seq-"+cgFunctionEvent.getId(),"Seq","Seq",false);
					ArrayList<NodeProcess> mappedActivities = new ArrayList<NodeProcess>();
					mappedActivities.add(cgFunctionEvent);
					gatewaySimilarityVector.put(sequence, mappedActivities);
					break;
				}
			}
		}

		return gatewaySimilarityVector;
	}
	
	public HashMap<NodeProcess,ArrayList<NodeProcess>> getGatewayBestMapping(NodeProcess cg){
	
		int denom =0;
		if(configurableModel.getPreset(cg).size()>1)
			denom = configurableModel.getPreset(cg).size();
		else
			denom = configurableModel.getPostset(cg).size();
		
		HashMap<NodeProcess,ArrayList<NodeProcess>> gatewayBestMapping = new HashMap<NodeProcess,ArrayList<NodeProcess>>();
		HashMap<NodeProcess,ArrayList<NodeProcess>> gatewaySimilarityVector = getGatewaySimilarityVector(cg);
		//similarity is the number of mapped preset/ total number of preset
		//NB not preset activity but only preset
		HashMap<NodeProcess,Double> simVector = new HashMap<NodeProcess,Double>();
		if(gatewaySimilarityVector.size()>0){
			Set<Map.Entry<NodeProcess,ArrayList<NodeProcess>>> gatewaySimilarityVectorEntries = gatewaySimilarityVector.entrySet();
			Iterator<Map.Entry<NodeProcess,ArrayList<NodeProcess>>> gatewaySimilarityVectorIter = gatewaySimilarityVectorEntries.iterator();
			while(gatewaySimilarityVectorIter.hasNext()){
				Map.Entry<NodeProcess,ArrayList<NodeProcess>> gatewaySimilarityVectorEntry = gatewaySimilarityVectorIter.next();
				//this is the configuration gateway or the sequence
				NodeProcess configurationNodeProcess = gatewaySimilarityVectorEntry.getKey();
				ArrayList<NodeProcess> mappedSet = gatewaySimilarityVectorEntry.getValue();
				double size = (double) mappedSet.size()/denom;
	
				simVector.put(configurationNodeProcess, size);
			}
		

			HashMap<NodeProcess,Double> bestMapping = calc.getBestMapping(cg, simVector);
			//get the arraylist NodeProcess corresponding to the NodeProcess key in bestMapping and put the NodeProcess and the corresponding arraymist in a new hashmap
			Collection<NodeProcess> bestMappingKeys = bestMapping.keySet();
			Collection<NodeProcess> BMNodeProcesss = gatewaySimilarityVector.keySet();
			//HashMap<NodeProcess,ArrayList<NodeProcess>> bestMappingWithNodeProcesss = new HashMap<NodeProcess, ArrayList<NodeProcess>>();
			for(NodeProcess bestMappingNodeProcess : bestMappingKeys){
				for(NodeProcess BMNodeProcess : BMNodeProcesss){
					if(bestMappingNodeProcess.getId().equals(BMNodeProcess.getId())){
						ArrayList<NodeProcess> BMNodeProcesssValue = gatewaySimilarityVector.get(BMNodeProcess);
						gatewayBestMapping.put(BMNodeProcess, BMNodeProcesssValue);
						return gatewayBestMapping;
					}
				}
			}
		}
		//no connector has been found then search sequence
		
		return gatewayBestMapping;
	}
	
	
	public HashMap<NodeProcess,HashMap<NodeProcess,ArrayList<NodeProcess>>> getConfigurableGatewaySimilarityVector(){
		HashMap<NodeProcess, HashMap<NodeProcess,ArrayList<NodeProcess>>> gatewaySimilarityVector = new HashMap<NodeProcess,HashMap<NodeProcess,ArrayList<NodeProcess>>>();
		ArrayList<NodeProcess> configurableGateways = configurableModel.getConfigurableGateways();
		if(configurableGateways.size()>0){
			ArrayList<NodeProcess> gateways = configurationModel.getGateways();
			if(gateways.size()>0){
				//for each configurable gateway compute its similarity with each gateway in the configuration model
				for(NodeProcess cg : configurableGateways){
					HashMap<NodeProcess, ArrayList<NodeProcess>> similarityVector = new HashMap<NodeProcess,ArrayList<NodeProcess>>();
					similarityVector = getGatewaySimilarityVector(cg);
					gatewaySimilarityVector.put(cg, similarityVector);
				}
			}
		}
		return gatewaySimilarityVector;
	}
	
	public HashMap<NodeProcess,HashMap<NodeProcess,ArrayList<NodeProcess>>> getConfigurableGatewaysBestMapping(){
		HashMap<NodeProcess,HashMap<NodeProcess,ArrayList<NodeProcess>>> gatewaySimilarityVector  = getConfigurableGatewaySimilarityVector();
		HashMap<NodeProcess, HashMap<NodeProcess,ArrayList<NodeProcess>>> gatewaysBestMappings = new HashMap<NodeProcess, HashMap<NodeProcess,ArrayList<NodeProcess>>>();
		if(gatewaySimilarityVector.size()>0){
			Set<Map.Entry<NodeProcess, HashMap<NodeProcess,ArrayList<NodeProcess>>>> entries = gatewaySimilarityVector.entrySet();
			Iterator<Map.Entry<NodeProcess, HashMap<NodeProcess,ArrayList<NodeProcess>>>> iter = entries.iterator();
			while(iter.hasNext()){
				Map.Entry<NodeProcess, HashMap<NodeProcess,ArrayList<NodeProcess>>> entry = iter.next();
				NodeProcess cg = entry.getKey();
				HashMap<NodeProcess,ArrayList<NodeProcess>> bestMappingWithNodeProcesss = new HashMap<NodeProcess, ArrayList<NodeProcess>>();
				bestMappingWithNodeProcesss = getGatewayBestMapping(cg);
				gatewaysBestMappings.put(cg, bestMappingWithNodeProcesss);
			}
		}
		return gatewaysBestMappings;
	}
	
	
}