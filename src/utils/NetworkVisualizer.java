package utils;

import java.util.ArrayList;

public class NetworkVisualizer {

	
	protected ArrayList<String> links;
	
	public NetworkVisualizer()
	{
		ArrayList<String> links = new ArrayList<String>();
	}
	
	public void addLinks(ArrayList<Link> links)
	{
		for(Link l : links)
		{
			addLink(l);
		}
	}
	
	public void addLink(Link l)
	{
		links.add(l.input + " -> " + l.output + ";" );
	}
	
	public void addLink(String in, String out)
	{
		links.add(in + " -> " + out + ";");
	}
	
	public void addLink(int in, int out)
	{
		links.add(in + " -> " + out + ";");
	}
	
	public void visualizeNetwork()
	{
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		for(String s : links)
		{
			gv.addln(s);
		}
		gv.addln(gv.end_graph());
		System.out.println(gv.getDotSource());
	}
	
}
