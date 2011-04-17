package cecj.neatq;

public class LogisticNeuralNetworkIndividual extends NeuralNetworkIndividual {

	public LogisticNeuralNetworkIndividual(int[] numberOfNeurons) {
		super(numberOfNeurons);
	}

	@Override
	protected double activation(double x)
	{
		return 1.0 / (1.0 + Math.exp(-x));
	}

	@Override
	protected double activationDerivative(double z) {
		return z * (1 - z);
	}

}
