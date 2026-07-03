package com.pulseisland.app;

/**
 * 灵动岛全局状态，由 PulseSchedulingAPI 统一管理
 */
public class IslandState {
    
    public boolean isActive = false;
    
    public int islandX = 0;
    public int islandY = 100;
    public int islandWidth = 200;
    public int islandHeight = 80;
    
    public String currentEffectName = null;
    public String displayState = "IDLE";
    
    public static final String STATE_IDLE = "IDLE";
    public static final String STATE_EXPANDED = "EXPANDED";
    public static final String STATE_COMPACT = "COMPACT";
    public static final String STATE_HIDDEN = "HIDDEN";
}
