/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

import java.util.StringTokenizer;

/**
 *
 * @author rokom
 * Streamlines execution of commands
 */
public class CommandParser {
    private String action;
    private String object;
    private String subject;
    private int subjectNo;
    private StringTokenizer tokenizer;
    
    public CommandParser(String command) {
        action = "";
        object = "";
        subjectNo = -1;
        tokenizer = new StringTokenizer(command);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("Command"))
                subject = token;
            else if (token.startsWith("#")) {
                subject += " " + token;
                subjectNo = Integer.parseInt(token.substring(1));
            }
            else if (action.equals(""))
                action = token;
            else if (object.equals(""))
                object = token;
        }
    }

    public String getAction() {
        return action;
    }

    public String getObject() {
        return object;
    }

    public String getSubject() {
        return subject;
    }
    
    public int getSubjectNo() {
        return subjectNo;
    }
    
}
