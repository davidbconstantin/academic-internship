/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.security.academicinternshipproject.MySQLConnector;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author rokom
 */
public class SQLSelectTest {
    
    public SQLSelectTest() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void select() {
        List<String> credentials = new ArrayList<String>();
        credentials.add(System.getenv("TEST_MYSQL_URL"));
        credentials.add(System.getenv("TEST_MYSQL_USER"));
        credentials.add(System.getenv("TEST_MYSQL_PASSWORD"));
        try {
            MySQLConnector mysql = new MySQLConnector(credentials.get(0), credentials.get(1), credentials.get(2));
            CachedRowSet results = mysql.executeQuery(credentials.get(0), credentials.get(1), credentials.get(2), "SELECT * FROM testtable");
            ResultSetMetaData meta = results.getMetaData();
            System.out.println("Table Name: " + meta.getTableName(1));
            while (results.next()) {
                System.out.println("ID: " + results.getInt("id") + " Text: " + results.getString("text"));
            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }
}
