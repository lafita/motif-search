package main;

import java.io.IOException;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.biojava.nbio.structure.io.mmtf.MmtfStructureReader;
import org.biojava.nbio.structure.Structure;
import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.decoder.ReaderUtils;
import org.rcsb.mmtf.decoder.StructureDataToAdapter;
import org.rcsb.mmtf.decoder.DefaultDecoder;
import org.rcsb.mmtf.serialization.MessagePackSerialization;
import scala.Tuple2;

/**
 * The MotifSearch is a simple program that searches through the PDB to find
 * specific structural motifs.
 * <p>
 * At the moment it can only be customized by modifying the code (no CLI).
 * 
 * @author Aleix Lafita
 *
 */
public class MotifSearch implements Serializable {

	private static final long serialVersionUID = 1239186643368091857L;

	public static void main(String[] args) throws IOException, InterruptedException {

		// This is the default 2 line structure for Spark applications
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(MotifSearch.class.getSimpleName());

		// Set the config for the spark context
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> jprdd = sc.sequenceFile(Parameters.HADOOP_SEQ_FILE, Text.class, BytesWritable.class, 8)
				// Test Spencer example only
				.filter(t -> t._1.toString().equalsIgnoreCase("1LW8"))
				// Random lines not sure what they are doing
				.mapToPair(t -> new Tuple2<String, byte[]>(t._1.toString(), ReaderUtils.deflateGzip(t._2.getBytes())))
				.mapToPair(t -> new Tuple2<String, MmtfStructure>(t._1,
						new MessagePackSerialization().deserialize(new ByteArrayInputStream(t._2))))
				.mapToPair(t -> new Tuple2<String, StructureDataInterface>(t._1, new DefaultDecoder(t._2)))
				// Now convert to Biojava strcutre
				.mapToPair(t -> {
					MmtfStructureReader mmtfStructureReader = new MmtfStructureReader();
					new StructureDataToAdapter(t._2, mmtfStructureReader);
					return new Tuple2<String, Structure>(t._1, mmtfStructureReader.getStructure());})
				.filter(new MotifFilter())
				.map(t -> t._1);

		// Collect and print to the stdout the search hits
		List<String> results = jprdd.collect();
		System.out.println(String.format("\nFound %d hits: ", results.size()));
		
		for (String hit : results)
			System.out.println(hit);

	}

}
