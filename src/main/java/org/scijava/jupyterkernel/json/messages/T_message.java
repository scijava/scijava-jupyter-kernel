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
public class T_message extends T_JSON {

    @JSONField(type = "T_JSON")
    public T_header header;

    @JSONField(type = "T_JSON")
    public T_header parent_header;

    @JSONField
    public JSONObject metadata = new JSONObject();

    @JSONField(type = "T_JSON")
    public T_JSON content;

}
