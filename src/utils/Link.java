package utils;


public class Link {
	String input;
	String output;

	public Link(String in, String out)
	{
		input = in;
		output = out;
	}
	
	public Link(int in, int out)
	{
		input = "" + in;
		output = "" + out;
	}
}