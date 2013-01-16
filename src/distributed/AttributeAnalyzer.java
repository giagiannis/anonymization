package distributed;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import data.Tuple;


class MyIntWritable implements WritableComparable<MyIntWritable> {

	private int value, partition;
	
	public MyIntWritable(){
		;
	}
	
	public MyIntWritable(int value, int partition){
		this.value=value;
		this.partition=partition;
	}
	
	public void setValue(int val){
		this.value=val;
	}
	
	public void setPartition(int partition){
		this.partition=partition;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public int getPartition(){
		return this.partition;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		this.value=in.readInt();
		this.partition=in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.value);
		out.writeInt(this.partition);
		
	}

	@Override
	public int compareTo(MyIntWritable o) {
		MyIntWritable a=(MyIntWritable)o;
		if(this.getValue()<a.getValue())
			return -1;
		else if(this.getValue()==a.getValue())
			return 0;
		else
			return 1;
	}
	
}

class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, MyIntWritable, MyIntWritable>{

	private int qid[];
	public void configure(JobConf conf){
		String[] temp=conf.get("qid").split(" ");
		this.qid=new int[temp.length];
		for(int i=0;i<this.qid.length;i++)
			this.qid[i]=new Integer(temp[i]);
	}
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<MyIntWritable, MyIntWritable> out, Reporter reporter)
			throws IOException {
		Tuple tuple=new Tuple(value.toString().split(","));
		for(int i=0;i<this.qid.length;i++){
			out.collect(new MyIntWritable(tuple.getValue(this.qid[i]), i), new MyIntWritable(1, 0));
		//	System.out.println("<reducer "+i+"> "+tuple.getValue(this.qid[i]));
		}
	}
	
	public void close(){
		
	}
	
}

class MyReducer extends MapReduceBase implements Reducer<MyIntWritable, MyIntWritable, IntWritable, IntWritable>{

	@Override
	public void reduce(MyIntWritable key, Iterator<MyIntWritable> values,
			OutputCollector<IntWritable, IntWritable> out, Reporter reporter)
			throws IOException {
		int sum=0;
		while(values.hasNext())
			sum+=values.next().getValue();
		out.collect(new IntWritable(key.getValue()), new IntWritable(sum));
		
	}
	
}
 
class MyPartitioner implements Partitioner<MyIntWritable, MyIntWritable>{

	@Override
	public void configure(JobConf arg0) {
		
	}
 
	@Override
	public int getPartition(MyIntWritable key, MyIntWritable value, int max) {
		return key.getPartition()%max;
	}
	
}

public class AttributeAnalyzer {
	private String qid;
	
	public void runJob(String input, String output) throws IOException{
		JobConf conf = new JobConf(AttributeAnalyzer.class);
		conf.setJobName("Attribute Analyzer");
		
		conf.set("qid", this.qid);
		
		conf.setMapOutputKeyClass(MyIntWritable.class);
		conf.setMapOutputValueClass(MyIntWritable.class);
		
		conf.setOutputKeyClass(IntWritable.class);
		conf.setOutputValueClass(IntWritable.class);
		
		conf.setMapperClass(MyMapper.class);
		conf.setReducerClass(MyReducer.class);
		conf.setPartitionerClass(MyPartitioner.class);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
	
		conf.setNumMapTasks(1);
		conf.setNumReduceTasks(this.qid.split(" ").length);
		
		FileInputFormat.setInputPaths(conf, new Path(input));
		FileOutputFormat.setOutputPath(conf, new Path(output));

		JobClient.runJob(conf);
	}	
	
	public void setQID(String qid){
		this.qid=qid;
	}
	
	public static void main(String[] args){
		AttributeAnalyzer job = new AttributeAnalyzer();
		job.setQID("0 1 2 3 4 5 6 7 8 9");
		if(args.length<2){
			System.out.println("Usage:\thadoop jar <jar file> mapred.MedianFinder <input file/folder> <output folder>");
			System.exit(1);
		}
		try {
			job.runJob(args[0], args[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
