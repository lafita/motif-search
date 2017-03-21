package main;

/**
 * Some parameters for the Motif Search.
 * 
 * @author Aleix Lafita
 *
 */
class MotifParams {

	/**
	 * Minimum oligomeric state.
	 */
	static final double MIN_OLIGO = 1;

	/**
	 * Max oligomeric state.
	 */
	static final double MAX_OLIGO = 10;

	/**
	 * Minimum length of the chains to consider. This has to always be higher
	 * than 2 times the {@link #HELIX_LENGTH}.
	 */
	static final int CHAIN_LENGTH = 300;

	/**
	 * Length in residues of the helical regions at the N and C terminus. It has
	 * to be higher than 3 residues..
	 */
	static final int HELIX_LENGTH = 15;

	/**
	 * Distance in Amstrongs from the N to the C terminal helical regions.
	 */
	static final double NC_DISTANCE = 11.0;

	/**
	 * The maximum allowed distance deviation from the {@link #NC_DISTANCE}, in
	 * Amstrongs.
	 */
	static final double NC_DISTANCE_EPSILON = 1.0;

	/**
	 * Maximum allowed angle between the two terminal helices in radians.
	 */
	static final double MAX_HELIX_ANGLE = Math.PI / 12; // 15 degrees

}
