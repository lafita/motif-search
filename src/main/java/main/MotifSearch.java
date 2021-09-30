package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.biojava.nbio.structure.align.util.UserConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger(MotifSearch.class);
	private static final String CURRENT_ENTRY_URL = "https://data.rcsb.org/rest/v1/holdings/current/entry_ids";
	private static final String DEFAULT_PDBID_FILENAME = "pdb_list.txt";

	public static Collection<String> getCurrentPDBIds() throws IOException {

		URL url = new URL(CURRENT_ENTRY_URL);

		logger.info("Fetching {}", url);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		// Getting the response code
		int responseCode = conn.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException(
					String.format("Error connecting to %s (%d) %s", url, responseCode, conn.getContent()));
		}

		try (Reader r = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			StreamTokenizer tokenizer = new StreamTokenizer(r);
			tokenizer.whitespaceChars(',', ',');
			tokenizer.whitespaceChars('[', '[');
			tokenizer.whitespaceChars(']', ']');

			List<String> ids = new ArrayList<>(200000);
			while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == '\"') {
					String token = tokenizer.sval;
					ids.add(token);
				} else {
					logger.warn("Unexpected json format, line {}, token {}", tokenizer.lineno(),
							tokenizer.ttype > ' ' ? String.format("'%s'", (char) tokenizer.ttype) : tokenizer.ttype);
				}
			}
			return ids;
		}
	}

	public static Collection<String> cacheCurrentPDBIds() throws IOException {
		Path path = Paths.get((new UserConfiguration()).getCacheFilePath(), DEFAULT_PDBID_FILENAME);
		return cacheCurrentPDBIds(path);
	}

	public static Collection<String> cacheCurrentPDBIds(Path path) throws IOException {
		// Determine if file is from before last tuesday
		File file = path.toFile();
		if (file.exists()) {
			ZoneId zone = ZoneId.of("UTC");
			LocalDateTime modified = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), zone);
			LocalDateTime lastUpdate = LocalDate.now(zone).atTime(1, 0).with(TemporalAdjusters.previous(DayOfWeek.WEDNESDAY));

			logger.debug("Last Update: {}", lastUpdate);
			logger.debug("File modified: {}", modified);

			if (modified.isAfter(lastUpdate)) {
				// file is recent; don't re-download
				List<String> ids = new ArrayList<>((int) (file.length() / 5l + 1l));
				try (BufferedReader in = new BufferedReader(new FileReader(file))) {
					String line = in.readLine();
					while (line != null) {
						ids.add(line.strip());
						line = in.readLine();
					}
				}
				return ids;
			}

			logger.info("{} is outdated and will be re-downloaded", path);
		}

		Collection<String> ids = getCurrentPDBIds();
		file.getParentFile().mkdirs();
		try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
			String newline = System.lineSeparator();
			for (String id : ids) {
				out.write(id);
				out.write(newline);
			}
			if(!file.setLastModified(System.currentTimeMillis())) {
				logger.error("Updated {} but unable to set modification date.", file);
			}
		}

		return ids;
	}

	public static void main(String[] args) {
		try {
			// AtomCache cache = StructureIO.getAtomCache();
			// cache.setFiletype(StructureFiletype.MMTF);
			// Use the java parallel stream to process the data
			List<String> results = cacheCurrentPDBIds().parallelStream()
					// Test one example only
					// .filter(t -> t.equalsIgnoreCase("1I5Q"))
					// .filter(t -> t.equalsIgnoreCase("5I3T"))
					// .filter(t -> t.equalsIgnoreCase("1FCO"))
					// Use the custom filtering function
					.filter(new MotifFilter())
					// Now convert back into a string and collect
					.collect(Collectors.toList());

			// Print to the stdout the search hits
			System.out.println(String.format("%nFound %d hits: ", results.size()));

			for (String hit : results)
				System.out.println(hit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
