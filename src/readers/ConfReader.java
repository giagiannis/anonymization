package readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ConfReader {
	private RandomAccessFile reader;

	public ConfReader(String confname) throws FileNotFoundException {
		this.reader = new RandomAccessFile(confname,"r");
	}
	
	public String getValue(String value) throws IOException{
		this.reader.seek(0);
		String buffer = this.reader.readLine();
		while(buffer!=null){
			if(buffer.split("\t")[0].equals(value))
				return buffer.split("\t")[buffer.split("\t").length-1];
			else
				buffer = this.reader.readLine();
		}
		return null;
	}
	
}
