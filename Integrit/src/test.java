import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class test {

	public static void main(String[] args) {
		
		try {
			Logger logger = Logger.getLogger(test.class.getName());
			Handler fileHandler = new FileHandler("/Users/heldergoncalves/loggerTESTE.log", 2000, 5);
		
			fileHandler.setFormatter(new MyFormatter());
			logger.addHandler(fileHandler);
			
			
			for(int i=0; i<10; i++){
                //logging messages
                logger.log(Level.INFO, "Msg"+i);
                logger.log(Level.WARNING, "Msg"+i);
            }
            logger.log(Level.CONFIG, "Config data");
            /*
			logger.setLevel(Level.SEVERE);
			logger.config("Mensagem Bonita de LOG!!");
			
			logger.log(Level.FINE, "Muito GIRO!!! MUAHAHAH");
			*/	
			System.out.println("FIM!!");
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		try {
	        String[] command = new String[3];
	        command[0]= "/bin/bash";
	        command[1]= "-c";
	        command[2]= "logger p -1 15762";
	        
	        // Execute command
	        Runtime.getRuntime().exec(command);  
	    } catch (IOException e) {}
		
	}
}

