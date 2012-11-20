package mapred;

import java.io.IOException;
import java.util.Iterator;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Partitioner;

class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> out, Reporter rep)
			throws IOException {
		String[] tuple=value.toString().split(",");
		for(Integer i=1;i<=tuple.length;i++)
			out.collect(new Text(i.toString()), new Text(tuple[i-1]));
		
	}
	
}


class MyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>{

	@Override
	public void reduce(Text key, Iterator<Text> value,
			OutputCollector<Text, Text> out, Reporter rep)
			throws IOException {
		out.collect(key, key);
	}
	
}

class MyPartitioner extends Partitioner<Text, Text>{

	@Override
	public int getPartition(Text key, Text value, int maxPartitions) {
		if(key.toString().split(" ")[0].equals("RANGE"))
			return 0;
		else
			return 0;
	}
	
}

public class MedianFinder {
	
	private int numberOfAttributes;
	private String[] qid;



	public void setNumberOfAttributes(int att){
		this.numberOfAttributes=att;
	}
	
	public void setQID(String[] qid){
		this.qid=qid;
	}
	
	
	public void runJob(String input, String output) throws IOException{
		JobConf conf = new JobConf(MedianFinder.class);
		conf.setJobName("MedianFinder");
		
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		
		conf.setMapperClass(MyMapper.class);
		conf.setReducerClass(MyReducer.class);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		//conf.setNumMapTasks(1);
		conf.setNumReduceTasks(this.numberOfAttributes+1);
		
		
		FileInputFormat.setInputPaths(conf, new Path(input));
		FileOutputFormat.setOutputPath(conf, new Path(output));
		
		JobClient.runJob(conf);
	}
	
	
	
	public static void main(String[] args){
		MedianFinder job = new MedianFinder();
		try {
			job.runJob("dataUNIFORM.txt", "dataOUTUNI");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
