/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import org.json.JSONArray;

/**
 *
 * @author kay schluehr
 */
public class T_execute_reply_err {
    public String ename = "";

    public String evalue = "";

    public JSONArray traceback = new JSONArray();
    
}
