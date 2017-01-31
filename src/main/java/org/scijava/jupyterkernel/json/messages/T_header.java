/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import org.scijava.jupyterkernel.util.JSONField;

/**
 *
 * @author kay schluehr
 */
public class T_header extends T_JSON {
    @JSONField
    public String msg_id;
    
    @JSONField
    public String username;

    @JSONField
    public String session;

    @JSONField
    public String date;    
    
    @JSONField
    public String msg_type;    
    
    @JSONField
    public String version;    

    public T_header() {
        this.version = T_JSON.message_protocol_version;
    }
    
}
