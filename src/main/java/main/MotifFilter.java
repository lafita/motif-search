package main;

import org.apache.spark.api.java.function.Function;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.secstruc.DSSPParser;
import org.biojava.nbio.structure.secstruc.SecStrucTools;

import scala.Tuple2;

/**
 * Take an input structure and return a boolean if the conditions are satisfied.
 * 
 * @author Aleix Lafita
 *
 */
public class MotifFilter implements Function<Tuple2<String, Structure>, Boolean> {

	private static final long serialVersionUID = 3812703935579649962L;

	@Override
	public Boolean call(Tuple2<String, Structure> t) throws Exception {

		Structure structure = t._2;
		
		DSSPParser.fetch(t._1, structure, true);
		
		//SecStrucTools.getSecStrucInfo(structure);
		
		
		return false;
	}
}
