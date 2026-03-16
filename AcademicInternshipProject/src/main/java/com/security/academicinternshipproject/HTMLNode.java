/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

/**
 *
 * @author rokom
 */
public class HTMLNode {
    private String element;
    private String elementClass;
    private String attribute;
    
    public HTMLNode(String element, String elementClass, String attribute) {
        this.element = element;
        this.elementClass = elementClass;
        this.attribute = attribute;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getElementClass() {
        return elementClass;
    }

    public void setElementClass(String elementClass) {
        this.elementClass = elementClass;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    
}
