package utils;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Statistics;
import ec.Subpopulation;
import ec.util.Parameter;

public class NetworkVisualizerStatistics extends Statistics {

	@Override
	public void setup(EvolutionState state, Parameter base) {		
		super.setup(state, base);
		
	}
	
	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
		
		Fitness maxFitness = state.population.subpops[0].individuals[0].fitness;
		for(Individual i : state.population.subpops[0].individuals)
		{
			if(i.fitness.betterThan(maxFitness))
			{
				maxFitness = i.fitness;
			}
		}
		
		//TODO konwersja
		
		//TODO wyœwietlanie
	} 
	
	
}
