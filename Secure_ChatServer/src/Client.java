import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;


public class Client extends InitCipher{
	
	private byte[] iv = "1234567812345678".getBytes();
	private String username = "";
	
	private Message_Handler handler;
	private BufferedReader input;
	
	public Client(){}
	
	public void runClient(){
		Cipher cEncrypt, cDecrypt;
		
		PublicKey publicKey;
		Socket s;
		
		try {
			s = new Socket("localhost", 6000);
		
			//To accord the Diffie-Hellman Key
			DiffieHellman df = new DiffieHellman();
			KeyPair kPair = df.generateKey();
			
			//Init Message_Handler and Get PublicKey
			handler = new Message_Handler(s);
			publicKey = handler.SendRecvKey(kPair.getPublic());
			
			//Generate SessionKey
			byte[] key = df.sessionKey(kPair.getPrivate(), publicKey);
		
			//Init Cipher
			String type = "AES/CBC/NoPadding";	
			cDecrypt = initCipherByType(type, Cipher.DECRYPT_MODE, key, iv);
			cEncrypt = initCipherByType(type, Cipher.ENCRYPT_MODE, key, iv);
			
			//Prepare Message_Handler with Cipher
			handler.setCipher(cDecrypt, cEncrypt);
			handler.setMac(key);
			
			handler.initToSign();
			handler.initToVerify();
			handler.testSignature(publicKey);
			
			this.input = new BufferedReader(new InputStreamReader(System.in));
			
			//MainMenu
			mMain();
			
			s.close();
		} catch (IOException   e) {
			e.printStackTrace();
		}
	}
	
	
    private void mMain() {
        int choice = -1;
        handler.start();

        while (choice == -1) {
            System.out.println("####### Main Menu #######\n");
            System.out.println("-- [1] Login");
            System.out.println("-- [2] Secure Mode");
            System.out.println("-- [3] Register");
            System.out.println("-----:");
            System.out.println("-- [0] Exit");

            System.out.print("?> ");
            try {
                choice = Integer.parseInt(this.input.readLine());
            } catch (Exception ex) {
                // System.out.println("Error: Invalid Option");
                choice = -1;
            }

            switch (choice) {
                case 1:
                    clearConsole();
                    mLogin();
                    choice = -1;
                    clearConsole();
                    break;
                case 2:
                    clearConsole();
                    mSecureMode();
                    choice = -1;
                    clearConsole();
                    break;
                case 3:
                    clearConsole();
                    mRegister();
                    choice = -1;
                    clearConsole();
                    break;
                case 0:
                    clearConsole();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Error: Invalid Option");
                    choice = -1;
                    break;
            }
        }
    }
	
	private void mRegister() {
		String password = "", user = "", email = "";
        boolean status = true;
        
        handler.sendMessage("REGISTER");
        
        while(status){
        	
	    	try {
	        	System.out.println("####### Login #######\n");
	            System.out.println("-- Username:");
	            System.out.print("?> ");
	        
	            user = input.readLine();
	
	            System.out.println("-- Password:");
	            System.out.print("?> ");
	   
	            password = input.readLine();
	            
	            System.out.println("-- Email:");
	            System.out.print("?> ");
	   
	            email = input.readLine();
	        } catch (Exception ex) {
	            
	        }
	    	
	    	password = new String(Base64.getEncoder().encode(getHash(password)));
	    	handler.sendMessage(new Register(user, password, email));
	    	
	    	try {
	    		String recv = (String)handler.receiveMessage();
	    		recv.trim();
	    	
		    	if(recv.equals("OK")){
		    		System.out.println("Success!! Now you are Registered!\n Welcome " + user);
		    		status = false;
		    		
		    	}else if(recv.equals("USER_EXIST")){
		    		System.out.println("I am sorry, the username already exists! Try Again..");
		    	}
	    	
	    	} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
        }
		
	}

