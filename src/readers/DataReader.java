package readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import data.EquivalenceClass;
import data.Tuple;

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
		while(this.file.ready()){
			String buffer=this.file.readLine();
			if(buffer.charAt(0)!='#' || buffer.charAt(0)=='"')
				this.set.add(new Tuple(buffer.split(",")));
		}
		this.file.close();
		return this.set;
	}
	
	public Tuple getNextTuple() throws IOException{
		if(this.file.ready()){
			String buffer=this.file.readLine();
			while(buffer.charAt(0)=='#' || buffer.charAt(0)=='"')
				buffer=this.file.readLine();
			return new Tuple(buffer.split(","));
		}
		else{
			this.file.close();
			return null;
		}
			
	}
}
