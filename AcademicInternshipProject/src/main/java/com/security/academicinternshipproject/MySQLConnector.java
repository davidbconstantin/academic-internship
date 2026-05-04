/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * based on https://aiven.io/docs/products/mysql/howto/connect-with-java
 * https://www.tutorialspoint.com/java_mysql/java_mysql_create_tables.htm
 */
package com.security.academicinternshipproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author rokom
 */

public class MySQLConnector {
    
    private String result;
    
    public MySQLConnector(String host, int port, String databaseName, String userName, String password) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (final Connection connection = 
            DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName +
                "?sslmode=require", userName, password);
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT version() AS version")) {
            while (resultSet.next()) {
                System.out.println("SQL Version: " + resultSet.getString("version"));
                result = "SQL Version: " + resultSet.getString("version");
                }        
            } catch (SQLException e) {
                System.out.println("Connection failure.");
                    e.printStackTrace();
            }
    }
    
    public void executeUpdate(String host, int port, String databaseName, String userName, String password, String userStatement) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (final Connection connection = 
            DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName +
                "?sslmode=require", userName, password);
            final Statement statement = connection.createStatement()) {
            statement.executeUpdate(userStatement); 
            } catch (SQLException e) {
                System.out.println("Connection failure.");
                    e.printStackTrace();
            }      
    }
    
    public String getResult() {
        return result;
    }
}
