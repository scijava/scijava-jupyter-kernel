/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import org.scijava.jupyterkernel.util.JSONField;
import org.json.JSONObject;
/**
 *
 * @author kay schluehr
 */
public class T_execute_result extends T_JSON {
    @JSONField
    public Integer execution_count;

    @JSONField
    public JSONObject data = new JSONObject();

    @JSONField
    public JSONObject metadata = new JSONObject();
    
}
