/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * https://learn.microsoft.com/en-us/sql/connect/jdbc/reference/gettables-method-sqlserverdatabasemetadata?view=sql-server-ver17
 */
package com.security.academicinternshipproject;

import java.sql.DatabaseMetaData; 
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author rokom
 */
public class SQLParser {
    private SQLDatabaseInfo databaseInfo;
    private List<String> result;
    
    public SQLParser(String sqlStatement, SQLDatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        result = new ArrayList<>();
        // parse INSERT INTO table_name (column1, column2, column3, ...) VALUES (value1, value2, value3, ...); 
        StringTokenizer tokenizer = new StringTokenizer(sqlStatement);
    }
}
