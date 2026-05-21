/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.security.academicinternshipproject.MySQLConnector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author rokom
 */
public class SQLUpdateTest {
    
    public SQLUpdateTest() {
    }
    
    @Test
    public void testBadStatement() {
        Exception exception = assertThrows(SQLException.class, () -> {
            List<String> credentials = new ArrayList<String>();
            credentials.add(System.getenv("TEST_MYSQL_URL"));
            credentials.add(System.getenv("TEST_MYSQL_USER"));
            credentials.add(System.getenv("TEST_MYSQL_PASSWORD"));
            String sqlStatement = "INSERT INTO testtable (text) VALUES ('AAAAAAA"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')";
                MySQLConnector mysql = new MySQLConnector(credentials.get(0), credentials.get(1), credentials.get(2), sqlStatement);                          
        }, "Fail");
    }
    
    @Test
    public void testStatement() {
        List<String> credentials = new ArrayList<String>();
        credentials.add(System.getenv("TEST_MYSQL_URL"));
        credentials.add(System.getenv("TEST_MYSQL_USER"));
        credentials.add(System.getenv("TEST_MYSQL_PASSWORD"));
        String sqlStatement = "INSERT INTO testtable (text) VALUES ('AAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')";
            try {
                MySQLConnector mysql = new MySQLConnector(credentials.get(0), credentials.get(1), credentials.get(2)); 
                int columnSize = mysql.getColumnSize("Column size: " + credentials.get(0), credentials.get(1), credentials.get(2), "testtable", "text");
                String value = sqlStatement;
                Pattern pattern = Pattern.compile("'([^']*)'");
                Matcher matcher = pattern.matcher(value);
                while (matcher.find()) {
                    System.out.println("Size of value to insert: " + matcher.group().length());
                }
                mysql.fetchTableSchemas(credentials.get(0), credentials.get(1), credentials.get(2));
            } catch (ClassNotFoundException | SQLException ex) {
                if (ex instanceof SQLException)
                    System.out.println(ex);
            }
    }
}

