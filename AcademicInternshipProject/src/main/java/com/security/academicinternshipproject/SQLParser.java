/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * https://learn.microsoft.com/en-us/sql/connect/jdbc/reference/gettables-method-sqlserverdatabasemetadata?view=sql-server-ver17
 */
package com.security.academicinternshipproject;

import java.sql.DatabaseMetaData; 
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author rokom
 */
public class SQLParser {
    private List<String> columnInfo;
    private String currentDb;
    
    public SQLParser(String sqlStatement, DatabaseMetaData dbMetadata) {

    }
}
