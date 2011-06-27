package cecj.neatq.problems;

import cecj.neatq.NeatIndividual;
import cecj.neatq.NeatIndividual.Gene;
import cecj.neatq.NeuralNetworkFenotype;
import cecj.neatq.NeuralNetworkIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;

public class XORNeatProblem extends Problem implements SimpleProblemForm {

	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) 
	{
		NeatIndividual ni = (NeatIndividual)ind;
		System.out.println("Evaluating");
		/*for (Gene g : ni.genotype)
		{
			System.out.println(g);
		}*/
		
		System.out.println("fit... ");
		
		NeuralNetworkFenotype fenotype = new NeuralNetworkFenotype((NeatIndividual)ind);
		
		/*for(NeuralNetworkFenotype.Neuron n : fenotype.fenotype)
		{
			System.out.println(n);
		}*/
		
		double[][] inputs = {{0, 0, 1}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}};
		double[][] expOutputs = {{0}, {1}, {1}, {0}};
		double fit = 0.0;
		
		for(int i = 0; i < 4; i++)
		{
			double[] recOutputs = fenotype.evalNetwork(inputs[i]);
			fit += Math.abs(expOutputs[i][0] - recOutputs[0]);
		}
		fit = 4.0 - fit;
		fit = fit * fit;
		
		((SimpleFitness)ind.fitness).setFitness(state, (float)fit, fit == 16);
		
		System.out.println("DONE ");
	}

	public void describe(Individual ind, EvolutionState state,
			int subpopulation, int threadnum, int log, int verbosity) {
		
	}

}
