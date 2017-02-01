/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scijava.jupyterkernel.util.JSONField;
import org.json.JSONArray;

/**
 *
 * @author kay schluehr
 */
public class T_kernel_info_reply extends T_JSON {

    @JSONField
    public String protocol_version;
    
    @JSONField
    public String implementation_version;

    @JSONField
    public String implementation;
    
    @JSONField(type = "T_JSON")
    public T_language_info language_info;
    
    @JSONField
    public String banner;

    @JSONField
    public JSONArray help_links;

    public T_kernel_info_reply() {
        this.protocol_version = T_JSON.message_protocol_version;  
    }
    
}
