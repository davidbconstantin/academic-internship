/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

/**
 *
 * @author rokom
 */
public class SQLColumnInfo {
    private String columnName;
    private int dataType;
    private String typeName;
    private int columnSize;
    private String remarks;
    private String columnDef;
    private int ordinalPosition;
    private String isAutoincrement;
    private String isGeneratedColumn;

    public SQLColumnInfo(String columnName, int dataType, String typeName, int columnSize, String remarks, String columnDef, int ordinalPosition, String isAutoincrement, String isGeneratedColumn) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.typeName = typeName;
        this.columnSize = columnSize;
        this.remarks = remarks;
        this.columnDef = columnDef;
        this.ordinalPosition = ordinalPosition;
        this.isAutoincrement = isAutoincrement;
        this.isGeneratedColumn = isGeneratedColumn;
    }

    public void printInfo() {
        System.out.println("Column Name: " + columnName);
        System.out.println("Data Type: " + dataType);
        System.out.println("Type Name: " + typeName);
        System.out.println("Column Size: " + columnSize);
        System.out.println("Remarks: " + remarks);
        System.out.println("Column Definition: " + columnDef);
        System.out.println("Ordinal Position: " + ordinalPosition);
        System.out.println("Is Autoincrement: " + isAutoincrement);
        System.out.println("Is Generated Column: " + isGeneratedColumn);
    }
    
}
