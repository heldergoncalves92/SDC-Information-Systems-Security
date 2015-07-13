import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLiteJDBC {
	
	private Connection conn;
	
	public SQLiteJDBC(){
		try {
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:DataBase.db");
			System.out.println("Opened database successfully");
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public void createTables(){
		Statement stmt = null;
		
	    try {
	      stmt = this.conn.createStatement();
	      String sql = "CREATE TABLE USERS " +
	                   "(USERNAME		TEXT    NOT NULL PRIMARY KEY, " + 
	                   " PASSWORD		INT     NOT NULL, " + 
	                   " EMAIL			TEXT    NOT NULL, " +
	                   " LOGGED			INT    NOT NULL, " +
	                   " LOG_TRY 		INT 	NOT NULL)";
	      stmt.executeUpdate(sql);
	      
	      sql = "CREATE TABLE MESSAGES " +
                  "(ID 			INT		NOT NULL PRIMARY KEY," +
                  " ID_USER		INT		NOT NULL, " + 
                  " MESSAGE		TEXT     NOT NULL)";
	      
	      stmt.executeUpdate(sql);
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    System.out.println("Tables created successfully");
	}
	
	public void populateDB(){
		Statement stmt = null;
	      try {
			this.conn.setAutoCommit(false);
			
			stmt = this.conn.createStatement();
			String sql = "INSERT INTO USERS (USERNAME,PASSWORD,EMAIL,LOG_TRY, LOGGED) VALUES ('user1', 'password','heldergoncalves92@gmail.com', 0, 0);"; 
			stmt.executeUpdate(sql);

			sql = "INSERT INTO USERS (USERNAME, PASSWORD,EMAIL, LOG_TRY,LOGGED) VALUES ('user2', 'password','heldergoncalves92@live.com.pt', 0, 0);"; 
			stmt.executeUpdate(sql);

			sql = "INSERT INTO MESSAGES (ID,ID_USER,MESSAGE) VALUES (0, 1, 'Olá user2. Então está tudo bem?');"; 
			stmt.executeUpdate(sql);

			sql = "INSERT INTO MESSAGES (ID,ID_USER,MESSAGE) VALUES (1, 2, 'Tudo ótimo user1! À quanto tempo não nos vemos. Temos de marcar um café');"; 
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO MESSAGES (ID,ID_USER,MESSAGE) VALUES (2, 1, 'Mesmo!! Pode ser na quinta às 20h?');"; 
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO MESSAGES (ID,ID_USER,MESSAGE) VALUES (3, 2, 'Para mim está ótimo!! Até quinta.. :)');"; 
			stmt.executeUpdate(sql);
			
			this.conn.setAutoCommit(true);
			System.out.println("Tables populated successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getMessageByID(int id){
		Statement stmt = null;
		String msg = "";
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM MESSAGES WHERE ID="+ id + ";" );
			
			msg = rs.getString("message"); 
			System.out.println( "MESSAGE = " + msg );
			System.out.println();
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The MessageID doesn´t exist!!");
			return null;
		}
		return msg;
	}
	
	public String getLastNMessage(int N){
		Statement stmt = null;
		String msg = "";
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM MESSAGES ORDER BY ID DESC LIMIT "+ N + ";" );
			
			while ( rs.next() ) {
				msg = rs.getString("message"); 
				
		    }
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			return null;
		}
		return msg;
	}
	
	public String getUserByID(int id){
		Statement stmt = null;
		String msg = "";
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS WHERE ID="+ id + ";" );
			
			msg = rs.getString("username"); 
			System.out.println( "USER = " + msg );
			msg = rs.getString("password");
			System.out.println( "PASS = " + msg );
			System.out.println();
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("The UserID doesn´t exist!!");
			return null;
		}
		return msg;
	}
	
	public String getUserByUsername(String username){
		Statement stmt = null;
		String msg = "";
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS WHERE USERNAME='"+ username + "';" );
			
			msg = rs.getString("password");
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("The UserID doesn´t exist!!");
			return null;
		}
		return msg;
	}
	
	public boolean checkLogin(String username, String password){
		Boolean res = false;
		Statement stmt = null;
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT USERNAME FROM USERS WHERE USERNAME='"+ username + "' AND PASSWORD='"+ password + "';" );
			rs.getString("username");
			res=true;
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("Something wrong!!");
			res = false;
		}
		
		return res;
	}
	
	public void changePassword(String username, String password){
		Statement stmt = null;
		try {
			stmt = this.conn.createStatement();
			stmt.executeUpdate( "UPDATE USERS SET PASSWORD='" + password +  "' WHERE USERNAME='"+ username + "';" ); 
			
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("Something wrong!!");
		}
	}
	
	public int getTryByUser(String username){
		Statement stmt = null;
		int num = -1;
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT LOG_TRY FROM USERS WHERE USERNAME='"+ username + "';" );
			
			num = rs.getInt("log_try");
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("The UserID doesn´t exist!!");
			return -1;
		}
		return num;
	}
	
	public void setTryByUser(String username, int n){
		Statement stmt = null;
		try {
			stmt = this.conn.createStatement();
			stmt.executeUpdate( "UPDATE USERS SET LOG_TRY=" + n + " WHERE USERNAME='"+ username + "';" ); 
			
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("Something wrong!!");
		}
	}
	
	public boolean isUserLogged(String username){
		Statement stmt = null;
		boolean logged = false;
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT LOGGED FROM USERS WHERE USERNAME='"+ username + "';" );
			
			if(rs.getInt("logged") == 1) logged = true;
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("The UserID doesn´t exist!!");
		}
		return logged;
	}
	
	public void setUserLogged(String username, int logged){
		Statement stmt = null;
		try {
			stmt = this.conn.createStatement();
			stmt.executeUpdate( "UPDATE USERS SET LOGGED=" + logged + " WHERE USERNAME='"+ username + "';" ); 
			
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("Something wrong!!");
		}
	}
	
	public String getMailByUser(String username){
		Statement stmt = null;
		String mail = null;
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT EMAIL FROM USERS WHERE USERNAME='"+ username + "';" );
			mail = rs.getString("email");
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("The UserID doesn´t exist!!");
		}
		return mail;
	}
	
	public boolean containsUser(String username){
		Statement stmt = null;
		
		
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT USERNAME FROM USERS WHERE USERNAME='"+ username + "';" );
			rs.getString("username");
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("The UserID doesn´t exist!!");
			return false;
		}
		return true;
	}
	
	public void insertUser(String username, String password, String email){
		Statement stmt = null;
		try {
			stmt = this.conn.createStatement();
			stmt.executeUpdate( "INSERT INTO USERS (USERNAME,PASSWORD,EMAIL,LOG_TRY, LOGGED) VALUES ('"
					+ username + "', '"
					+ password + "','"
					+ email + "', 0, 0);" ); 
			
			stmt.close();
		} catch (SQLException e) {
			//System.out.println("Something wrong!!");
		}
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//SQLiteJDBC db = new SQLiteJDBC();
		
		//db.createTables();
		//db.populateDB();
		
		//db.getMessageByID(2);
		//db.getLastNMessage(3);
		//db.getUserByID(1);
		//db.checkLogin("user1", "XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=    ");
		//db.changePassword("user1", "XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=    ");
		//db.setTryByUser("user1", 0);
		//db.setUserLogged("user1", 0);
		//System.out.println(db.isUserLogged("user1"));
		
		//System.out.println(db.containsUser("user1"));
		
	}
}
