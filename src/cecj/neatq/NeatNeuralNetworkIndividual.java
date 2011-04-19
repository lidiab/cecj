package cecj.neatq;

import java.util.ArrayList;

import ec.Fitness;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class NeatNeuralNetworkIndividual extends Individual {

	public static class Gene
	{
		protected static int newInnovationNumber = 0;
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
		
		public Gene(int inNode, int outNode, double weight, boolean enable)
		{
			this.inNode = inNode;
			this.outNode = outNode;
			this.weight = weight;
			this.enable = enable;
			this.innovationNumber = newInnovationNumber++;			
		}
		
		public Gene()
		{
			this.innovationNumber = newInnovationNumber++;
		}
		
		public Gene clone()
		{
			Gene clone = new Gene(inNode, outNode, weight, enable, innovationNumber);
			
			return clone;
		}
		
		@Override
		public String toString() {
			return ""+this.innovationNumber+ ": " +inNode+" -> "+outNode;
		}
	}
	
	private static final int TRYADDNUMBER = 10;
	ArrayList<Gene> genotype = new ArrayList<Gene>();
	int inNodesNumber;
	int outNodesNumber;
	int hiddenNodesNumber;
	
	public NeatNeuralNetworkIndividual(int inNodesNum, int outNodesNum, int linkNum, MersenneTwisterFast rand)
	{
		int[] tab = new int[inNodesNum * outNodesNum];
		this.inNodesNumber = inNodesNum;
		this.outNodesNumber = outNodesNum;
		this.hiddenNodesNumber = 0;
		
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
			Gene newGene = new Gene(inNode, outNode, 1.0, true);
			genotype.add(newGene);
		}			
	}
	
	public NeatNeuralNetworkIndividual()
	{
		
	}
	
public boolean isInNode(int node)
	{
		if(node < inNodesNumber)
		{
			return true;
		}
		return false;
	}

	public boolean isOutNode(int node)
	{
		if((node >= inNodesNumber) && (node < inNodesNumber + outNodesNumber))
		{
			return true;
		}
		return false;
	}
	
	public void addNodeMutation(MersenneTwisterFast rand)
	{
		System.out.println("NM1: "+genotype.toString());
		for(int i = 0; i < TRYADDNUMBER; i++)
		{
			if(tryAddNodeMutation(rand))
			{
				break;
			}
		}
		System.out.println("NM2: "+genotype.toString());
	}
	
	public boolean tryAddNodeMutation(MersenneTwisterFast rand)
	{
		int index = rand.nextInt(genotype.size());
		if(isOutNode(genotype.get(index).inNode))
		{
			return false;
		}
		
		int newNode = inNodesNumber + outNodesNumber + hiddenNodesNumber++;
		Gene newGene1 = new Gene(genotype.get(index).inNode, newNode, 1.0, true);
		Gene newGene2 = new Gene(newNode, genotype.get(index).outNode, 1.0, true);
		genotype.add(newGene1);
		genotype.add(newGene2);
		genotype.remove(index);
		
		return true;
	}
	
	public boolean tryAddLinkMutation(MersenneTwisterFast rand)
	{
		int inNode = rand.nextInt(inNodesNumber + hiddenNodesNumber + outNodesNumber);
		int outNode;
		if(isOutNode(inNode))
		{
			outNode = inNodesNumber + rand.nextInt(outNodesNumber);
		}
		else
		{
			outNode = inNodesNumber + rand.nextInt(outNodesNumber + hiddenNodesNumber);
		}
		
		if(isLinkAllowed(inNode, outNode))
		{
			Gene newGene = new Gene(inNode, outNode, 1.0, true);
			genotype.add(newGene); //TODO funkcja do wag
			return true;
		}
		
		return false;
	}
	
	public void addLinkMutation(MersenneTwisterFast rand)
	{
		System.out.println("LM1: "+genotype.toString());
		for(int i = 0; i < TRYADDNUMBER; i++)
		{
			if(tryAddLinkMutation(rand))
			{
				break;
			}
		}
		System.out.println("LM2: "+genotype.toString());
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

	public NeatNeuralNetworkIndividual crossover(NeatNeuralNetworkIndividual parent, MersenneTwisterFast rand)
	{
		NeatNeuralNetworkIndividual offspring = new NeatNeuralNetworkIndividual(inNodesNumber, outNodesNumber, 0, rand);
		
		int i = 0;
		int j = 0;
		
		System.out.println("P1: " + genotype.toString());
		System.out.println("P2: " + parent.genotype.toString());
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
			}
		}
		if(parent.fitness.betterThan(this.fitness))
		{
			while(j < parent.genotype.size())
			{
				offspring.genotype.add(parent.genotype.get(j).clone());
			}
		}
		System.out.println("O:" + offspring.genotype.toString());
		
		int max = 0;
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
		}
		
		offspring.fitness = new SimpleFitness();
		offspring.species = this.species;
		return offspring;
	}
	
	public Parameter defaultBase() {
		return new Parameter("NeatNeuralNetwork");
	}
	
	@Override
	public Object clone() {
		NeatNeuralNetworkIndividual clone = new NeatNeuralNetworkIndividual();
		clone.genotype = this.genotype;
		clone.hiddenNodesNumber = this.hiddenNodesNumber;
		clone.inNodesNumber = this.inNodesNumber;
		clone.outNodesNumber = this.outNodesNumber;
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
