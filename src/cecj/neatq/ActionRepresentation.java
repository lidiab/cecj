package cecj.neatq;

public abstract class ActionRepresentation {
	
	
	public abstract boolean isValid(StateRepresentation currentState);
	public abstract double[] getActionState();
	public abstract ActionRepresentation clone();
}
