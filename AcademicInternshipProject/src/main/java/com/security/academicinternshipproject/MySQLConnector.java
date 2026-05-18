/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * based on https://aiven.io/docs/products/mysql/howto/connect-with-java
 * https://www.tutorialspoint.com/java_mysql/java_mysql_create_tables.htm
 * https://www.baeldung.com/jdbc-database-metadata
 */
package com.security.academicinternshipproject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
    private DatabaseMetaData metadata;
    
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
    
    public MySQLConnector(String host, int port, String databaseName, String userName, String password, String userStatement) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (final Connection connection = 
            DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName +
                "?sslmode=require", userName, password);
            final Statement statement = connection.createStatement()) {
            final int resultCount = statement.executeUpdate(userStatement); 
            System.out.println("Rows affected: " + resultCount);
            } catch (SQLException e) {
                System.out.println("Connection failure.");
                    e.printStackTrace();
            }      
    }
    
    public MySQLConnector(String url, String userName, String password, String userStatement) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    try (final Connection connection = 
        DriverManager.getConnection(url + "?sslmode=require", userName, password);
        final Statement statement = connection.createStatement()) {
        final int resultCount = statement.executeUpdate(userStatement); 
        System.out.println("Rows affected: " + resultCount);
        } catch (SQLException e) {
            System.out.println("Connection failure.");
                e.printStackTrace();
                throw new SQLException("Hi", e);
        }      
    }
    
    public MySQLConnector(String url, String userName, String password) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    try (final Connection connection = 
        DriverManager.getConnection(url + "?sslmode=require", userName, password)) {
        } catch (SQLException e) {
            System.out.println("Connection failure.");
                e.printStackTrace();
                throw new SQLException("Hi", e);
        }      
    }
    
    public int getColumnSize(String url, String userName, String password, String tableName, String columnName) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (final Connection connection = 
        DriverManager.getConnection(url + "?sslmode=require", userName, password)) {  
            metadata = connection.getMetaData();
            ResultSet rSet = metadata.getColumns(null, null, tableName, null);
            while (rSet.next()) {
                if (rSet.getString("COLUMN_NAME").equals(columnName))
                    return rSet.getInt("COLUMN_SIZE");
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return -1;
    } 
    
    public String getResult() {
        return result;
    }
}
