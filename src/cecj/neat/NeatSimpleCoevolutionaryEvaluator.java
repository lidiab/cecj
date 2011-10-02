package cecj.neat;

import java.util.ArrayList;
import java.util.Arrays;

import cecj.eval.SimpleCoevolutionaryEvaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class NeatSimpleCoevolutionaryEvaluator extends
		SimpleCoevolutionaryEvaluator {

	static class Species
	{
		Individual rep;
		int speciesNumber;
		int indsNumber = 0;
		Species(Individual r, int num)
		{
			rep = r;
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
	
	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		
		subpopsSpecies = new SpeciesInfo[numSubpopulations];
		for(int i = 0; i < numSubpopulations; i++)
		{
			subpopsSpecies[i] = new SpeciesInfo();
		}
	}
	
	@Override
	public void evaluatePopulation(EvolutionState state) {		
		super.evaluatePopulation(state);
		
		for(int i = 0; i < numSubpopulations; i++)
		{
			speciation(state, i);
			prepareSpeciesList(state, i);
			reevalFit(state, i);
		}
	}
	
	public void prepareSpeciesList(EvolutionState state, int subpop)
	{
		for(Species s : subpopsSpecies[subpop].speciesList)
		{
			int num = state.random[0].nextInt(s.indsNumber);
			
			for(int i = 0; i < subpopsSpecies[subpop].species.length; i++)
			{
				if(subpopsSpecies[subpop].species[i] == s.speciesNumber)
				{
					if(num == 0)
					{
						s.rep = (Individual)state.population.subpops[subpop].individuals[i].clone();
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
	
	public void speciation(EvolutionState state, int subpop)
	{
		Individual[] inds = state.population.subpops[subpop].individuals;
		if(subpopsSpecies[subpop].species == null)
		{
			subpopsSpecies[subpop].species = new int[inds.length];
		}
		Arrays.fill(subpopsSpecies[subpop].species, -1);
		
		for(int i = 0; i < inds.length; i++)
		{
			for(Species s : subpopsSpecies[subpop].speciesList)
			{
				double dist = inds[i].distanceTo(s.rep);
				if(dist < subpopsSpecies[subpop].threshold)
				{
					subpopsSpecies[subpop].species[i] = s.speciesNumber;
					s.indsNumber++;
					break;
				}
			}
			if(subpopsSpecies[subpop].species[i] == -1)
			{
				subpopsSpecies[subpop].speciesList.add(new Species(inds[i], 
						subpopsSpecies[subpop].totalSpeciesNumber));
				subpopsSpecies[subpop].species[i] = subpopsSpecies[subpop].totalSpeciesNumber;
				subpopsSpecies[subpop].totalSpeciesNumber++;
			}
		}
		
		
	}
	
	public void reevalFit(EvolutionState state, int subpop)
	{
		Individual[] inds = state.population.subpops[subpop].individuals;
		
		for(int i = 0; i < inds.length; i++)
		{
			float newFit = inds[i].fitness.fitness();
			newFit = newFit / subpopsSpecies[subpop].speciesList.get(subpopsSpecies[subpop].species[i]).indsNumber;
			((SimpleFitness)inds[i].fitness).setFitness(state, newFit, false);			
		}
	}
}
