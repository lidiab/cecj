package cecj.neat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ec.Fitness;
import ec.Individual;
import ec.Species;
import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class NeatIndividual extends Individual {

	public static class Gene
	{
		//protected static int newInnovationNumber = 0;
		public int inNode;
		public int outNode;
		public double weight;
		public boolean enable;
		public int innovationNumber;
		
		private Gene(int inNode, int outNode, double weight, boolean enable, int innovationNumber)
		{
			this.inNode = inNode;
			this.outNode = outNode;
			this.weight = weight;
			this.enable = enable;
			this.innovationNumber = innovationNumber;			
		}
		
		/*public Gene(int inNode, int outNode, double weight, boolean enable)
		{
			this.inNode = inNode;
			this.outNode = outNode;
			this.weight = weight;
			this.enable = enable;
			this.innovationNumber = newInnovationNumber++;			
		}*/
		
		/*public Gene()
		{
			this.innovationNumber = newInnovationNumber++;
		}*/
		
		public Gene clone()
		{
			Gene clone = new Gene(inNode, outNode, weight, enable, innovationNumber);
			
			return clone;
		}
		
		@Override
		public String toString() {
			if (enable)
				return ""+this.innovationNumber+ ": " +inNode+" -> "+outNode;
			else
				return "";
		}
	}
	//private static final int TRYADDNUMBER = 10; 
	public ArrayList<Gene> genotype = new ArrayList<Gene>();
	/*int inNodesNumber;
	int outNodesNumber;
	int hiddenNodesNumber;*/
	
	public NeatIndividual(int inNodesNum, int outNodesNum, int linkNum, MersenneTwisterFast rand, Species species)
	{
		int[] tab = new int[inNodesNum * outNodesNum];
		/*this.inNodesNumber = inNodesNum;
		this.outNodesNumber = outNodesNum;
		this.hiddenNodesNumber = 0;*/
		
		this.species = species;
		
		for(int i = 0; i < tab.length; i++)
		{
			tab[i] = i;
		}
		
		for(int i = 0; i < linkNum; i++)
		{
			int ind = i + rand.nextInt(tab.length - i);
			int buf = tab[i];
			tab[i] = tab[ind];
			tab[ind] = buf;			
		}
		
		for(int i = 0; i < linkNum; i++)
		{
			int inNode = tab[i] / outNodesNum;
			int outNode = inNodesNum + tab[i] % outNodesNum;
			addGeneToGenotype(inNode, outNode, rand);
			//Gene newGene = new Gene(inNode, outNode, newWeight(rand), true, species().innovationNumber++);
			//genotype.add(newGene);
		}			
	}
	
	public NeatIndividual(String sgen, MersenneTwisterFast rand, Species species)
	{
		this.species = species;
		

		int hide = 0;
		
		String[] gentab = sgen.split(":");
		
		String[] buf = gentab[0].split(">");
		if(buf.length > 1)
		{
			for (String s : gentab)
			{
				buf = s.split(">");
				int inNode = Integer.parseInt(buf[0]);
				int outNode = Integer.parseInt(buf[1]);
				if(inNode > hide)
				{
					hide = inNode;
				}
				if(outNode > hide)
				{
					hide = outNode;
				}
				addGeneToGenotype(inNode, outNode, rand);
				//Gene newGene = new Gene(inNode, outNode, newWeight(rand), true, species().innovationNumber++);
				//genotype.add(newGene);
			}
			hide++;
		}
		else
		{			
			int innodes = 0;
			int outnodes = 0;
			
			if(gentab.length == 2)
			{
				innodes = Integer.parseInt(gentab[0]);
				outnodes = Integer.parseInt(gentab[1]);
				for(int i = 0; i < innodes; i++)
				{
					for(int j = 0; j < outnodes; j++)
					{
						addGeneToGenotype(i, innodes + j, rand);
						//Gene newGene = new Gene(i, innodes + j, newWeight(rand), true, species().innovationNumber++);
						//genotype.add(newGene);
					}
				}
			}
			else
			{
				innodes = Integer.parseInt(gentab[0]);
				outnodes = innodes + Integer.parseInt(gentab[gentab.length - 1]);
				for(int i = 0; i < innodes; i++)
				{
					for(int j = 0; j < Integer.parseInt(gentab[1]); j++)
					{
						addGeneToGenotype(i, outnodes + j, rand);
						//Gene newGene = new Gene(i, outnodes + j, newWeight(rand), true, species().innovationNumber++);
						//genotype.add(newGene);
					}
				}
				
				innodes = outnodes;
				outnodes += Integer.parseInt(gentab[1]);
				for(int i = 1; i < gentab.length - 2; i++)
				{
					for(int j = 0; j < Integer.parseInt(gentab[i]); j++)
					{
						for(int k = 0; k < Integer.parseInt(gentab[i+1]); k++)
						{
							addGeneToGenotype(innodes + j, outnodes + k, rand);
							//Gene newGene = new Gene(innodes + j, outnodes + k, newWeight(rand), true, species().innovationNumber++);
							//genotype.add(newGene);
						}
					}
					innodes += Integer.parseInt(gentab[i+1]);
					outnodes += Integer.parseInt(gentab[i+2]);
				}
				outnodes = Integer.parseInt(gentab[0]);
				for(int i = 0; i < Integer.parseInt(gentab[gentab.length - 2]); i++)
				{
					for(int j = 0; j < Integer.parseInt(gentab[gentab.length - 1]); j++)
					{
						addGeneToGenotype(innodes + i, outnodes + j, rand);
						//Gene newGene = new Gene(innodes + i, outnodes + j, newWeight(rand), true, species().innovationNumber++);
						//genotype.add(newGene);
					}
				}
				
				for(int i = 0; i < gentab.length; i++ )
				{
					hide += Integer.parseInt(gentab[i]);
				}
			}
		}
		hide -= species().inNodesNumber + species().outNodesNumber;
		if(hide > 0)
		{
			species().hiddenNodesNumber = hide;
		}
		
	}
	
	public NeatIndividual()
	{
		
	}
	
	NeatSpecies species()
	{
		return (NeatSpecies)species;
	}
		
	public int addGeneToGenotype(int inNode, int outNode, MersenneTwisterFast rand)
	{
		Integer g = findGene(inNode, outNode);
		
		if(g >= 0)
		{
			if(!genotype.get(g).enable)
			{
				genotype.get(g).enable = true;
				genotype.get(g).weight = newWeight(rand);
			}
			return genotype.get(g).innovationNumber;
		}
		
		g = species().findGeneInLinkHistory(inNode, outNode);
		
		if(g == null)
		{
			g = species().innovationNumber++;
			species().addGeneToLinkHistory(inNode, outNode, g);
		}
		
		Gene newGene = new Gene(inNode, outNode, newWeight(rand), true, g);
		genotype.add(newGene);
		return newGene.innovationNumber;
	}
	public double newWeight(MersenneTwisterFast rand)
	{
		return rand.nextDouble() * 20.0 - 10.0;
	}

	public int findGene(int inNode, int outNode)
		{for(int i = 0; i < genotype.size(); i++)
		{
			if(genotype.get(i).inNode == inNode)
			{
				if(genotype.get(i).outNode == outNode)
				{
					return i;
				}
			}
			
		}
		return -1;
	}
	
	public boolean isInNode(int node)
	{
		if(node < species().inNodesNumber)
		{
			return true;
		}
		return false;
	}

	public boolean isOutNode(int node)
	{
		if((node >= species().inNodesNumber) 
				&& (node < species().inNodesNumber + species().outNodesNumber))
		{
			return true;
		}
		return false;
	}
	
	public void addNodeMutation(MersenneTwisterFast rand)
	{
		//System.out.println("NM1: "+genotype.toString());
		for(int i = 0; i < species().tryAddNumber; i++)
		{
			if(tryAddNodeMutation(rand))
			{
				break;
			}
		}
		//System.out.println("NM2: "+genotype.toString());
	}
	
	public boolean tryAddNodeMutation(MersenneTwisterFast rand)
	{
		int index = rand.nextInt(genotype.size());
		if( ! genotype.get(index).enable)
		{
			return false;
		}
		
		if(isOutNode(genotype.get(index).inNode))
		{
			return false;
		}
		
		int[] genes = species().findGeneInNodeHistory(genotype.get(index).innovationNumber);
		
		//Gene newGene1;
		//Gene newGene2;
		if(genes != null)
		{
			int newNode = genes[2];
			if(isCycle(newNode, genotype.get(index).inNode))
			{
				return false;
			}
			if(isCycle(genotype.get(index).outNode, newNode))
			{
				return false;
			}
			addGeneToGenotype(genotype.get(index).inNode, newNode, rand);
			addGeneToGenotype(newNode, genotype.get(index).outNode, rand);
			//newGene1 = new Gene(genotype.get(index).inNode, newNode, newWeight(rand), true, genes[0]);
			//newGene2 = new Gene(newNode, genotype.get(index).outNode, newWeight(rand), true, genes[1]);
		}
		else
		{
			int newNode = species().inNodesNumber + species().outNodesNumber 
					+ species().hiddenNodesNumber++;
			
			if (newNode == 5 && (genotype.get(index).inNode==5 || genotype.get(index).outNode==5))
				newNode = 5;
			
			int newGene1 = addGeneToGenotype(genotype.get(index).inNode, newNode, rand);
			int newGene2 = addGeneToGenotype(newNode, genotype.get(index).outNode, rand);
			
			//newGene1 = new Gene(genotype.get(index).inNode, newNode, newWeight(rand), true, species().innovationNumber++);
			//newGene2 = new Gene(newNode, genotype.get(index).outNode, newWeight(rand), true, species().innovationNumber++);
			
			species().addGeneToNodeHistory(genotype.get(index).innovationNumber, newGene1, newGene2, newNode);
		}
		
		//genotype.add(newGene1);
		//genotype.add(newGene2);
		genotype.get(index).enable = false;
				
		return true;
	}
	
	public boolean tryAddLinkMutation(MersenneTwisterFast rand, ArrayList<Integer> buf)
	{
		//int inNode = rand.nextInt(species().inNodesNumber 
		//		+ species().hiddenNodesNumber + species().outNodesNumber);
		int inNode = rand.nextInt(species().inNodesNumber 
				+ species().outNodesNumber) + buf.size();

		if(inNode >= species().inNodesNumber + species().outNodesNumber)
		{
			inNode -= species().inNodesNumber + species().outNodesNumber;
			inNode = buf.get(inNode);
		}
		int outNode;
		if(isOutNode(inNode))
		{
			outNode = species().inNodesNumber + rand.nextInt(species().outNodesNumber);
		}
		else
		{
			//outNode = species().inNodesNumber 
			//		+ rand.nextInt(species().outNodesNumber + species().hiddenNodesNumber);
			outNode = species().inNodesNumber 
					+ rand.nextInt(species().outNodesNumber + buf.size());
			
			if(outNode >= species().inNodesNumber + species().outNodesNumber)
			{
				outNode -= species().inNodesNumber + species().outNodesNumber;
				outNode = buf.get(outNode);
			}
		}
		
		if(isLinkAllowed(inNode, outNode))
		{
			addGeneToGenotype(inNode, outNode, rand);
			/*int g = findGene(inNode, outNode);
			if(g > -1)
			{
				genotype.get(g).enable = true;
				return true;
			}
			Integer innum = species().findGeneInLinkHistory(inNode, outNode);
			if(innum == null)
			{
				innum = species().innovationNumber++;
				species().addGeneToLinkHistory(inNode, outNode, innum);
			}
			
			Gene newGene = new Gene(inNode, outNode, newWeight(rand), true, innum);
			genotype.add(newGene);*/
			return true;
		}
		return false;
	}
	
	public void addLinkMutation(MersenneTwisterFast rand)
	{
		ArrayList<Integer> buf = new ArrayList<Integer>();
		for(Gene g : genotype)
		{
			if(! g.enable)
			{
				continue;
			}
			if(! isInNode(g.inNode) && ! isOutNode(g.inNode))
			{
				if(! buf.contains(g.inNode))
				{
					buf.add(g.inNode);
				}
			}
			if(! isInNode(g.outNode) && ! isOutNode(g.outNode))
			{
				if(! buf.contains(g.outNode))
				{
					buf.add(g.outNode);
				}
			}
		}
		//System.out.println("LM1: "+genotype.toString());
		for(int i = 0; i < species().tryAddNumber; i++)
		{
			if(tryAddLinkMutation(rand, buf))
			{
				break;
			}
		}
		//System.out.println("LM2: "+genotype.toString());
	}
	
	public boolean isLinkAllowed(int inNode, int outNode)
	{
		if(inNode == outNode)
		{
			return false;
		}
		for(int i = 0; i < genotype.size(); i++)
		{
			if(genotype.get(i).inNode == inNode)
			{
				if(genotype.get(i).outNode == outNode)
				{
					if(genotype.get(i).enable)
					{
						return false;
					}
				}
			}
			else if(genotype.get(i).inNode == outNode)
			{
				if(genotype.get(i).outNode == inNode)
				{
					if(genotype.get(i).enable)
					{
						return false;
					}
				}
			}
		}
		
		if(isCycle(outNode, inNode))
		{
			return false;
		}
		
		return true;
	}
	
	public void weightMutation(MersenneTwisterFast rand)
	{
		for(Gene g : genotype)
		{
			if(rand.nextBoolean(species().randomWeightProbability))
			{
				g.weight = newWeight(rand);
			}
			else
			{
				g.weight *= (rand.nextDouble() * 0.6 + 0.7); //TODO Parametr?
			}
		}
	}
	
	public boolean isCycle(int inNode, int destNode)
	{
		//System.out.println(inNode+" => "+destNode);
		for(int i = 0; i < genotype.size(); i++)
		{
			if(genotype.get(i).enable)
			{
				if(genotype.get(i).inNode == inNode)
				{
					if(genotype.get(i).outNode == destNode)
					{
						return true;
					}
					else
					{
						if(isCycle(genotype.get(i).outNode, destNode))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public NeatIndividual crossover(NeatIndividual parent, MersenneTwisterFast rand)
	{
		NeatIndividual offspring = 
				new NeatIndividual(species().inNodesNumber, 
						species().outNodesNumber, 0, rand, species);
		
		sortGenotype();
		parent.sortGenotype();
		
		int i = 0;
		int j = 0;
		
		/*System.out.println("P1: " + genotype.toString());
		System.out.println("P2: " + parent.genotype.toString());*/
		
		int randpar = -1;
		if(this.fitness == parent.fitness)
		{
			randpar = rand.nextInt(2);
		}
		
		while((i < this.genotype.size()) && (j < parent.genotype.size()))
		{
			if(this.genotype.get(i).innovationNumber == parent.genotype.get(j).innovationNumber)
			{
				if(this.genotype.get(i).enable == parent.genotype.get(j).enable)
				{
					int r = rand.nextInt(2);
					
					if(r == 0)
					{
						offspring.genotype.add(this.genotype.get(i).clone());
					}
					else
					{
						offspring.genotype.add(parent.genotype.get(j).clone());
					}
				}
				else
				{
					if(this.fitness.betterThan(parent.fitness) || (randpar == 0))
					{
						offspring.genotype.add(this.genotype.get(i).clone());
					}
					else if (parent.fitness.betterThan(this.fitness) || (randpar == 1))
					{
						offspring.genotype.add(parent.genotype.get(j).clone());
					}
				}
				
				++i;
				++j;
			}
			else if(this.genotype.get(i).innovationNumber < parent.genotype.get(j).innovationNumber)
			{
				if(this.fitness.betterThan(parent.fitness) || (randpar == 0))
				{
					offspring.genotype.add(this.genotype.get(i).clone());
				}
				++i;
			}
			else
			{
				if(parent.fitness.betterThan(this.fitness) || (randpar == 1))
				{
					offspring.genotype.add(parent.genotype.get(j).clone());
				}
				++j;
			}
		}
		if(this.fitness.betterThan(parent.fitness) || (randpar == 0))
		{
			while(i < this.genotype.size())
			{
				offspring.genotype.add(this.genotype.get(i).clone());
				++i;
			}
		}
		if(parent.fitness.betterThan(this.fitness) || (randpar == 1))
		{
			while(j < parent.genotype.size())
			{
				offspring.genotype.add(parent.genotype.get(j).clone());
				++j;
			}
		}
		//System.out.println("O:" + offspring.genotype.toString());
		
		/*int max = 0;
		for(Gene g : offspring.genotype)
		{
			if(g.inNode > max)
			{
				max = g.inNode;
			}
			if(g.outNode > max)
			{
				max = g.outNode;
			}
		}
		
		max -= offspring.inNodesNumber + offspring.outNodesNumber;
		
		if(max >= offspring.hiddenNodesNumber)
		{
			offspring.hiddenNodesNumber = max + 1;
		}*/
		
		offspring.fitness = new SimpleFitness();
		offspring.species = this.species;
		return offspring;
	}
	
	public void sortGenotype()
	{
		Collections.sort(genotype, new Comparator<Gene>() {

			public int compare(Gene o1, Gene o2) {
				if(o1.innovationNumber < o2.innovationNumber)
					return -1;
				if(o1.innovationNumber > o2.innovationNumber)
					return 1;
				return 0;
			}
		});
		
	}
	
	@Override
	public double distanceTo(Individual otherInd) {
		if(otherInd instanceof NeatIndividual)
		{
			return distance((NeatIndividual)otherInd, species().c1, species().c2, species().c3);
		}
		return super.distanceTo(otherInd);
	}
	
	public double distance(NeatIndividual ind, double w1, double w2, double w3)
	{
		sortGenotype();
		ind.sortGenotype();
		double dist = 0.0;
		int i1 = 0;
		int i2 = 0;
		int excess = 0;
		int disjoint = 0;
		int matching = 0;
		double weight = 0.0;
		
		int number = Math.max(this.genotype.size(), ind.genotype.size());
		
		while(this.genotype.get(i1).innovationNumber < ind.genotype.get(0).innovationNumber)
		{
			++i1;
			++excess;
		}
		while(this.genotype.get(0).innovationNumber > ind.genotype.get(i2).innovationNumber)
		{
			++i2;
			++excess;
		}
		
		while((i1 < this.genotype.size()) && (i2 < ind.genotype.size()))
		{
			if(this.genotype.get(i1).innovationNumber == ind.genotype.get(i2).innovationNumber)
			{
				weight += Math.abs(this.genotype.get(i1).weight - ind.genotype.get(i2).weight);
				++matching;
				++i1;
				++i2;
			}
			else if(this.genotype.get(i1).innovationNumber < ind.genotype.get(i2).innovationNumber)
			{
				++i1;
				++disjoint;
			}
			else
			{
				++i2;
				++disjoint;
			}
		}
		
		while(i1 < this.genotype.size())
		{
			++i1;
			++excess;
		}
		while(i2 < ind.genotype.size())
		{
			++i2;
			++excess;
		}
		
		weight /= matching;
		
		dist = w1 * excess / number + w2 * disjoint / number + w3 * weight;
		
		return dist;
	}
	
	public Parameter defaultBase() {
		return new Parameter("NeatNeuralNetwork");
	}
	
	@Override
	public Object clone() {
		NeatIndividual clone = new NeatIndividual();
		//clone.genotype = new ArrayList<Gene>(genotype);
		clone.genotype = new ArrayList<Gene>();
		for(int i = 0; i < genotype.size(); i++)
		{
			clone.genotype.add(genotype.get(i).clone());
		}
		/*clone.hiddenNodesNumber = this.hiddenNodesNumber;
		clone.inNodesNumber = this.inNodesNumber;
		clone.outNodesNumber = this.outNodesNumber;*/
		if (this.fitness != null)
		{
			clone.fitness = (Fitness)(this.fitness.clone());
		}
		clone.species = this.species;
		
		return clone;
	}

	@Override
	public boolean equals(Object ind) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString()
	{
		return genotype.toString();
	}

}
