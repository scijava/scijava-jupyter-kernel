/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scijava.jupyterkernel.json.messages;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scijava.jupyterkernel.util.JSONField;

/**
 *
 * @author kay schluehr
 */
public class T_JSON {
    public static String message_protocol_version = null;
    
    private static double protocol_version = 5.0;
    
    public static void setProtocolVersion(String protocolVersion)
    {
        message_protocol_version = protocolVersion;
        protocol_version = Double.parseDouble(protocolVersion);
        
    }
    

    public static T_JSON fromJSON(String classname, JSONObject jsonObj) {

        T_JSON instance;
        Object value;
        try {
            Class cls = Class.forName("org.scijava.jupyterkernel.json.messages." + classname);
            try {
                instance = (T_JSON) cls.newInstance();
                if (jsonObj.length() == 0) {
                    return instance;
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(T_JSON.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(T_JSON.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Field[] fields = instance.getClass().getFields();
        for (Field field : fields) {
            JSONField jsonField = field.getAnnotation(JSONField.class);
            if (jsonField != null) {
                String name = field.getName();                
                try {
                    value = jsonObj.get(name);
                } catch (JSONException e) {
                    continue;
                }
                if (jsonField.type().equals("T_JSON")) {
                    if (name.equals("content")) {
                        name = (String) jsonObj.getJSONObject("header").get("msg_type");
                    }
                    value = fromJSON("T_" + name, (JSONObject) value);
                }
                try {
                    field.set(instance, value);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(T_JSON.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return instance;
    }
    
    private boolean checkVersion(String versionString)
    {
        int n = versionString.length()-1;
        char qual = versionString.charAt(n);
        double version = Double.parseDouble(versionString.substring(0, n));
        switch(qual)
        {
            case '+':
            {
                return (protocol_version>=version);
            }
            case '-':
            {
                return (protocol_version<version);
            }
            default:
            {
                return true;
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject();
        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            JSONField jsonField = field.getAnnotation(JSONField.class);
            if (jsonField != null) {
                Object value;
                String name = field.getName();
                String fieldType = jsonField.type();
                String version = jsonField.version();
                if(!checkVersion(version))
                {
                    // skip the value which is not part of the JSON message
                    continue;
                }
                try {
                    value = field.get(this);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(T_message.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
                switch (fieldType) {
                    case "Simple": {
                        if (value == null) {
                            Class cls = field.getType();
                            switch (cls.getName()) {
                                case "JSONObject":
                                    value = new JSONObject();
                                    break;

                                case "JSONArray":
                                    value = new JSONArray();
                                    break;

                                case "String":
                                    value = "";
                                    break;

                                case "Integer":
                                    value = 0;
                                    break;
                            }
                        }
                        jsonObj.put(name, value);
                        break;
                    }

                    case "T_JSON": {
                        jsonObj.put(name, ((T_JSON) value).toJSON());
                        break;
                    }

                    case "Choice":
                        // don't put default value into field
                        if (value != null) {
                            jsonObj.put(name, value);
                        }
                        break;
                }
            }
        }
        return jsonObj;
    }

    private static Object cloneObject(Object obj) {
        try {
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(obj) == null || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                if (field.getType().isPrimitive() || field.getType().equals(String.class)
                        || field.getType().getSuperclass().equals(Number.class)
                        || field.getType().equals(Boolean.class)) {
                    field.set(clone, field.get(obj));
                } else {
                    Object childObj = field.get(obj);
                    if (childObj == obj) {
                        field.set(clone, clone);
                    } else {
                        field.set(clone, cloneObject(field.get(obj)));
                    }
                }
            }
            return clone;
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException e) {
            return null;
        }
    }

    public T_JSON clone() {
        return (T_JSON) cloneObject(this);
    }
}
