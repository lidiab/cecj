package cecj.neatq;

import ec.util.MersenneTwisterFast;

public abstract class QLearn {
	
	public void qLearnAlgorithm(NeuralNetworkIndividual network1, NeuralNetworkIndividual network2, 
			int e, double c, double eps, double learningSpeed, double gamma, double lambda)
	{
		MersenneTwisterFast rand = new MersenneTwisterFast(); //TODO u¿yc generatora z ecj
		//int[] numberOfNeurons = {64, 128, 128, 64}; //TODO do zrobienia losowanie wag
		//LogisticNeuralNetworkIndividual network = new LogisticNeuralNetworkIndividual(numberOfNeurons);
		for(int i = 0; i < e; i++)
		{
			StateRepresentation s = null;
			StateRepresentation sp = initStateRepresentation();
			sp.initState();
			
			ActionRepresentation a = null;
			ActionRepresentation ap = null;
			
			double reward = 0;
			do{
				System.out.println(sp.toString());
				ap = makeMove(network1, c, eps, learningSpeed, rand, s, sp, a, reward, gamma, lambda);
				System.out.println(ap.toString());
				s = sp.clone();
				a = ap.clone();
				
				reward = sp.takeAction(ap);
			}
			while(!s.isTerminalState());
		}
	}

	protected ActionRepresentation makeMove(NeuralNetworkIndividual network,
			double c, double eps, double learningSpeed,
			MersenneTwisterFast rand, StateRepresentation s,
			StateRepresentation sp, ActionRepresentation a, double reward, double gamma, double lambda) {
		ActionRepresentation ap;
		double[] Q = network.getLastOutput(sp.getState());
		for(int j = 0; j < Q.length; j++)
		{
			Q[j] *= c;
		}
		
		double r = rand.nextDouble();
		ActionRepresentation[] actionSet = getActionSet();
		double max = Double.NEGATIVE_INFINITY;
		int maxIndex = -1;
		for(int j = 0; j < Q.length; j++)
		{
			if ((Q[j] > max) && (actionSet[j].isValid(sp)))
			{
				max = Q[j];
				maxIndex = j;
			}
		}
		  
		if(r < eps)
		{
			int validNumber = 0;
			for(int j = 0; j < actionSet.length; j++)
			{
				if (actionSet[j].isValid(sp))
				{
					validNumber++;
				}
			}
			int index = rand.nextInt(validNumber);
			int actIndex = -1;
			for(int j = 0; (j < actionSet.length)&&(index >= 0); j++)
			{
				if(actionSet[j].isValid(sp))
				{
					actIndex = j;
					index--;
				}
			}
			ap = actionSet[actIndex];
		}
		else
		{
			ap = actionSet[maxIndex];
			
		}
		if(s != null)
		{
			network.backpropagationAlgorithm(s.getState(), a.getActionState(), (reward + gamma * max) / c, learningSpeed, gamma, lambda);
		}
		return ap;
	}
	
	public abstract StateRepresentation initStateRepresentation();
	
	public abstract ActionRepresentation[] getActionSet();
}
