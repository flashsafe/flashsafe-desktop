/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.http;

/**
 * Listener for upload file progress
 * @author alex_xpert
 */
public interface UploadProgressListener {
    
    public void onUpdateProgress(double percent);
    
}
