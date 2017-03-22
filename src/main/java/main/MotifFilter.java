package main;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Calc;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.StructureTools;
import org.biojava.nbio.structure.geometry.CalcPoint;
import org.biojava.nbio.structure.secstruc.DSSPParser;
import org.biojava.nbio.structure.secstruc.SecStrucCalc;
import org.biojava.nbio.structure.secstruc.SecStrucState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Take an input structure and return a boolean if the conditions are satisfied.
 * 
 * @author Aleix Lafita
 *
 */
public class MotifFilter implements Predicate<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(MotifFilter.class);

	@Override
	public boolean test(String t) {

		try {
			
			// Parse the biological assembly of the structure
			Structure s = StructureIO.getBiologicalAssembly(t, false);

			int oligo = s.getChains().stream().filter(c -> StructureTools.isProtein(c)).
					collect(Collectors.toList()).size();

			// Check the minimum and maximum oligomeric state sizes -- check homo
			if (oligo < MotifParams.MIN_OLIGO)
				return false;
			if (oligo > MotifParams.MAX_OLIGO)
				return false;
						
			logger.debug("Evaluating " + t + " with " + oligo + " subunits.");
			
			s = StructureIO.getStructure(t);
			
			// Assign the secondary structure
			try {
				DSSPParser.fetch(t, s, true);
			} catch(Exception e){
				logger.warn("DSSP fetch for " + t + " failed: " + e);
				SecStrucCalc dssp = new SecStrucCalc();
				dssp.calculate(s, true);
			}

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
					if (ss == null){
						logger.warn("Ignoring residue without SS assignment: " + t);
						continue;
					}
					if (!ss.getType().isHelixType()) {
						cont = true;
						break;
					}
				}

				// Continue in case there was a residue that was not helix
				if (cont)
					continue;
				
				logger.info("There is a helix in the N-terminus: " + t);

				// Check the helix in the C-terminal
				for (int ter = 0; ter < MotifParams.HELIX_LENGTH; ter++) {
					SecStrucState ss = (SecStrucState) atomArray[chainLen - 2 - ter].getGroup().getProperty(Group.SEC_STRUC);
					if (ss == null){
						logger.warn("Ignoring residue without SS assignment: " + t);
						continue;
					}
					if (!ss.getType().isHelixType()) {
						cont = true;
						break;
					}
				}

				// Continue in case there was a residue that was not helix
				if (cont)
					continue;
				
				logger.info("There is a helix in the C-terminus: " + t);

				// Check for the distance from N to C-terminal regions
				Point3d centroidN = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), 1, MotifParams.HELIX_LENGTH));
				
				Point3d centroidC = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), chainLen - MotifParams.HELIX_LENGTH, chainLen));

				double distance = centroidN.distance(centroidC);
				logger.debug(String.format("The two terminal helices are at %.0f A apart: %s", 
								distance, t));
						
				if (Math.abs(distance - MotifParams.NC_DISTANCE) > MotifParams.NC_DISTANCE_EPSILON)
					return false;
				
				logger.info("The two terminal helices are at the specified disatance: " + t);
				
				// Calculate a starting and ending point of each helix using centroid of 3 residues
				Point3d startN = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), 1, 4));
				Point3d endN = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), MotifParams.HELIX_LENGTH - 3, MotifParams.HELIX_LENGTH));

				Point3d startC = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), chainLen - MotifParams.HELIX_LENGTH, chainLen + 3 - MotifParams.HELIX_LENGTH));
				Point3d endC = CalcPoint.centroid(Arrays.copyOfRange(
						Calc.atomsToPoints(atomArray), chainLen - 3, chainLen));
				
				// Check parallel helical vectors
				Vector3d helixN = new Vector3d();
				helixN.sub(endN, startN);
				helixN.normalize();
				
				Vector3d helixC = new Vector3d();
				helixC.sub(startC, endC);
				helixC.normalize();
				
				double angle = helixN.angle(helixC);
				logger.debug(String.format("The two terminal helices are at %.0f degrees: %s", 
						Math.toDegrees(angle), t));
				
				if (angle < MotifParams.MAX_HELIX_ANGLE){
					logger.info("The two terminal helices are parallel: " + t);
					return true;
				}
				
			}

			return false;

		} catch (Exception e) {
			logger.error(t + " errored: " + e);
			return false;
		}
	}
}
