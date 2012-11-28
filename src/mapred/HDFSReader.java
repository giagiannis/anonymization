package mapred;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSReader {
	
	public HDFSReader(String input) throws Exception{
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		if(!fs.exists(new Path(input)))
			throw new Exception("Given directory doesnot exist!");
		for(int i=0;(fs.exists(new Path(input+"/part-0000"+i )));i++)
			System.out.println(i+":\tExists!");
	}

	public static void main(String args[]){
		 try {
			HDFSReader reader = new HDFSReader(args[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch bloc k 
			 e.printStackTrace();
		}
	}
}
