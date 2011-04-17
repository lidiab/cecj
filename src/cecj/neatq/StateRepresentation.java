package cecj.neatq;

public abstract class StateRepresentation {
	public abstract double[] getState();
	public abstract void setState(double[] newState);
	public abstract void initState();
	public abstract double takeAction(ActionRepresentation action);
	public abstract boolean isTerminalState();	
	public abstract StateRepresentation clone();
}
