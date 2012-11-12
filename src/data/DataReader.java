package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataReader {

	private String filename;
	private BufferedReader file;
	private EquivalenceClass set;

	public DataReader(String filename) throws FileNotFoundException{
		this.filename=filename;
		this.file = new BufferedReader(new FileReader(this.filename));
		this.set = new EquivalenceClass();
		
	}
	
	public EquivalenceClass getTuples() throws IOException{
		while(this.file.ready())
			this.set.add(new Tuple(this.file.readLine().split(",")));
		this.file.close();
		return this.set;
	}
	
	public Tuple getNextTuple() throws IOException{
		if(this.file.ready())
			return new Tuple(this.file.readLine().split(","));
		else{
			this.file.close();
			return null;
		}
			
	}
}
