/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * https://learn.microsoft.com/en-us/sql/connect/jdbc/reference/gettables-method-sqlserverdatabasemetadata?view=sql-server-ver17
 * https://www.w3schools.com/java/ref_string_split.asp/ */
package com.security.academicinternshipproject;

import java.sql.DatabaseMetaData; 
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rokom
 */
public class SQLParser {
    private SQLDatabaseInfo databaseInfo;
    private List<String> stringsToInsert = new ArrayList<>();
    private String valueToInsert;
    private boolean mustSplit;
    
    public SQLParser(String sqlStatement, SQLDatabaseInfo databaseInfo) {
        mustSplit = false;
        int valueLength = 0;
        this.databaseInfo = databaseInfo;
        // parse INSERT INTO table_name (column1, column2, column3, ...) VALUES (value1, value2, value3, ...); 
        StringTokenizer tokeniser = new StringTokenizer(sqlStatement);
        String token;
        List<String> tokenCollection = new ArrayList<>();
        String tableName = "";
        boolean afterValuesToken = false;
        List<String> columnNames = new ArrayList<>();
        while (tokeniser.hasMoreTokens()) {
            token = tokeniser.nextToken();
            //System.out.println("Token: " + token);
            tokenCollection.add(token);
            if (tokenCollection.size() > 2) {
                for (int i = 0; i < tokenCollection.size(); i++) {
                    // extract the table name
                    if (tokenCollection.get(i).equals("INSERT") && i + 1 < tokenCollection.size())
                        if (tokenCollection.get(i + 1).equals("INTO") && tableName.equals("")) {
                            tableName = tokenCollection.get(i + 2);
                            System.out.println("Table name: " + tableName);
                        }
                }
            }
            if (token.equals("VALUES"))
                afterValuesToken = true;
            // extract the column name(s)
            if (!tableName.equals("") && !token.equals("VALUES")
                    && !token.equals(tableName) && !afterValuesToken) {
                String columnName = token;
                columnName = columnName.replace('(', ' ');
                columnName = columnName.replace(')', ' ');
                columnName = columnName.replace(',', ' ');
                columnName = columnName.trim();
                System.out.println("Column name: " + columnName);
                columnNames.add(columnName);
            }
        }
        Pattern pattern = Pattern.compile("'([^']*)'");
        Matcher matcher = pattern.matcher(sqlStatement);
        while (matcher.find()) {
            System.out.println("Value to insert: " + matcher.group());
            valueToInsert = matcher.group();
            System.out.println("Size of value to insert: " + matcher.group().length());
            valueLength = matcher.group().length();
        }
        System.out.println("Printing table info...");
        // print table info
        for (SQLTableInfo table: databaseInfo.getTables()) {
            if (table.getTableName().equals(tableName)) {
                //table.printInfo();
                for (String column: columnNames) {
                    for (SQLColumnInfo colInfo: table.getColumns()) {
                        if (colInfo.getColumnName().equals(column)) {
                            colInfo.printInfo();
                            int colSize = colInfo.getColumnSize();
                                if (colSize < valueLength && colInfo.getTypeName().equals("VARCHAR")) {
                                    mustSplit = true;
                                    System.out.println("Column size is too small for string.");
                                    int position = 0;
                                    String substring = "";
                                    while (position < valueToInsert.length()) {
                                        if (colSize + position > valueLength) {
                                            substring = valueToInsert.substring(position, valueLength);
                                            substring = "'" + substring;
                                        }
                                        else {
                                            substring = valueToInsert.substring(position, colSize + position);
                                            substring = substring + "'";
                                            
                                        }
                                        // compose whole statement
                                        String newSqlStatement = sqlStatement.replace(valueToInsert, substring);
                                        stringsToInsert.add(newSqlStatement);
                                        position += colSize;
                                    }
                                    // list off substrings
                                    for (String sub: stringsToInsert) {
                                        System.out.println("Substring: " + sub);
                                    }
                                }
                        }
                    }
                }
            }   
        }
    }
    
    public List<String> getStringsToInsert() {
        return stringsToInsert;
    }
    
    public boolean getMustSplit() {
        System.out.println("Splitting SQL statement into multiple statements...");
        return mustSplit;
    }
}
