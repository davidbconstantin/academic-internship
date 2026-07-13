/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * https://www.tutorialspoint.com/article/java-databasemetadata-getcolumns-method-with-example
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableDemoProject/src/components/TableDemo.java
 */
package com.security.academicinternshipproject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rokom
 */
public class SQLTableInfo {
    private String databaseName;
    private String tableName;
    private List<SQLColumnInfo> columns;
    
    public SQLTableInfo(String databaseName, String tableName, List<SQLColumnInfo> columns) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columns = new ArrayList<>();
        this.columns.addAll(columns);
    }
    
    public String getTableName() {
        return tableName;
    }

    public List<SQLColumnInfo> getColumns() {
        return columns;
    }
    
    public void printInfo() {
        System.out.println("Database Name: " + databaseName);
        System.out.println("Table Name: " + tableName);
        for (int i = 0; i < columns.size(); i ++) {
            columns.get(i).printInfo();
        }
    }   
}
