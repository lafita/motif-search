package main;

/**
 * Some parameters for the Motif Search.
 * 
 * @author Aleix Lafita
 *
 */
class MotifParams {
	
	/**
	 * Length in residues of the helical regions at the N and C terminus.
	 */
	static final int HELIX_LENGTH = 10;
	
	/**
	 * Minimum length of the chains to consider.
	 */
	static final int CHAIN_LENGTH = 200;
	
	/**
	 * Distance in Amstrongs from the N to the C terminal helical regions.
	 */
	static final double NC_DISTANCE = 11.0;

	/**
	 * Minimum oligomeric state.
	 */
	static final double MIN_OLIGO = 3;
	
	/**
	 * Max oligomeric state.
	 */
	static final double MAX_OLIGO = 10;
	
}
