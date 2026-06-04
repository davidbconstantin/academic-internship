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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rokom
 */

public class MySQLConnector {
    
    private String result;
    private DatabaseMetaData metadata;
    private String currentDb;
    private List<SQLDatabaseInfo> dbInfo;
    
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
    
    public void fetchTableSchemas(String url, String userName, String password) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (final Connection connection = 
            DriverManager.getConnection(url + "?sslmode=require", userName, password)) {
            metadata = connection.getMetaData();
            ResultSet results = metadata.getTables(null, null, "%", new String[]{"TABLE"});
            ResultSetMetaData rsmd = results.getMetaData();
            currentDb = connection.getCatalog();
            System.out.println("The current database is: " + currentDb);
            List<SQLDatabaseInfo> databaseInfo = new ArrayList<>();
            List<SQLTableInfo> tableInfo = new ArrayList<>();
            List<SQLColumnInfo> columnInfo = new ArrayList<>();
            String databaseName = "";
            String tableName = "";
            List<String> tables = new ArrayList<>();
            while (results.next()) {
                if (!databaseName.equals(results.getString("TABLE_CAT"))) {
                    if (!tables.isEmpty()) {
                        databaseInfo.add(new SQLDatabaseInfo(databaseName, tableInfo));
                        tables.clear();
                        tableInfo.clear();
                    }
                    databaseName = results.getString("TABLE_CAT");
                }
                if (!tables.contains(results.getString("TABLE_NAME"))) {
                    tables.add(results.getString("TABLE_NAME"));
                    ResultSet results2 = metadata.getColumns(databaseName, null, results.getString("TABLE_NAME"), null);
                    String columnName = "";
                    while (results2.next()) {
                        columnName = results2.getString("COLUMN_NAME");
                        int dataType = Integer.parseInt(results2.getString("DATA_TYPE"));
                        String typeName = results2.getString("TYPE_NAME");
                        int columnSize = Integer.parseInt(results2.getString("COLUMN_SIZE"));
                        String remarks = results2.getString("REMARKS");
                        String columnDef = results2.getString("COLUMN_DEF");
                        int ordinalPos = Integer.parseInt(results2.getString("ORDINAL_POSITION"));
                        String isAutoincrement = results2.getString("IS_AUTOINCREMENT");
                        String isGeneratedColumn = results2.getString("IS_GENERATEDCOLUMN");
                        SQLColumnInfo columns = new SQLColumnInfo(columnName, dataType, typeName, columnSize, remarks, columnDef, ordinalPos, isAutoincrement, isGeneratedColumn);
                        columnInfo.add(new SQLColumnInfo(columnName, dataType, typeName, columnSize, remarks, columnDef, ordinalPos, isAutoincrement, isGeneratedColumn));
                    }   
                    tableInfo.add(new SQLTableInfo(results.getString("TABLE_CAT"), results.getString("TABLE_NAME"), columnInfo));
                    columnInfo.clear();
                }
            }
            dbInfo = databaseInfo;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public void fetchTableSchemas(String host, int port, String dbName, String userName, String password) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (final Connection connection = 
            DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?sslmode=require", userName, password)) {
            metadata = connection.getMetaData();
            ResultSet results = metadata.getTables(null, null, "%", new String[]{"TABLE"});
            ResultSetMetaData rsmd = results.getMetaData();
            currentDb = connection.getCatalog();
            System.out.println("The current database is: " + currentDb);
            List<SQLDatabaseInfo> databaseInfo = new ArrayList<>();
            List<SQLTableInfo> tableInfo = new ArrayList<>();
            List<SQLColumnInfo> columnInfo = new ArrayList<>();
            String databaseName = "";
            String tableName = "";
            List<String> tables = new ArrayList<>();
            while (results.next()) {
                if (!databaseName.equals(results.getString("TABLE_CAT"))) {
                    if (!tables.isEmpty()) {
                        databaseInfo.add(new SQLDatabaseInfo(databaseName, tableInfo));
                        tables.clear();
                        tableInfo.clear();
                    }
                    databaseName = results.getString("TABLE_CAT");
                }
                if (!tables.contains(results.getString("TABLE_NAME"))) {
                    tables.add(results.getString("TABLE_NAME"));
                    ResultSet results2 = metadata.getColumns(databaseName, null, results.getString("TABLE_NAME"), null);
                    String columnName = "";
                    while (results2.next()) {
                        columnName = results2.getString("COLUMN_NAME");
                        int dataType = Integer.parseInt(results2.getString("DATA_TYPE"));
                        String typeName = results2.getString("TYPE_NAME");
                        int columnSize = Integer.parseInt(results2.getString("COLUMN_SIZE"));
                        String remarks = results2.getString("REMARKS");
                        String columnDef = results2.getString("COLUMN_DEF");
                        int ordinalPos = Integer.parseInt(results2.getString("ORDINAL_POSITION"));
                        String isAutoincrement = results2.getString("IS_AUTOINCREMENT");
                        String isGeneratedColumn = results2.getString("IS_GENERATEDCOLUMN");
                        SQLColumnInfo columns = new SQLColumnInfo(columnName, dataType, typeName, columnSize, remarks, columnDef, ordinalPos, isAutoincrement, isGeneratedColumn);
                        columnInfo.add(new SQLColumnInfo(columnName, dataType, typeName, columnSize, remarks, columnDef, ordinalPos, isAutoincrement, isGeneratedColumn));
                    }   
                    tableInfo.add(new SQLTableInfo(results.getString("TABLE_CAT"), results.getString("TABLE_NAME"), columnInfo));
                    columnInfo.clear();
                }
            }
            dbInfo = databaseInfo;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public String getCurrentDb() {
        return currentDb;
    }
    
    public String getResult() {
        return result;
    }
    
    public List<SQLDatabaseInfo> getDbInfo() {
        return dbInfo;
    }
    
    public SQLDatabaseInfo getCurrentDbAsInfo() {
        for (SQLDatabaseInfo db: dbInfo) {
            if (db.getName().equals(currentDb)) {
                return db;
            }
        }
        return null;
    }
}
