package utils;

import java.util.ArrayList;

import cecj.neat.NeatIndividual;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Statistics;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class NetworkVisualizerStatistics extends Statistics {

	public Converter converter;
	NetworkVisualizer visualizer;
	Fitness maxFitness;
	
	@Override
	public void setup(EvolutionState state, Parameter base) {		
		super.setup(state, base);
		
		converter = (Converter) state.parameters.getInstanceForParameter(base.push("converter"), null, Converter.class);
		visualizer = new NetworkVisualizer();
		maxFitness = null;
	}
	
	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
		
		int maxIndex = -1;
		if(maxFitness == null)
		{
			maxFitness = state.population.subpops[0].individuals[0].fitness;
			maxIndex = 0;
		}
		
		for(int i = 0; i < state.population.subpops[0].individuals.length; i++)
		{
			
			/*if(i == 31)
			{
				ArrayList<Link> l;
				NeatIndividual n = (NeatIndividual) state.population.subpops[0].individuals[i];
				l = converter.convert(state.population.subpops[0].individuals[i]);
				
				visualizer.setLinks(l);
				
				visualizer.saveToFile(i + ".png");
			}*/
			
			
			if(state.population.subpops[0].individuals[i].fitness.betterThan(maxFitness))
			{
				maxFitness = state.population.subpops[0].individuals[i].fitness;
				maxIndex = i;
			}
		}
		
		if(maxIndex > -1)
		{
			ArrayList<Link> l;
			l = converter.convert(state.population.subpops[0].individuals[maxIndex]);
			
			visualizer.setLinks(l);
			
			visualizer.visualizeNetwork();
		}
	} 
	
	
}
