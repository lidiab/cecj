package cecj.neat;

import java.util.Dictionary;
import java.util.HashMap;

import cecj.utils.Pair;

import ec.EvolutionState;
import ec.Individual;
import ec.Species;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class NeatSpecies extends Species {

	public double addLinkMutationProbability;
	public double addNodeMutationProbability;
	public double weightMutationProbability;
	public double randomWeightProbability; 
	
	public int tryAddNumber;
	
	public int inNodesNumber;
	public int outNodesNumber;
	public int hiddenNodesNumber = 0;
	
	public int initialLinkNumber;
	public String initialGenotypeTopology;
	public String topologySize;
	
	public double c1 = 1.0;
	public double c2 = 1.0;
	public double c3 = 0.4;
	public double dt = 3.0;
	
	public int innovationNumber = 0;
	
	HashMap<Integer, int[]> nodeMutationHistory = new HashMap<Integer, int[]>();
	HashMap<Pair<Integer>, Integer> linkMutationHistory = new HashMap<Pair<Integer>, Integer>();
	
	public NeatIndividual i_prototype;
	
	NeatIndividual newIndividual = null;
	
	public int[] findGeneInNodeHistory(int g)
	{
		return nodeMutationHistory.get(g);
	}
	
	public void addGeneToNodeHistory(int gene, int newGene1, int newGene2, int node)
	{
		nodeMutationHistory.put(gene, new int[]{newGene1, newGene2, node});
	}
	
	public Integer findGeneInLinkHistory(int inNode, int outNode)
	{
		return linkMutationHistory.get(new Pair<Integer> (inNode, outNode));
	}
	
	public void addGeneToLinkHistory(int innode, int outnode, int link)
	{
		linkMutationHistory.put(new Pair<Integer>(innode, outnode), link);
	}
	
	
	public Parameter defaultBase() {
		return new Parameter("NeatNeuralNetworkSpecies");
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		//i_prototype = new NeatNeuralNetworkIndividual(inNodesNumber, outNodesNumber, initialLinkNumber, state.random[thread]);
		super.setup(state, base);
		
		if(state.parameters.exists(base.push("tryAddNumber")))
		{
			tryAddNumber = state.parameters.getInt(base.push("tryAddNumber"), null);
		}
		else
		{
			tryAddNumber = 30;
		}
		addLinkMutationProbability = state.parameters.getDouble(base.push("addLinkMutationProbability"), null);
		addNodeMutationProbability = state.parameters.getDouble(base.push("addNodeMutationProbability"), null);
		weightMutationProbability = state.parameters.getDouble(base.push("weightMutationProbability"), null);
		randomWeightProbability = state.parameters.getDouble(base.push("randomWeightProbability"), null);
		inNodesNumber = state.parameters.getInt(base.push("inNodesNumber"), null);
		outNodesNumber = state.parameters.getInt(base.push("outNodesNumber"), null);
		if(state.parameters.exists(base.push("initialLinkNumber")))
		{
			initialLinkNumber = state.parameters.getInt(base.push("initialLinkNumber"), null);
		}
		else
		{
			initialLinkNumber = 0;
		}
		if(state.parameters.exists(base.push("initialGenotypeTopology")))
		{
			initialGenotypeTopology = state.parameters.getString(base.push("initialGenotypeTopology"), null);
		}
		else
		{
			initialGenotypeTopology = null;
		}
		if(state.parameters.exists(base.push("topologySize")))
		{
			topologySize = state.parameters.getString(base.push("topologySize"), null);
		}
		else
		{
			topologySize = null;
		}
	}
	
	@Override
	public Individual newIndividual(EvolutionState state, int thread) {
		if (newIndividual == null)
		{
			if(topologySize != null)
			{
				newIndividual = new NeatIndividual(topologySize, 
						state.random[thread], this);
			}
			else if(initialGenotypeTopology != null)
			{
				newIndividual = new NeatIndividual(initialGenotypeTopology, 
						state.random[thread], this);
			}
			else
			{
				newIndividual = new NeatIndividual(inNodesNumber, 
						outNodesNumber, initialLinkNumber, state.random[thread], this);
			}
			newIndividual.fitness = new SimpleFitness();
		}
		NeatIndividual ind = (NeatIndividual)newIndividual.clone();
		

		for(NeatIndividual.Gene g: ind.genotype)
		{
			g.weight = ind.newWeight(state.random[thread]);
		}
		
		return (Individual) ind;
	}
}
