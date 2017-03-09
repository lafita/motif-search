package main;

import java.util.Arrays;
import java.util.function.Predicate;

import javax.vecmath.Point3d;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Calc;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureTools;
import org.biojava.nbio.structure.geometry.CalcPoint;
import org.biojava.nbio.structure.secstruc.DSSPParser;
import org.biojava.nbio.structure.secstruc.SecStrucState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Take an input structure and return a boolean if the conditions are satisfied.
 * 
 * @author Aleix Lafita
 *
 */
public class MotifFilter implements Predicate<Structure> {
	
	private static final Logger logger = LoggerFactory.getLogger(MotifFilter.class);

	@Override
	public boolean test(Structure s) {

		try {
			
			// In case there are errors in parsing
			if (s == null)
				return false;
			
			logger.debug("Evaluating " + s.getIdentifier());
			
			// Check the minimum and maximum oligomeric states
			if (s.getChains().size() < MotifParams.MIN_OLIGO)
				return false;
			if (s.getChains().size() > MotifParams.MAX_OLIGO)
				return false;
			
			// Assign the secondary structure
			DSSPParser.fetch(s.getIdentifier(), s, true);

			for (Chain c : s.getChains()) {

				// Short chains or non protein are discarded
				Atom[] atomArray = StructureTools.getRepresentativeAtomArray(c);
				int chainLen = atomArray.length;
				if (chainLen < MotifParams.CHAIN_LENGTH | !StructureTools.isProtein(c))
					continue;

				boolean cont = false;

				// Check the helix in the N-terminal
				for (int ter = 0; ter < MotifParams.HELIX_LENGTH; ter++) {
					SecStrucState ss = (SecStrucState) atomArray[ter + 1].getGroup().getProperty(Group.SEC_STRUC);
					if (!ss.getType().isHelixType()) {
						cont = true;
						break;
					}
				}

				// Continue in case there was a residue that was not helix
				if (cont)
					continue;
				
				logger.info("Passed condition 1 " + s.getIdentifier());

				// Check the helix in the C-terminal
				for (int ter = 0; ter < MotifParams.HELIX_LENGTH; ter++) {
					SecStrucState ss = (SecStrucState) atomArray[chainLen - 2 - ter].getGroup().getProperty(Group.SEC_STRUC);
					if (!ss.getType().isHelixType()) {
						cont = true;
						break;
					}
				}

				// Continue in case there was a residue that was not helix
				if (cont)
					continue;
				
				logger.info("Passed condition 2 " + s.getIdentifier());

				// Check for the distance from N to C-terminal regions
				Point3d centroidN = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), 0, MotifParams.HELIX_LENGTH));
				
				Point3d centroidC = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), chainLen - MotifParams.HELIX_LENGTH, chainLen));

				if (Math.abs(centroidN.distance(centroidC) - MotifParams.NC_DISTANCE) < 1) {
					logger.info("Found a hit: " + s.getIdentifier());
					return true;
				}
				
				// TODO check parallel helical vectors
				
				
				
			}

			return false;

		} catch (Exception e) {

			return false;
		}
	}
}
