package main;

/**
 * Some parameters for the Motif Search.
 * 
 * @author Aleix Lafita
 *
 */
class MotifParams {

	/**
	 * Path to the Hadoop sequence file of the PDB.
	 */
	static final String HADOOP_SEQ_FILE = "/scratch/full-201605";
	
	/**
	 * Length of the helix.
	 */
	static final int HELIX_LENGTH = 15;
	
	/**
	 * Minimum chain length to consider.
	 */
	static final int CHAIN_LENGTH = 400;
	
	/**
	 * Distance in A from the N to the C terminal helix regions
	 */
	static final double NC_DISTANCE = 11.0;

	
}
