package cecj.neatq;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class NeatCrossoverPipeline extends BreedingPipeline {

	public Parameter defaultBase() {
		return new Parameter("NeatCrossover");
	}

	public static final int NUM_SOURCES = 2;
	NeatIndividual[] parents;
	
	public NeatCrossoverPipeline()
	{
		parents = new NeatIndividual[2];
	}
	
	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {
		int n = typicalIndsProduced();
		n = Math.max(Math.min(n, max), min);

		for (int i = start; i < n + start; i++) {
			if (sources[0] == sources[1]) {
				sources[0].produce(2, 2, 0, subpopulation, parents, state, thread);
				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (NeatIndividual) (parents[0].clone());
					parents[1] = (NeatIndividual) (parents[1].clone());
				}
			} else {
				sources[0].produce(1, 1, 0, subpopulation, parents, state, thread);
				sources[1].produce(1, 1, 1, subpopulation, parents, state, thread);
				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (NeatIndividual) (parents[0].clone());
				}
				if (!(sources[1] instanceof BreedingPipeline)) {
					parents[1] = (NeatIndividual) (parents[1].clone());
				}
			}

			parents[0].crossover(parents[1], state.random[thread]);
			parents[0].evaluated = false;

			inds[i] = parents[0];
		}

		return n;
	}

}
