import java.sql.*;


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
	                   "(ID 			INT		NOT NULL PRIMARY KEY," +
	                   " USERNAME		TEXT    NOT NULL, " + 
	                   " PASSWORD		INT     NOT NULL, " + 
	                   " LOG 			INT 	NOT NULL)";
	      stmt.executeUpdate(sql);
	      
	      sql = "CREATE TABLE MESSAGES " +
                  "(ID 			INT		NOT NULL PRIMARY KEY," +
                  " ID_USER		INT		NOT NULL, " + 
                  " MESSAGE		TEXT     NOT NULL)";
	      
	      stmt.executeUpdate(sql);
	      stmt.close();
	      this.conn.close();
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
			String sql = "INSERT INTO USERS (ID,USERNAME,PASSWORD,LOG) VALUES (1, 'user1', 'password', 0);"; 
			stmt.executeUpdate(sql);

			sql = "INSERT INTO USERS (ID, USERNAME, PASSWORD, LOG) VALUES (2, 'user2', 'password', 0);"; 
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				
				System.out.println( "MESSAGE = " + msg );
				System.out.println();
		    }
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	
	
	
	public static void main(String[] args) {
		SQLiteJDBC db = new SQLiteJDBC();
		
		//db.createTables();
		//db.populateDB();
		
		//db.getMessageByID(2);
		//db.getLastNMessage(3);
	}
}
