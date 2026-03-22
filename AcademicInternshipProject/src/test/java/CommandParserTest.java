/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.security.academicinternshipproject.CommandParser;
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
public class CommandParserTest {
    
    private CommandParser test;
    
    public CommandParserTest() {
        String testCommand = "Command #3 Search a[href]";
        test = new CommandParser(testCommand);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testSubjectNo() {
        assertEquals(3, test.getSubjectNo());
        System.out.println(test.getSubjectNo());
    }
    
    @Test
    public void testSubject() {
        assertEquals("Command #3", test.getSubject());
        System.out.println(test.getSubject());
    }
    
    @Test
    public void testAction() {
        assertEquals("Search", test.getAction());
        System.out.println(test.getAction());
    }
    
    @Test
    public void testObject() {
        assertEquals("a[href]", test.getObject());
        System.out.println(test.getObject());
    }
}
