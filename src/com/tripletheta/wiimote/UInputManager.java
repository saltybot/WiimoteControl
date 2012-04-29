package com.tripletheta.wiimote;

public class UInputManager {
    
//    private boolean mIsInitialized = false;
    
    public UInputManager() {
        this(true);
    }
    
    public UInputManager(boolean initialize) {  
        System.loadLibrary("UInputManager");
        if (initialize) {
            init();
        }
    }
    
    public void zoomIn() {
        
    }
    
    public void zoomOut() {
        
    }
    
    public void click() {
        
    }
    
    public void clickHold() {
        
    }
    
    public void clickRelease() {
        
    }
    
    public void back() {
        
    }
    
    public native int init();
    public native void destroy();
    public native void movePointerAbsolute(int x, int y);
    public native void movePointerRelative(int x, int y);
}
