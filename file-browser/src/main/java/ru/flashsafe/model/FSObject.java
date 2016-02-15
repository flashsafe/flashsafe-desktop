/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.model;

/**
 * File System Object
 * @author alex_xpert
 */
@Deprecated
public class FSObject {
    public int id;
    public String type;
    public String name;
    public String format;
    public long size;
    public boolean pincode;
    public int count;
    public long create_time;
    public long update_time;
    
    public FSObject() {}
    
    public FSObject(int _id, String _type, String _name, String _format, long _size, boolean _pincode, int _count,
            long _create_time, long _update_time) {
        id = _id;
        type = _type;
        name = _name;
        format = _format;
        size = _size;
        pincode = _pincode;
        count = _count;
        create_time = _create_time;
        update_time = _update_time;
    }
    
}
