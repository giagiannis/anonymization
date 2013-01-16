package distributed;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSReader {
	
	private FileSystem fs;
	private int[] ranges;
	public HDFSReader(String input) throws Exception{
		Configuration conf = new Configuration();
		this.fs = FileSystem.get(conf);
		if(!fs.exists(new Path(input)))
			throw new Exception("Given directory does not exist!");
		this.ranges=new int[fs.listStatus(new Path(input)).length];
		int i=0;
		for(FileStatus temp:fs.listStatus(new Path(input))){
			if(temp.getPath().toString().contains("part-")){			//contains results and not hadoop info
				System.out.println("Getting ranges for"+temp.getPath());
				this.getRanges(temp.getPath(), i);
				i++;
			}
		}
		for(int q:this.ranges){
			System.out.println(q);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void getRanges(Path inputFile, int index) throws IOException{
		FSDataInputStream in = fs.open(inputFile);
		int a=new Integer(in.readLine().split("\t")[0]), b=a;
		String buffer;
		while((buffer=in.readLine())!=null)
			b=new Integer(buffer.split("\t")[0]);
		this.ranges[index]=b-a+1;
		
	}

	public static void main(String args[]){
		 try {
			HDFSReader reader = new HDFSReader(args[0]);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
}
