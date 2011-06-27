package cecj.neatq;

import java.util.ArrayList;
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
	
	private static final int TRYADDNUMBER = 10; //TODO parametr
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
			Gene newGene = new Gene(inNode, outNode, newWeight(rand), true, species().innovationNumber++);
			genotype.add(newGene);
		}			
	}
	
	public NeatIndividual()
	{
		
	}
	
	NeatNeuralNetworkSpecies species()
	{
		return (NeatNeuralNetworkSpecies)species;
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
	
	public double newWeight(MersenneTwisterFast rand)
	{
		return rand.nextDouble() * 2.0 - 1.0;
	}
	
	public void addNodeMutation(MersenneTwisterFast rand)
	{
		//System.out.println("NM1: "+genotype.toString());
		for(int i = 0; i < TRYADDNUMBER; i++)
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
		
		Gene newGene1;
		Gene newGene2;
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
			newGene1 = new Gene(genotype.get(index).inNode, newNode, newWeight(rand), true, genes[0]);
			newGene2 = new Gene(newNode, genotype.get(index).outNode, newWeight(rand), true, genes[1]);
		}
		else
		{
			int newNode = species().inNodesNumber + species().outNodesNumber 
					+ species().hiddenNodesNumber++;
			newGene1 = new Gene(genotype.get(index).inNode, newNode, newWeight(rand), true, species().innovationNumber++);
			newGene2 = new Gene(newNode, genotype.get(index).outNode, newWeight(rand), true, species().innovationNumber++);
			
			species().addGeneToNodeHistory(genotype.get(index).innovationNumber, newGene1.innovationNumber, newGene2.innovationNumber, newNode);
		}
		genotype.add(newGene1);
		genotype.add(newGene2);
		genotype.get(index).enable = false;
		
		
		
		return true;
	}
	
	public boolean tryAddLinkMutation(MersenneTwisterFast rand)
	{
		int inNode = rand.nextInt(species().inNodesNumber 
				+ species().hiddenNodesNumber + species().outNodesNumber);
		int outNode;
		if(isOutNode(inNode))
		{
			outNode = species().inNodesNumber + rand.nextInt(species().outNodesNumber);
		}
		else
		{
			outNode = species().inNodesNumber 
					+ rand.nextInt(species().outNodesNumber + species().hiddenNodesNumber);
		}
		
		if(isLinkAllowed(inNode, outNode))
		{
			Integer innum = species().findGeneInLinkHistory(inNode, outNode);
			if(innum == null)
			{
				innum = species().innovationNumber++;
				species().addGeneToLinkHistory(inNode, outNode, innum);
			}
			
			Gene newGene = new Gene(inNode, outNode, newWeight(rand), true, innum);
			genotype.add(newGene);
			return true;
		}
		return false;
	}
	
	public void addLinkMutation(MersenneTwisterFast rand)
	{
		//System.out.println("LM1: "+genotype.toString());
		for(int i = 0; i < TRYADDNUMBER; i++)
		{
			if(tryAddLinkMutation(rand))
			{
				break;
			}
		}
		//System.out.println("LM2: "+genotype.toString());
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
				g.weight *= (rand.nextDouble() * 0.2 + 0.9); //TODO Parametr?
			}
		}
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
					return false;
				}
			}
			else if(genotype.get(i).inNode == outNode)
			{
				if(genotype.get(i).outNode == inNode)
				{
					return false;
				}
			}
		}
		
		if(isCycle(outNode, inNode))
		{
			return false;
		}
		
		return true;
	}
	
	public boolean isCycle(int inNode, int destNode)
	{
		//System.out.println(inNode+" => "+destNode);
		for(int i = 0; i < genotype.size(); i++)
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
		return false;
	}

	public NeatIndividual crossover(NeatIndividual parent, MersenneTwisterFast rand)
	{
		NeatIndividual offspring = 
				new NeatIndividual(species().inNodesNumber, 
						species().outNodesNumber, 0, rand, species);
		
		int i = 0;
		int j = 0;
		
		/*System.out.println("P1: " + genotype.toString());
		System.out.println("P2: " + parent.genotype.toString());*/
		while((i < this.genotype.size()) && (j < parent.genotype.size()))
		{
			if(this.genotype.get(i).innovationNumber == parent.genotype.get(j).innovationNumber)
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
				
				++i;
				++j;
			}
			else if(this.genotype.get(i).innovationNumber < parent.genotype.get(j).innovationNumber)
			{
				if(this.fitness.betterThan(parent.fitness))
				{
					offspring.genotype.add(this.genotype.get(i).clone());
				}
				++i;
			}
			else
			{
				if(parent.fitness.betterThan(this.fitness))
				{
					offspring.genotype.add(parent.genotype.get(j).clone());
				}
				++j;
			}
		}
		if(this.fitness.betterThan(parent.fitness))
		{
			while(i < this.genotype.size())
			{
				offspring.genotype.add(this.genotype.get(i).clone());
				++i;
			}
		}
		if(parent.fitness.betterThan(this.fitness))
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

}
