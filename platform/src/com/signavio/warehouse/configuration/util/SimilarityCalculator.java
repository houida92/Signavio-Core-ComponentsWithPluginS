package com.signavio.warehouse.configuration.util;

import java.util.*;

import com.signavio.warehouse.configuration.business.NodeProcess;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SimilarityCalculator {

	private LabelSimilarity checker = null;

	private double threshold=0.5;
	private double semanticWeight = 0;
	private double syntaxWeight = 1.0;


	public SimilarityCalculator(){}
	public SimilarityCalculator(double threshold, double syntaxWeight, double semanticWeight) {
		this.threshold = threshold;
		this.semanticWeight = semanticWeight;
		this.syntaxWeight = syntaxWeight;
		//this.structureWeight = structureWeight;
		if (checker == null) {
			this.checker = new LabelSimilarity("C:/Users/Ahmed/workspace/Signavio Core Components/External/similarity/");
			//this.checker = new LabelSimilarity("/home/nour/repository/similarity/");
		}
	}

	// calculates the similarity of two Context fragments,
	private double getSyntacticEquivalence(String f1, String f2) {
		if (!(syntaxWeight > 0)) {
			return 0;
		}
		String activityLabel1 = f1;
		String activityLabel2 = f2;
		// compare the two and return
		return checker.syntacticEquivalenceScore(activityLabel1, activityLabel2);
	}

	private double getSemanticEquivalence(String f1, String f2) {
		if (!(semanticWeight > 0)) {
			return 0;
		}
		String activityLabel1 = f1;
		String activityLabel2 = f2;
		// compare the two and return
		return checker.semanticEquivalenceScore(activityLabel1, activityLabel2);
	}

	public double getSimilarityLabel(String l1, String l2)
	{
		double sim= (double) (getSyntacticEquivalence(l1,l2) + getSemanticEquivalence(l1,l2))/2;
		return sim;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getThreshold() {
		return threshold;
	}
	
	
	//compute the similarity between one activity and all others activity in the same process model
	public HashMap<ArrayList<String>,Double> getSimilarityVector(String s1 , ArrayList<String> l2)
	{
		HashMap<ArrayList<String>, Double> similarities = new HashMap<ArrayList<String>,Double>();
		for(String s2 : l2)
		{
			double sim = getSimilarityLabel(s1,s2);
			ArrayList<String> entry = new ArrayList<String>();
			entry.add(s1);
			entry.add(s2);
			similarities.put(entry, sim);
		}
		return similarities;
	}
	
	 public  HashMap<NodeProcess,Double> getBestMapping(NodeProcess NodeProcess, HashMap<NodeProcess,Double> simVector)
	 {
		 Collection<Double> values = simVector.values();
		 HashMap<NodeProcess,Double> mapping = new HashMap<NodeProcess,Double>();
		 double max = Collections.max(values);
		 if(NodeProcess.getType().equals("Task")){
			 if(max>=threshold)
			 {
				 Set<Map.Entry<NodeProcess,Double>> entries = simVector.entrySet();
				 Iterator<Map.Entry<NodeProcess,Double>> iter = entries.iterator();
				 while(iter.hasNext())
				 {
					 Map.Entry<NodeProcess,Double> entry = iter.next();
					 if(entry.getValue()==max)
					 {
						 mapping.put(entry.getKey(), entry.getValue());
					 }
				 }
			 }
		 }
		 //gateway case its the maximum number of mappings if max>0
		 else{
			 if(max>0)
			 {
				 Set<Map.Entry<NodeProcess,Double>> entries = simVector.entrySet();
				 Iterator<Map.Entry<NodeProcess,Double>> iter = entries.iterator();
				 while(iter.hasNext())
				 {
					 Map.Entry<NodeProcess,Double> entry = iter.next();
					 if(entry.getValue()==max)
					 {
						 mapping.put(entry.getKey(), entry.getValue());
					 }
				 }
			 }
		 }
		 //if empty then no mapping
		 return mapping;
	 }
	 
		public static void main(String[] args){
			String s = "purchase order release";
			String s2 = "order form release";
			SimilarityCalculator sc = new SimilarityCalculator(0.5,1.0,1.0);
			System.out.println(sc.getSemanticEquivalence(s, s2));
			System.out.println(sc.getSyntacticEquivalence(s, s2));
		}

}
