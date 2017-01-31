/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scijava.jupyterkernel.util.JSONField;

/**
 *
 * @author kay schluehr
 */
public class T_complete_reply extends T_JSON {
    
    @JSONField
    public String status = "ok";

    @JSONField
    public JSONArray matches = new JSONArray();    

    @JSONField
    public Integer cursor_start;
    
    @JSONField
    public Integer cursor_end;

    @JSONField
    public JSONObject metadata = new JSONObject();    
    
}
