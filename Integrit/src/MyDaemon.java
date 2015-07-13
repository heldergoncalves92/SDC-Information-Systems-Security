
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

public class MyDaemon implements Daemon{

    private Integrit myThread; 
    
    public MyDaemon(){}
  
    @Override
    public void init(DaemonContext daemonContext) throws DaemonInitException, Exception {
        /*
         * Construct objects and initialize variables here.
         * You can access the command line arguments that would normally be passed to your main() 
         * method as follows:
         */
        String[] args = daemonContext.getArguments(); 
        
        //Create a new Integrit Object (if necessary give some args) 
        myThread = new Integrit();
    }

    @Override
    public void start() throws Exception {
    	System.out.println("Func START");
        myThread.start();
    }

    @Override
    public void stop() throws Exception {
    	System.out.println("FUNC STOP");
    	myThread.stopIntegrit();
        try{
            myThread.join(1000);
        }catch(InterruptedException e){
            System.err.println(e.getMessage());
            throw e;
        }
    }
    
    @Override
    public void destroy() {
    	System.out.println("FUNC DESTROY");
        myThread = null;
    }
    /*
    public static void main(String[] args) {
		MyDaemon d = new MyDaemon();
		try {
			d.init(null);
			d.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
