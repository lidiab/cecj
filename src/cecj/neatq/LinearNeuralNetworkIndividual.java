package cecj.neatq;

public class LinearNeuralNetworkIndividual extends NeuralNetworkIndividual {

	public LinearNeuralNetworkIndividual(int[] numberOfNeurons) {
		super(numberOfNeurons);
	}

	@Override
	protected double activation(double x) {
		return x;
	}

	@Override
	protected double activationDerivative(double z) {
		return 1;
	}

}
