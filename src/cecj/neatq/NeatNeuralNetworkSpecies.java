package cecj.neatq;

import java.io.DataInput;
import java.io.IOException;
import java.io.LineNumberReader;

import ec.EvolutionState;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;

public class NeatNeuralNetworkSpecies extends Species {

	public double addLinkMutationProbability;
	public double addNodeMutationProbability;
	public int inNodesNumber;
	public int outNodesNumber;
	public int initialLinkNumber;
	
	public Parameter defaultBase() {
		return null;
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		
		addLinkMutationProbability = state.parameters.getDouble(base.push("addLinkMutationProbability"), null);
		addNodeMutationProbability = state.parameters.getDouble(base.push("addNodeMutationProbability"), null);
		inNodesNumber = state.parameters.getInt(base.push("inNodesNumber"), null);
		outNodesNumber = state.parameters.getInt(base.push("outNodesNumber"), null);
		initialLinkNumber = state.parameters.getInt(base.push("initialLinkNumber"), null);
	}
	
	@Override
	public Individual newIndividual(EvolutionState state, int thread) {
		NeatNeuralNetworkIndividual newIndywidual;
		newIndywidual = new NeatNeuralNetworkIndividual(inNodesNumber, 
				outNodesNumber, initialLinkNumber, state.random[thread]);
		newIndywidual.species = this;
		return newIndywidual;
	}
}

