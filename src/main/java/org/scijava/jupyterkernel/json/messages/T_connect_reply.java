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
public class T_connect_reply extends T_JSON {
    @JSONField
    public Integer shell_port;
    
    @JSONField
    public Integer iopub_port;

    @JSONField
    public Integer stdin_port;
    
    @JSONField
    public Integer hb_port;    
}
