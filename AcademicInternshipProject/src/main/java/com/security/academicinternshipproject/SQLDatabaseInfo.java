/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rokom
 */
public class SQLDatabaseInfo {
    private String name;
    private List<SQLTableInfo> tables;
    
    public SQLDatabaseInfo(String name, List<SQLTableInfo> tables) {
        this.name = name;
        this.tables = new ArrayList<>();
        this.tables.addAll(tables);
    }

    public String getName() {
        return name;
    }

    public List<SQLTableInfo> getTables() {
        return tables;
    }
    
    public void printInfo() {
        System.out.println("Database Name: " + name);
        for (SQLTableInfo table: tables) {
            table.printInfo();
        }     
    }
}
