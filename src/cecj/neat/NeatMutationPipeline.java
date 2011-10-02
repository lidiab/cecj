package cecj.neat;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;
import ec.vector.IntegerVectorSpecies;
import ec.vector.VectorDefaults;

public class NeatMutationPipeline extends BreedingPipeline 
{
	public static final String P_OURMUTATION = "our-mutation";

	public Parameter defaultBase() 
	{
		return new Parameter("NeatMutation");
	}

	public static final int NUM_SOURCES = 1;

	public int numSources() 
	{
		return NUM_SOURCES;
	}

	public int produce(final int min, final int max, final int start,
			final int subpopulation, final Individual[] inds,
			final EvolutionState state, final int thread) 
	{
		int n = sources[0].produce(min, max, start, subpopulation, inds, state,
				thread);

		if (!(sources[0] instanceof BreedingPipeline))
			for (int q = start; q < n + start; q++)
				inds[q] = (Individual) (inds[q].clone());

		if (!(inds[start] instanceof NeatIndividual))
			state.output
					.fatal("NeatMutationPipeline didn't get an NeatIndividual."
							+ "The offending individual is in subpopulation "
							+ subpopulation + " and it's:" + inds[start]);
		
		NeatSpecies species = (NeatSpecies) (inds[start].species);

		for (int q = start; q < n + start; q++) {
			NeatIndividual ind = (NeatIndividual) inds[q];
			if(state.random[thread].nextBoolean(species.addLinkMutationProbability))
			{
				ind.addLinkMutation(state.random[thread]);
			}
			if(state.random[thread].nextBoolean(species.addNodeMutationProbability))
			{
				ind.addNodeMutation(state.random[thread]);
			}
			if(state.random[thread].nextBoolean(species.weightMutationProbability))
			{
				ind.weightMutation(state.random[thread]);
			}
			ind.evaluated = false;
		}

		return n;
	}

}
