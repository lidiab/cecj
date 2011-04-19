package cecj.neatq;

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
	public int initialLinkNumber;
	
	public NeatNeuralNetworkIndividual i_prototype;
	
	NeatNeuralNetworkIndividual newIndywidual = null;
	
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
	}
	
	@Override
	public Individual newIndividual(EvolutionState state, int thread) {
		if (newIndywidual == null)
		{
			newIndywidual = new NeatNeuralNetworkIndividual(inNodesNumber, 
					outNodesNumber, initialLinkNumber, state.random[thread]);
			newIndywidual.species = this;
			newIndywidual.fitness = new SimpleFitness();
		}
		return (Individual) newIndywidual.clone();
	}
}

