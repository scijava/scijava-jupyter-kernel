/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import org.json.JSONObject;
import org.json.JSONArray;
import org.scijava.jupyterkernel.util.JSONField;


/**
 *
 * @author kay schluehr
 */
public class T_execute_reply extends T_JSON {
    @JSONField
    public String status;
    
    @JSONField
    public Integer execution_count;

    @JSONField(type = "Choice")
    public JSONObject user_expression;

    @JSONField(type = "Choice")
    public String ename;

    @JSONField(type = "Choice")
    public String evalue;

    @JSONField(type = "Choice")
    public JSONArray traceback;
    
    public void setAnswer(T_execute_reply_ok reply)
    {
        this.user_expression = reply.user_expressions;
    }

    public void setAnswer(T_execute_reply_err reply)
    {
        this.ename  = reply.ename;
        this.evalue = reply.evalue;
        this.traceback = reply.traceback;
    }    
}
