import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;



public class File_Handler implements Serializable{

	private String name, fullPath, owner, group;
	private long creationTime, lastAccessTime, lastModified, size;
	private Set<PosixFilePermission> permissions;
	//private Object fileKey;
	
	public File_Handler(File f){
		this.name = f.getName();
		this.fullPath = f.getAbsolutePath();
		
		try {		
			//Dates (Creation, Access, Modified), Size, FileKey, hashCode, Ower, Group, Permissions,
			Path path = Paths.get(this.fullPath);
			PosixFileAttributes posixAttr = Files.readAttributes(path, PosixFileAttributes.class);
	 
			//Times
			this.creationTime = posixAttr.creationTime().toMillis();
			this.lastModified = posixAttr.lastModifiedTime().toMillis();
			this.lastAccessTime = posixAttr.lastAccessTime().toMillis();
			
			//Owner and Group
			this.owner = posixAttr.owner().getName();
			this.group = posixAttr.group().getName();
			
			//Permissions
			this.permissions = posixAttr.permissions();
			
			//FileKey
			//this.fileKey = posixAttr.fileKey();
			
		} catch (Exception e) {
		}
	}

	//Get's && Set's
	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Set<PosixFilePermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<PosixFilePermission> permissions) {
		this.permissions = permissions;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public Boolean checkDiffs(File f, Logger logger){
		PosixFilePermission p;
		Boolean change = false;
		
		try {		
			//Dates (Creation, Access, Modified), Size, FileKey, hashCode, Ower, Group, Permissions,
			Path path = Paths.get(this.fullPath);
			PosixFileAttributes posixAttr = Files.readAttributes(path, PosixFileAttributes.class);
	 
			//Times
			if(this.creationTime != posixAttr.creationTime().toMillis()){ 
				System.out.println("Modified Creation: " + this.name);
				logger.log(Level.INFO, "Modified Creation: " + this.name);
				this.creationTime = posixAttr.creationTime().toMillis();
				change = true;
			}
			
			if(this.lastModified != posixAttr.lastModifiedTime().toMillis()){
				System.out.println("Modified LastModification: " + this.name);
				logger.log(Level.INFO, "Modified LastModification: " + this.name);
				this.lastModified = posixAttr.lastModifiedTime().toMillis();
				change = true;
			}
			
			/*if(this.lastAccessTime != posixAttr.lastAccessTime().toMillis()){
				System.out.println("Modified LastAccess: " + this.name + this.lastAccessTime);
				this.lastAccessTime = posixAttr.lastAccessTime().toMillis();
				change = true;
			}*/
			
			//Owner and Group
			if(this.owner == posixAttr.owner().getName()){
				System.out.println("Modified Owner: " + this.name);
				logger.log(Level.SEVERE, "Modified Owner: " + this.name);
				this.owner = posixAttr.owner().getName();
				change = true;
			}
			
			if(this.group == posixAttr.group().getName()){
				System.out.println("Modified Group: " + this.name);
				logger.log(Level.SEVERE, "Modified Group: " + this.name);
				this.group = posixAttr.group().getName();
				change = true;
			}
			
			//Permissions
			Set<PosixFilePermission> toCompare = posixAttr.permissions();
			ArrayList<PosixFilePermission> removed = new ArrayList<PosixFilePermission>();
			
			for(PosixFilePermission pS: this.permissions){
				
				if(toCompare.contains(pS)){
					toCompare.remove(pS);
				
				} else {
					removed.add(pS);
					System.out.println("Remove: " + pS.name() + " | " + this.name);
					logger.log(Level.WARNING, "Permissions Removed: " + pS.name() + " | " + this.name); 
				}
			}
			
			//If some permission is removed
			if(removed.size() != 0){
				for(PosixFilePermission pS: removed)
					this.permissions.remove(pS);
				change = true;
			}
			
			if(toCompare.size() != 0){
				Iterator<PosixFilePermission> it = toCompare.iterator();
				
				while(it.hasNext()){
					p = it.next();
					
					System.out.println("Add: " + p.name()+ " | " + this.name );
					logger.log(Level.WARNING, "Permissions Added: " + p.name() + " | " + this.name); 
					this.permissions.add(p);
					change = true;
				}
			}
			
		} catch (Exception e) {
		}
		return change;
	}
	
}
