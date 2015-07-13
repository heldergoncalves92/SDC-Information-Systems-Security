import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Integrit extends Thread{

	/*********************************/
	/*********    ATENTION    ********/
	/*********   Some Paths   ********/
	/*********************************/
	private String pathObg = "/home/heldergoncalves/";
	private String basePath = "/home/heldergoncalves/Desktop/toVerify/toControl.txt";
	private String logPath = "/home/heldergoncalves/";
	
	private HashMap<String, Directory> mainDirs;
	private Boolean pause = false, working = true;
	private Logger logger;
	
	public Integrit(){
		this.mainDirs = new HashMap<String, Directory>();
		initLogger();
	}
	
	public Integrit(String pathObj){
		this.pathObg = pathObj;
		this.mainDirs = new HashMap<String, Directory>();
		initLogger();
	}
	
	private void initLogger(){
		try {
			this.logger = Logger.getLogger(Integrit.class.getName());
			Handler fileHandler = new FileHandler(logPath + "integrit.log", 2000, 5);
			fileHandler.setFormatter(new MyFormatter());
			this.logger.addHandler(fileHandler);
			System.out.println("LOGGER STARTED!!");
			
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	//Define Signal's
	private void initSignal(){
		
	    Signal.handle(new Signal("INT"), new SignalHandler() {
			public void handle(Signal sig) {  
				if(pause){ 
					pause = false;
					System.out.println("System is Running!!");
				} else {
					pause = true;
					System.out.println("System is Paused!!");
				}
			}
		});
	    
	    Signal.handle(new Signal("HUP"), new SignalHandler() {
			public void handle(Signal sig) {  
				System.out.println("Proccess Saved and Killed");
				try {
					FileOutputStream fout = new  FileOutputStream("/Users/heldergoncalves/ssi.obj");
					ObjectOutputStream oout = new ObjectOutputStream(fout);
					
					oout.writeObject(mainDirs);
					oout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
	}
	
	public Boolean loadControlDirectories(String path){
		ArrayList<String> list = new ArrayList<String>();
		Boolean change = false;
		String dirPath = "";
		File f;
		
		//Create a new set of Directories
		for(Directory d: mainDirs.values())
			list.add(d.getFullPath());
		
		try {
			BufferedReader buf = new BufferedReader(new FileReader(path));
			
			while((dirPath = buf.readLine()) != null){
				dirPath.trim();
				
				f = new File(dirPath);
				if(f != null && f.isDirectory()){
					if(!mainDirs.containsKey(f.getAbsolutePath())){
						System.out.println("Add Directory to Control!! - " + dirPath);
						mainDirs.put(f.getAbsolutePath(), new Directory(f));
						change = true;
					
					} else
						System.out.println("Directory Already Controled!! - " + dirPath);
				} else
					System.out.println("Isn't a Directory!! - " + dirPath);
				
				//Remove from list to detect if some Directory was deleted
				list.remove(f.getAbsolutePath());
			}
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//If some Directory was deleted
		Iterator<String> it = list.iterator();
		while(it.hasNext()){
			String s = it.next();
			System.out.println("Deleted Directory!! - " + s);
			mainDirs.remove(s);
			change = true;
		}
		
		return change;
	}
	
	public boolean dirChecks(){
		Boolean change = false;
		File f;
		
		for(Directory d: mainDirs.values()){
			
			f = new File(d.getFullPath());
			if(f.exists()){
				change =  d.readDir(f, 0, this.logger) || change; 
			
			}else{
				System.out.println("Main Directory Deleted!! - " + d.getFullPath());
				mainDirs.remove(d.getFullPath());
				change = true;
			}
		}
		
		return change;
	}
	
	
	public void run(){
		//Active Signals
		initSignal();
		
		//Read if exist a previous state
		FileInputStream fin;
		ObjectInputStream oin;
		
		try {
			fin = new  FileInputStream(this.pathObg +"ssi.obj");
			oin = new ObjectInputStream(fin);
			HashMap<String, Directory> aux = (HashMap<String, Directory>) oin.readObject();
			
			//Add the previous state
			if(aux != null)
				mainDirs = aux;
			oin.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			//Load Directories To Control
			if(loadControlDirectories(basePath)){
				FileOutputStream fout = new  FileOutputStream(this.pathObg + "ssi.obj");
				ObjectOutputStream oout = new ObjectOutputStream(fout);
				
				oout.writeObject(mainDirs);
				oout.close();
				
				System.out.println("New State Version - 1");
			}
				
			if(mainDirs.size() == 0){
				System.out.println("No Directoryes to Control!!");
				return;
			}
			
			while(this.working){
			
				//If system is paused
				while(pause)Thread.sleep(100);
				
				//Check all directories
				if(dirChecks()){
					FileOutputStream fout = new  FileOutputStream(this.pathObg + "ssi.obj");
					ObjectOutputStream oout = new ObjectOutputStream(fout);
					
					oout.writeObject(mainDirs);
					oout.close();
					
					System.out.println("New State Version");
				}	
				
				//Wait some time between Checks
				Thread.sleep(2000);
			}	
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopIntegrit(){
		this.working = false;
	}
}