	private void mSecureMode() {
		String recv = "", code, password, passwordAUX;

	    try {
        	System.out.println("####### Secure Mode #######\n");
            System.out.println("-- Username:");
            System.out.print("?> ");
        
            username = input.readLine();
        } catch (Exception ex) {
            
        }
    	handler.sendMessage("SECURE_MODE");
    	handler.sendMessage(username);
    	
    	try {
    		System.out.println("Please, Wait a second..");
    		recv = (String)handler.receiveMessage();
    		recv.trim();
    		if(recv.equals("WAITING"))
    			System.out.println("The SECURE CODE was sent to your Email");
    		
    		else if(recv.equals("INVALID_USER")){
    			System.out.println("The USER is WRONG!! Try again..");
    			return;
    		}
    		else return;
    		
    		
    		while(true){
    			 try {
		            System.out.println("-- Insert your code:");
		            System.out.print("?> ");
		        
		            code = input.readLine();
		            handler.sendMessage(code);
		        } catch (Exception ex) {}
    			
    			recv = (String)handler.receiveMessage();
	    		recv.trim();
	    	
		    	if(recv.equals("OK")){
		    		System.out.println("Success! The SECURE CODE is correct!");
		    		int choice = -1;
		    		 while (choice == -1) {
		    			 System.out.println(   "###############################");
		    	            System.out.println("####### Change PASSWORD #######\n");
		    	            System.out.println("Do you want to set your PASSWORD?");
		    	            System.out.println("-- [1] Yes");
		    	            System.out.println("-- [2] No");
		    	            System.out.println("-----:");
		    	            System.out.print("?> ");
		    	            try {
		    	                choice = Integer.parseInt(this.input.readLine());
		    	            } catch (Exception ex) {
		    	                choice = -1;
		    	            }

		    	            switch (choice) {
		    	                case 1:
		    	                    clearConsole();
		    	                    handler.sendMessage("YES_CHANGE");
		    	                    while(true){
		    	                    	try {
		    	                    		System.out.println("-- Insert your new Password");
			    	                    	System.out.print("?> ");
		    		    	                password = this.input.readLine();
		    		    	                
		    		    	                System.out.println("-- Insert REPEAT your new Password");
			    	                    	System.out.print("?> ");
		    		    	                passwordAUX = this.input.readLine();
		    		    	                
		    		    	                if(password.equals(passwordAUX)){
		    		    	                	handler.sendMessage(new String(Base64.getEncoder().encode(getHash(password))));
		    		    	                	break;
		    		    	                }
		    		    	                else
		    		    	                	System.out.println("ERROR: The passwords are Different!! Try again..");
		    		    	            } catch (Exception ex) {}
		    	                    }
		    	                    clearConsole();
		    	                    break;
		    	                case 2:
		    	                    clearConsole();
		    	                    handler.sendMessage("NO_CHANGE");
		    	                    clearConsole();
		    	                    break;
		    	                default:
		    	                    System.out.println("Error: Invalid Option");
		    	                    choice = -1;
		    	                    break;
		    	            }
		    	        }
		    		break;
		    		
		    	}else if(recv.equals("NOK")){
		    		System.out.println("I am sorry, The SECURE CODE is Wrong! Try Again..");
		    	
		    	}else if(recv.equals("TIME_EXCEDED")){
		    		System.out.println("The time to insert the SECURE CODE is expired! Ask for a new SECURE CODE..");
		    		break;
		    
		    	} else if(recv.equals("TRYS_EXCEDED")){
		    		System.out.println("You already try this code three times!! Ask for a new SECURE CODE..");
		    		break;
		    
		    	}
    		}
    	
    	} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
	}

	private void mLogin() {
        String password = null;
        boolean status = true;
        
        handler.sendMessage("LOGIN");
        
        while(status){
        	
	    	try {
	        	System.out.println("####### Login #######\n");
	            System.out.println("-- E-Mail:");
	            System.out.print("?> ");
	        
	            username = input.readLine();
	
	            System.out.println("-- Password:");
	            System.out.print("?> ");
	   
	            password = input.readLine();
	        } catch (Exception ex) {
	            
	        }
	    	String msg;
	    	
	    	msg = new String(Base64.getEncoder().encode(getHash(password)));
	    	handler.sendMessage(new Login(username, msg));
	    	
	    	
	    	try {
	    		String recv = (String)handler.receiveMessage();
	    		recv.trim();
	    	
		    	if(recv.equals("OK")){
		    		System.out.println("Success, username and password are Right!\n Welcome " + username);
		    		
		    		status = false;
		    		//Start listening new messages from other clients
		    		handler.setListening(true);
		    		
		    		try {
		    			while(true){
							msg = input.readLine();
							handler.sendMessage((username + ": " + msg));
							
							//Reserved words
							if(msg.equals("OUT")) break;
		    			}
		    		} catch (IOException e) {
						e.printStackTrace();
					}
		    		
		    	}else if(recv.equals("NOK")){
		    		System.out.println("I am sorry, username or password wrong! Try Again..");
		    	
		    	} else if(recv.equals("BLOCKED")){
		    		System.out.println("I am sorry, you are BLOCKED! Try the SECURE MODE..");
		    		status = false;
		    	}
	    	
	    	} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
        }
    }
	
	 private byte[] getHash(String password) { //SHA-256
        byte byteData[] = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byteData = md.digest();
            
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
        return byteData;	
	 }

	public static void main(String[] args) {
		Client cli = new Client();
		cli.runClient();
	}
	
	// ------------------- OTHER STUFF ------------------
    // Clear Console
    public final void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            for (int clear = 0; clear < 50; clear++) {
                System.out.println("\b");
            }
        }
    }
	
	
	
}
