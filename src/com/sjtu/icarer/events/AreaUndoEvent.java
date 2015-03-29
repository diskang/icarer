package com.sjtu.icarer.events;


public class AreaUndoEvent {
    final public static int BUILDING_CHANGED = 2;
    final public static int FLOOR_CHANGED = 1;
    final public static int ROOM_CHANGED = 0;
    final public static int UNDO_ALL = 3;
    public int undoEvent;
    public AreaUndoEvent(final int flag) {
		undoEvent = flag;
	}
}
