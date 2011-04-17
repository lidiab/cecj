package cecj.neatq;

import java.util.ArrayList;
import java.util.Stack;

public class NeuralNetworkFenotype {

	private static class Link
	{
		public Neuron inNeuron;
		public double weight;
		
		public Link(Neuron inNeuron, double weight)
		{
			this.inNeuron = inNeuron;
			this.weight = weight;
		}
	}
	
	public static class Neuron
	{
		public ArrayList<Link> prevNeurons;
		public int nextNeurons = 0;
		public double value = 0;
		public int neuronID;
		public boolean info;
		
		public Neuron(double value, int id)
		{
			this.value = value;
			neuronID = id;
			prevNeurons = null;
		}
		
		public Neuron(int id)
		{
			prevNeurons = new ArrayList<Link>();
			neuronID = id;
		}
		
		public void addPrevNeuron(Link newNeuron)
		{
			prevNeurons.add(newNeuron);
		}
		
		public void evalNeuron()
		{
			if(prevNeurons != null)
			{
				value = 0;
				for(Link l : prevNeurons)
				{
					value += l.weight * l.inNeuron.value;
				}
				value = 1.0 / (1.0 + Math.exp(value));
			}
		}
	}
	
	ArrayList<Neuron> fenotype;
	ArrayList<Neuron> inputs;
	ArrayList<Neuron> outputs;
	
	public Neuron findGene(int id, NeatNeuralNetworkIndividual genotype)
	{
		for(Neuron n : fenotype)
		{
			if(n.neuronID == id)
			{
				return n;
			}
		}
		
		Neuron n = new Neuron(id);
		fenotype.add(n);
		
		if(genotype.isInNode(id))
		{
			inputs.add(n);
			n.prevNeurons = null;
		}
		else if(genotype.isOutNode(id))
		{
			outputs.add(n);
		}
		
		return n;
	}
	
	
	public void readFromGenotype(NeatNeuralNetworkIndividual genotype)
	{
		for(NeatNeuralNetworkIndividual.Gene g : genotype.genotype)
		{
			Neuron inNeuron = findGene(g.inNode, genotype);
			
			Neuron outNeuron = findGene(g.outNode, genotype);
						
			Link newLink = new Link(inNeuron, g.weight);
			outNeuron.addPrevNeuron(newLink);
			++inNeuron.nextNeurons;
		}
		
		for(Neuron n : fenotype)
		{
			n.info = false;
		}
		topologicalSort();
	}
	
	
	public void topologicalSort()
	{
		Stack<Neuron> stack = new Stack<Neuron>();
		
		for(Neuron n : inputs)
		{
			if(n.nextNeurons == 0)
			{
				stack.push(n);
			}
		}

		for(Neuron n : outputs)
		{
			if(n.nextNeurons == 0)
			{
				stack.push(n);
			}
		}
		
		fenotype.clear();
		while(!stack.isEmpty())
		{
			Neuron n = stack.peek();
			if(!n.info)
			{
				int cnt = 0;
				for(Link l : n.prevNeurons)
				{
					if(!l.inNeuron.info)
					{
						stack.push(l.inNeuron);
						++cnt;
					}
				}
				if(cnt == 0)
				{
					n.info = true;
					fenotype.add(n);
					stack.pop();
				}
			}
		}
	}
	
	public double[] evalNetwork(double[] inputs)
	{
		for(int i = 0; i < inputs.length; i++)
		{
			this.inputs.get(i).value = inputs[i];
		}
		
		for(Neuron n : fenotype)
		{
			n.evalNeuron();
		}
		
		double[] outputs = new double[this.outputs.size()];
		for(int i = 0; i < outputs.length; i++)
		{
			outputs[i] = this.outputs.get(i).value;
		}
		
		return outputs;
	}
}
