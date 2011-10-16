package cecj.neat;

import java.util.ArrayList;
import java.util.Arrays;

import cecj.eval.SimpleCoevolutionaryEvaluator;
import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class SpeciationEvaluator extends
		Evaluator {

	static class Species
	{
		Individual representant;
		int speciesNumber;
		int indsNumber = 0;
		Species(Individual r, int num)
		{
			representant = r;
			speciesNumber = num;
			indsNumber++;
		}
	}
	
	static class SpeciesInfo
	{
		ArrayList<Species> speciesList = new ArrayList<Species>();
		int[] species;
		double threshold = 3.0;  //TODO parametr
		int totalSpeciesNumber = 0;
	}
	
	SpeciesInfo[] subpopsSpecies;
	
	Evaluator innerEvaluator;
	
	int[] speciatedPopulations;
	
	@Override
	public void setup(EvolutionState state, Parameter base) {
		//super.setup(state, base);
		
		innerEvaluator = (Evaluator) state.parameters.getInstanceForParameter(base.push("innerEvaluator"), null, Evaluator.class);
		
		state.evaluator = innerEvaluator;
		innerEvaluator.setup(state, base.push("innerEvaluator"));
		
		state.evaluator = this;
		
		p_problem = innerEvaluator.p_problem;
		
		String spec = state.parameters.getString(base.push("speciatedPopulations"), null);
		String[] spectab = spec.split(":");
		speciatedPopulations = new int[spectab.length];
		for(int i = 0; i < spectab.length; i++)
		{
			speciatedPopulations[i] = Integer.parseInt(spectab[i]);
		}
		subpopsSpecies = new SpeciesInfo[speciatedPopulations.length];
		for(int i = 0; i < speciatedPopulations.length; i++)
		{
			subpopsSpecies[i] = new SpeciesInfo();
		}
	}
	
	@Override
	public void evaluatePopulation(EvolutionState state) {		
		innerEvaluator.evaluatePopulation(state);
		
		for(int i = 0; i < speciatedPopulations.length; i++)
		{
			speciation(state, i);
			prepareSpeciesList(state, i);
			reevalFit(state, i);
		}
	}
	
	public void prepareSpeciesList(EvolutionState state, int subpopIdx)
	{
		for(Species s : subpopsSpecies[subpopIdx].speciesList)
		{
			int num = state.random[0].nextInt(s.indsNumber);
			
			for(int i = 0; i < subpopsSpecies[subpopIdx].species.length; i++)
			{
				if(subpopsSpecies[subpopIdx].species[i] == s.speciesNumber)
				{
					if(num == 0)
					{
						s.representant = (Individual)state.population.subpops[speciatedPopulations[subpopIdx]].individuals[i].clone();
						break;
					}
					else
					{
						num--;
					}
				}
			}
		}
	}
	
	public void speciation(EvolutionState state, int subpopIdx)
	{
		Individual[] inds = state.population.subpops[speciatedPopulations[subpopIdx]].individuals;
		if(subpopsSpecies[subpopIdx].species == null)
		{
			subpopsSpecies[subpopIdx].species = new int[inds.length];
		}
		Arrays.fill(subpopsSpecies[subpopIdx].species, -1);
		
		for(int i = 0; i < inds.length; i++)
		{
			for(Species s : subpopsSpecies[subpopIdx].speciesList)
			{
				double dist = inds[i].distanceTo(s.representant);
				if(dist < subpopsSpecies[subpopIdx].threshold)
				{
					subpopsSpecies[subpopIdx].species[i] = s.speciesNumber;
					s.indsNumber++;
					break;
				}
			}
			if(subpopsSpecies[subpopIdx].species[i] == -1)
			{
				subpopsSpecies[subpopIdx].speciesList.add(new Species(inds[i], 
						subpopsSpecies[subpopIdx].totalSpeciesNumber));
				subpopsSpecies[subpopIdx].species[i] = subpopsSpecies[subpopIdx].totalSpeciesNumber;
				subpopsSpecies[subpopIdx].totalSpeciesNumber++;
			}
		}
		
		
	}
	
	public void reevalFit(EvolutionState state, int subpopIdx)
	{
		Individual[] inds = state.population.subpops[speciatedPopulations[subpopIdx]].individuals;
		
		for(int i = 0; i < inds.length; i++)
		{
			float newFit = inds[i].fitness.fitness();
			newFit = newFit / subpopsSpecies[subpopIdx].speciesList.get(subpopsSpecies[subpopIdx].species[i]).indsNumber;
			((SimpleFitness)inds[i].fitness).setFitness(state, newFit, false);			
		}
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		return innerEvaluator.runComplete(state);
	}
}
