/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import org.json.JSONObject;
import org.scijava.jupyterkernel.util.JSONField;

/**
 *
 * @author kay schluehr
 */
public class T_comm_open extends T_JSON {
    @JSONField
    public String comm_id;
    
    @JSONField
    public String target_name;
    
    @JSONField
    public JSONObject data;
    
    @JSONField
    public String my_module;    
}

