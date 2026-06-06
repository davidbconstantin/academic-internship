/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.security.academicinternshipproject.CommandParser;
import com.security.academicinternshipproject.MySQLConnector;
import java.util.List;
import java.util.Scanner;
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
public class SQLCommandTest {
    
    public SQLCommandTest() {
        Scanner input = new Scanner(System.in);
            String sqlStatement = "INSERT INTO (id, result) VALUES (1, @2s);";
            System.out.println("SQL Statement: " + sqlStatement);
            // parse contents of the SQL command
            // example command: @2i or @2is
            // s stands for String, i means Integer (numeric value)7
            boolean atSignPresent = false;
            boolean numericCharacterPresent = false;
            boolean alphabeticLetterPresent = false;
            boolean isNumeric = false;
            for (int i = 0; i < sqlStatement.length(); i++) {
                // sub in HTML responses 
                if (sqlStatement.charAt(i) == '@') {
                    atSignPresent = true;
                    System.out.println("At sign present.");
                }
                if (i + 1 < sqlStatement.length()) {
                    if (sqlStatement.charAt(i + 1) == '1' || sqlStatement.charAt(i + 1) == '2' ||
                        sqlStatement.charAt(i + 1) == '3' || sqlStatement.charAt(i + 1) == '4' ||
                        sqlStatement.charAt(i + 1) == '5' || sqlStatement.charAt(i + 1) == '6' ||
                        sqlStatement.charAt(i + 1) == '7' || sqlStatement.charAt(i + 1) == '8' ||
                        sqlStatement.charAt(i + 1) == '9') {
                    numericCharacterPresent = true;
                    System.out.println("Numeric character present.");
                    }
                }
                if (i + 2 < sqlStatement.length()) {
                    if (sqlStatement.charAt(i + 2) == 'i' || sqlStatement.charAt(i + 2) == 's' ||
                            sqlStatement.charAt(i + 2) == 'I' || sqlStatement.charAt(i + 2) == 'S') {
                        alphabeticLetterPresent = true;
                        System.out.println("Alphabetic letter present.");
                    }
                }
                if (atSignPresent && numericCharacterPresent && alphabeticLetterPresent) {
                    String valueToInsert = "Example";
                    sqlStatement = sqlStatement.replaceFirst("@\\d[iIsS]", valueToInsert);
                    atSignPresent = numericCharacterPresent = isNumeric = false;
                } else
                    atSignPresent = numericCharacterPresent = isNumeric = false;
            }
                System.out.println("Converted SQL Statement: " + sqlStatement);
        }
            
    @Test
    public void test() {
        CommandParser parser = new CommandParser("SQL INSERT INTO testtable (id, name) VALUES (0, @6s);");
        System.out.println("Object: " + parser.getObject());
        SQLCommandTest test = new SQLCommandTest();
    }
}
