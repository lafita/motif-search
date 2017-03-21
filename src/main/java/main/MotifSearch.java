package main;

import java.util.List;
import java.util.stream.Collectors;

import org.biojava.nbio.structure.rcsb.PdbIdLists;

/**
 * The MotifSearch is a simple program that searches through the PDB to find
 * specific structural motifs.
 * <p>
 * At the moment it can only be customized by modifying the code (no CLI).
 * 
 * @author Aleix Lafita
 *
 */
public class MotifSearch {

	public static void main(String[] args) throws Exception {

		// Use the java parallel stream to process the data
		List<String> results = PdbIdLists.getCurrentPDBIds().parallelStream()
				// Test one example only
				//.filter(t -> t.toString().equalsIgnoreCase("1I5Q"))
				// Use the custom filtering function
				.filter(new MotifFilter())
				// Now convert back into a string and collect
				.collect(Collectors.toList());

		// Print to the stdout the search hits
		System.out.println(String.format("\nFound %d hits: ", results.size()));
		
		for (String hit : results)
			System.out.println(hit);

	}

}
