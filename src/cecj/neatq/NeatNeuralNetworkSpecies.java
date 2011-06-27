package cecj.neatq;

import java.util.Dictionary;
import java.util.HashMap;

import cecj.utils.Pair;

import ec.EvolutionState;
import ec.Individual;
import ec.Species;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class NeatNeuralNetworkSpecies extends Species {

	public double addLinkMutationProbability;
	public double addNodeMutationProbability;
	public int inNodesNumber;
	public int outNodesNumber;
	public int hiddenNodesNumber = 0;
	public int initialLinkNumber;
	
	public double weightMutationProbability;
	public double randomWeightProbability; 
	
	public int innovationNumber = 0;
	
	public HashMap<Integer, int[]> nodeMutGeneHistory = new HashMap<Integer, int[]>();
	public HashMap<Pair<Integer>, Integer> linkMutGeneHistory = new HashMap<Pair<Integer>, Integer>();
	
	public NeatIndividual i_prototype;
	
	NeatIndividual newIndywidual = null;
	
	public int[] findGeneInNodeHistory(int g)
	{
		return nodeMutGeneHistory.get(g);
	}
	
	public void addGeneToNodeHistory(int gene, int newGene1, int newGene2, int node)
	{
		nodeMutGeneHistory.put(gene, new int[]{newGene1, newGene2, node});
	}
	
	public Integer findGeneInLinkHistory(int inNode, int outNode)
	{
		return linkMutGeneHistory.get(new Pair<Integer> (inNode, outNode));
	}
	
	public void addGeneToLinkHistory(int innode, int outnode, int link)
	{
		linkMutGeneHistory.put(new Pair<Integer>(innode, outnode), link);
	}
	
	
	public Parameter defaultBase() {
		return new Parameter("NeatNeuralNetworkSpecies");
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		//i_prototype = new NeatNeuralNetworkIndividual(inNodesNumber, outNodesNumber, initialLinkNumber, state.random[thread]);
		super.setup(state, base);
		
		addLinkMutationProbability = state.parameters.getDouble(base.push("addLinkMutationProbability"), null);
		addNodeMutationProbability = state.parameters.getDouble(base.push("addNodeMutationProbability"), null);
		inNodesNumber = state.parameters.getInt(base.push("inNodesNumber"), null);
		outNodesNumber = state.parameters.getInt(base.push("outNodesNumber"), null);
		initialLinkNumber = state.parameters.getInt(base.push("initialLinkNumber"), null);
		weightMutationProbability = state.parameters.getDouble(base.push("weightMutationProbability"), null);
		randomWeightProbability = state.parameters.getDouble(base.push("randomWeightProbability"), null);
	}
	
	@Override
	public Individual newIndividual(EvolutionState state, int thread) {
		if (newIndywidual == null)
		{
			newIndywidual = new NeatIndividual(inNodesNumber, 
					outNodesNumber, initialLinkNumber, state.random[thread], this);
			//newIndywidual.species = this;
			newIndywidual.fitness = new SimpleFitness();
		}
		NeatIndividual ind = (NeatIndividual)newIndywidual.clone();
		

		for(NeatIndividual.Gene g: ind.genotype)
		{
			g.weight = ind.newWeight(state.random[thread]);
		}
		
		return (Individual) ind;
	}
}
