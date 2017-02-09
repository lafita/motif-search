package main;

import java.util.Arrays;
import java.util.function.Predicate;

import javax.vecmath.Point3d;

import org.biojava.nbio.structure.Calc;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureTools;
import org.biojava.nbio.structure.geometry.CalcPoint;
import org.biojava.nbio.structure.secstruc.DSSPParser;
import org.biojava.nbio.structure.secstruc.SecStrucType;

/**
 * Take an input structure and return a boolean if the conditions are satisfied.
 * 
 * @author Aleix Lafita
 *
 */
public class MotifFilter implements Predicate<Structure> {

	@Override
	public boolean test(Structure s) {

		try {
			
			// In case there are errors in parsing
			if (s == null)
				return false;
			
			System.out.println("Evaluating " + s.getIdentifier());

			// Assign the secondary structure
			DSSPParser.fetch(s.getIdentifier(), s, true);

			for (Chain c : s.getChains()) {

				// Short chains are discarded
				int chainLen = c.getAtomGroups().size();
				if (chainLen < MotifParams.CHAIN_LENGTH)
					continue;

				boolean cont = false;

				// Check the helix in the N-terminal
				for (int ter = 0; ter < MotifParams.HELIX_LENGTH; ter++) {
					SecStrucType ss = (SecStrucType) c.getAtomGroup(ter + 1).getProperty(Group.SEC_STRUC);
					if (!ss.isHelixType()) {
						cont = true;
						break;
					}
				}

				// Continue in case there was a residue that was not helix
				if (cont)
					continue;

				// Check the helix in the C-terminal
				for (int ter = 0; ter < MotifParams.HELIX_LENGTH; ter++) {
					SecStrucType ss = (SecStrucType) c.getAtomGroup(chainLen - 2 - ter).getProperty(Group.SEC_STRUC);
					if (!ss.isHelixType()) {
						cont = true;
						break;
					}
				}

				// Continue in case there was a residue that was not helix
				if (cont)
					continue;

				// Check for the distance from N to C-terminal regions
				Point3d centroidN = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(StructureTools.getRepresentativeAtomArray(c)), 0, MotifParams.HELIX_LENGTH));
				
				Point3d centroidC = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(StructureTools.getRepresentativeAtomArray(c)), chainLen - MotifParams.HELIX_LENGTH, chainLen));

				if (Math.abs(centroidN.distance(centroidC) - MotifParams.NC_DISTANCE) < 1) {
					System.out.println("Found a hit: " + s.getIdentifier());
					return true;
				}
			}

			return false;

		} catch (Exception e) {

			return false;
		}
	}
}
