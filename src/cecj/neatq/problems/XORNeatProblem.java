package cecj.neatq.problems;

import cecj.neatq.NeatNeuralNetworkIndividual;
import cecj.neatq.NeuralNetworkFenotype;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;

public class XORNeatProblem extends Problem implements SimpleProblemForm {

	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) 
	{
		System.out.println("fit... ");
		
		NeuralNetworkFenotype fenotype = new NeuralNetworkFenotype((NeatNeuralNetworkIndividual)ind);
		
		double[][] inputs = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
		double[][] expOutputs = {{0}, {1}, {1}, {0}};
		double fit = 0.0;
		
		for(int i = 0; i < 4; i++)
		{
			double[] recOutputs = fenotype.evalNetwork(inputs[i]);
			fit += Math.abs(expOutputs[i][0] - recOutputs[0]);
		}
		fit = 4.0 - fit;
		fit = fit * fit;
		
		((SimpleFitness)ind.fitness).setFitness(state, (float)fit, false);
		
		System.out.println("DONE ");
	}

	public void describe(Individual ind, EvolutionState state,
			int subpopulation, int threadnum, int log, int verbosity) {
		
	}

}
