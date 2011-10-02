package utils;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class NetworkVisualizer {

	JFrame frame;
	JLabel label;
	ImageIcon image;
	protected ArrayList<String> links;
	
	public NetworkVisualizer()
	{
		links = new ArrayList<String>();
		
		frame = new JFrame();
		frame.setSize(800, 600);
		
		image = new ImageIcon();
		label = new JLabel(image);
		
		Container content = frame.getContentPane();
		content.add(new JScrollPane(label));
		
		frame.setVisible(true);
	}

	public void setLinks(ArrayList<Link> links)
	{
		this.links.clear();
		addLinks(links);
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
	
	public void saveToFile(String fileName)
	{
		File file = new File(fileName);
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		for(String s : links)
		{
			gv.addln(s);
		}
		gv.addln(gv.end_graph());
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), "png"), file);
	}
	
	public void visualizeNetwork()
	{
		File file = new File("graph.png");
		 GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		for(String s : links)
		{
			gv.addln(s);
		}
		gv.addln(gv.end_graph());
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), "png"), file);
		
		try {
			image.setImage(ImageIO.read(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frame.toFront();
		frame.repaint();
		
		//System.out.println(gv.getDotSource());
	}
	
}
