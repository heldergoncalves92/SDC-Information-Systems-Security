import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;


public class Directory extends File_Handler implements Serializable{

	private HashMap<String, File_Handler> files;
	private HashMap<String, Directory> directories;
	private Boolean change;

	public Directory(File dir){
		super(dir);
		this.files = new HashMap<String, File_Handler>();
		this.directories = new HashMap<String, Directory>();
	}

	//Get's && Set's
	public HashMap<String, File_Handler> getFiles() {
		return files;
	}

	public void setFiles(HashMap<String, File_Handler> files) {
		this.files = files;
	}

	public HashMap<String, Directory> getDirectories() {
		return directories;
	}

	public void setDirectories(HashMap<String, Directory> directories) {
		this.directories = directories;
	}
	

	private ArrayList<String> addFileToArray(Collection<File_Handler> collection){
		ArrayList<String> list = new ArrayList<String>();
		
		for(File_Handler f: collection)
			list.add(f.getName());
		
		return list;
	}
	
	private ArrayList<String> addDirToArray(Collection<Directory> collection){
		ArrayList<String> list = new ArrayList<String>();
		
		for(Directory d: collection)
			list.add(d.getName());
		
		return list;
	}
	
	private void checkFile(ArrayList<String> onDir, File f, Logger logger){
		File_Handler aux;
		
		if((aux = this.files.get(f.getName())) != null){
			onDir.remove(aux.getName());
			
			//Check if something change 
			this.change = aux.checkDiffs(f, logger) || this.change;
				
		
		} else{
			System.out.println("New File: " + f.getName());
			this.files.put(f.getName(), new File_Handler(f));
			this.change = true;
		}
	}
	

	public Boolean readDir(File dir, int level, Logger logger){
		String[] list = dir.list();
		
		//String sLevel = printTab(level);
		Directory dirAux;
		ArrayList<String> filesOnDir = addFileToArray(this.files.values());
		ArrayList<String> dirOnDir = addDirToArray(this.directories.values());
		this.change = false;
		
		for(String s: list){
			
			//Read and Creat a JavaFile 
			//System.out.println(sLevel + s);
			File f = new File(dir.getAbsolutePath() +"/"+ s);
			
			//If is a directory 
			if(f.isDirectory()){
				if((dirAux = this.directories.get(f.getName())) != null){
					dirOnDir.remove(f.getName());
					
					//Check if something change 
					this.change = dirAux.checkDiffs(f, logger) || this.change;
					
				}else{
					System.out.println("New Directory: " + f.getName());
					dirAux = new Directory(f);
					this.directories.put(f.getName(), dirAux);
					this.change = true;
				}
				
				//Check every files inside
				this.change = dirAux.readDir(f, level+1, logger) || this.change; 
			
			} else {
				//Check File Properties
				checkFile(filesOnDir, f, logger);
			}
		}	
		
		//Check if any file or directory was deleted
		for(String s: filesOnDir){
			System.out.println("Deleted File: " + s);
			this.files.remove(s);
			this.change = true;
		}
		
		for(String s: dirOnDir){
			System.out.println("Deleted Directory: " + s);
			this.directories.remove(s);
			this.change = true;
		}
		
		return this.change;
	}
	
}
