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
public class T_execute_request extends T_JSON {

    @JSONField
    public String code;

    @JSONField
    public Boolean silent = false;

    @JSONField
    public Boolean store_history;

    @JSONField
    public Boolean allow_stdin = false;

    @JSONField
    public Boolean stop_on_error = false;

    @JSONField
    public JSONObject user_expressions;    
}
