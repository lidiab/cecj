package cecj.neatq;

import java.util.Random;

import ec.Individual;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public abstract class NeuralNetworkIndividual extends Individual {

	//int numberOfLayers;
	int[] numberOfNeurons;
	public double[][][] weights;
	
	public NeuralNetworkIndividual(int[] numberOfNeurons) {
		MersenneTwisterFast rand = new MersenneTwisterFast(); 
		
		this.numberOfNeurons = new int[numberOfNeurons.length];
		for(int i = 0; i < numberOfNeurons.length; i++)
		{
			this.numberOfNeurons[i] = numberOfNeurons[i];
		}

		this.weights = new double[this.numberOfNeurons.length - 1][][]; 
		for(int i = 0; i < weights.length; i++)
		{
			this.weights[i] = new double[this.numberOfNeurons[i + 1]][];
			for(int j = 0; j < this.weights[i].length; j++)
			{
				this.weights[i][j] = new double[this.numberOfNeurons[i]+1];
				for(int k = 0; k < this.weights[i][j].length; k++)
				{
					this.weights[i][j][k] = rand.nextDouble() * 2 - 1;
				}
			}
		}
	}
	
	protected double[][] getOutput(double[] inputs)
	{
		if(inputs.length != this.numberOfNeurons[0])
		{
			throw new IllegalArgumentException("Wrong input size");
		}
		
		double[][] neuronsValues = new double[this.numberOfNeurons.length][];
		
		neuronsValues[0] = inputs;
		
		for(int i = 1; i < this.numberOfNeurons.length; i++)
		{
			neuronsValues[i] = new double[this.numberOfNeurons[i]];
			for(int j = 0; j < this.numberOfNeurons[i]; j++)
			{
				neuronsValues[i][j] = 0;
				for(int k = 0; k < this.numberOfNeurons[i-1]; k++)
				{
					neuronsValues[i][j] += neuronsValues[i-1][k] * this.weights[i-1][j][k];
				}
				neuronsValues[i][j] -= this.weights[i-1][j][this.numberOfNeurons[i-1]];
				neuronsValues[i][j] = activation(neuronsValues[i][j]);
			}
		}
		return neuronsValues;
	}
	
	public double[] getLastOutput(double[] inputs)
	{
		double[][] result = getOutput(inputs);
		return result[result.length - 1];
	}
	
	protected abstract double activation(double x);
	
	protected abstract double activationDerivative(double z);
	
	public double[][] errors(double[] expOutputs, double[] recOutputs)
	{
		double[][] errors  = new double[this.weights.length][];

		for(int i = 0; i < this.weights.length; i++)
		{
			errors[i] = new double[this.weights[i].length];
		}
		if(expOutputs.length != recOutputs.length)
		{
			throw new IllegalArgumentException("Wrong output size");
		}
		for(int i = 0; i < expOutputs.length; i++)
		{
			errors[weights.length - 1][i] = expOutputs[i] - recOutputs[i];
		}
		return errors;
	}
	
	public void backpropagation(double[][] errors)
	{
		for(int i = errors.length - 1; i > 0; i--)
		{
			for(int j = 0; j < errors[i-1].length; j++)
			{
				double suma = 0;
				for(int k = 0; k < errors[i].length; k++)
				{
					suma += errors[i][k] * weights[i][k][j];
				}
				errors[i-1][j] = suma;
			}
		}
	}
	
	public void weightsModification(double[][] errors, double[][] recOutputs, double learningSpeed){
		for(int i = 0; i < weights.length; i++)
		{
			for(int j = 0; j < weights[i].length; j++)
			{
				for(int k = 0; k < weights[i][j].length - 1; k++)
				{
					weights[i][j][k] += 
						learningSpeed *
						errors[i][j] * 
						activationDerivative(recOutputs[i+1][j]) 
						* recOutputs[i][k];
				}
				weights[i][j][weights[i][j].length - 1] += 
					learningSpeed *
					errors[i][j] * 
					activationDerivative(recOutputs[i+1][j]) 
					* -1;
			}
		}
	}
	
	//gamma - discount factor, lambda - eligibility decay rate
	public void backpropagationAlgorithm(double[] inputs, double[] expOutputs, double reward, double learningSpeed, double gamma, double lambda)
	{
		double[][] recOutputs = getOutput(inputs);
		double[][]errors = errors(expOutputs, recOutputs[recOutputs.length - 1]);
		backpropagation(errors);
		weightsModification(errors, recOutputs, learningSpeed);
	}
	
	public void learnEpoch(double[][] inputs, double[][] expOutputs, double reward, double learningSpeed, double gamma, double lambda)
	{
		for(int i = 0; i < inputs.length; i++)
		{
			backpropagationAlgorithm(inputs[i], expOutputs[i], reward, learningSpeed, gamma, lambda);
		}
	}
	
	static <T> void swap (T a, T b)
	{
		T tmp = a;
		a = b;
		b = tmp;
	}
	
	public double meanSquareError(double[] a, double[] b)
	{
		if(a.length != b.length)
		{
			throw new IllegalArgumentException("Arrays lenghts do not match");
		}
		
		double error = 0.0;
		for(int i = 0; i < a.length; i++)
		{
			error += (a[i] - b[i])*(a[i] - b[i]);
		}
		error = Math.sqrt(error);
		
		return error;
	}
	
	/*public double meanSquareErrorAvg(double[][] a, double[][] b)
	{
		if(a.length != b.length)
		{
			throw new IllegalArgumentException("Arrays lenghts do not match");
		}
		
		double error = 0.0;
		for(int i = 0; i < a.length; i++)
		{
			error += meanSquareError(a[i], b[i]);
		}
		
		error /= a.length;
		
		return error;
	}*/

	public void learn(double[][] inputs, double[][] expOutputs, int epochNumber, double reward, double learningSpeed, double gamma, double lambda)
	{
		for(int i = 0; i < epochNumber; i++)
		{
			for (int j = inputs.length - 1; j > 0; j--)
			{
				double r = Math.random();
				int index = (int)(r * j);
				
				//swap(inputs[j], inputs[index]);
				double[] tmp = inputs[j];
				inputs[j] = inputs[index];
				inputs[index] = tmp;
				
				//swap(expOutputs[j], expOutputs[index]);
				tmp = expOutputs[j];
				expOutputs[j] = expOutputs[index];
				expOutputs[index] = tmp;
			}
			
			learnEpoch(inputs, expOutputs, reward, learningSpeed, gamma, lambda);
			
			double error = 0.0;
			for(int j = 0; j < inputs.length; j++)
			{
				double[][] recOutput = getOutput(inputs[j]);
				
				System.out.println(inputs[j][0] + " " + inputs[j][1] + " " + expOutputs[j][0] + " " + recOutput[recOutput.length - 1][0]);
				//System.out.println();
				
				error += meanSquareError(recOutput[recOutput.length - 1], expOutputs[j]);
			}
			error /= inputs.length;
			System.out.println(error);
		}
	}

	public int getNumberOfLayers()
	{
		return numberOfNeurons.length - 1;
	}
	
	public void addNodeMutation(MersenneTwisterFast rand)
	{
		System.out.println("Mutacja wierzcho³ków");
		//TODO do
	}
	
	public void addLinkMutation(MersenneTwisterFast rand)
	{
		System.out.println("Mutacja po³¹czeñ");
		//TODO do
	}
	
	public void crossover(MersenneTwisterFast rand, NeuralNetworkIndividual ind)
	{
		System.out.println("Krzyzowanie");
		//TODO do
	}
	
	public Parameter defaultBase() {
		// TODO Auto-generated method stub
		return null;
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
