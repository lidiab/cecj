package utils;

import java.util.ArrayList;

import cecj.neat.NeatIndividual;
import ec.Individual;

public class NeatConverter implements Converter {

	public ArrayList<Link> convert(Individual ind) {
		NeatIndividual neatind = (NeatIndividual) ind;
		
		ArrayList<Link> l = new ArrayList<Link>();
		
		for(NeatIndividual.Gene g : neatind.genotype)
		{
			if(g.enable)
			{
				l.add(new Link(g.inNode, g.outNode));
			}
		}
		
		
		return l;
	}

}
