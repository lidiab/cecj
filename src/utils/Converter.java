package utils;

import java.util.ArrayList;

import ec.Individual;


public abstract interface Converter {

	public abstract ArrayList<Link> convert(Individual ind);
}
