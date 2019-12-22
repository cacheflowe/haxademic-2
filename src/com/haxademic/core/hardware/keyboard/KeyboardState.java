package com.haxademic.core.hardware.keyboard;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.shared.InputState;

import processing.event.KeyEvent;

public class KeyboardState {

	protected HashMap<Integer, InputState> keyboardButtons = new HashMap<Integer, InputState>();
	protected int lastUpdatedFrame = 0;

	// Singleton instance
	
	public static KeyboardState instance;
	
	public static KeyboardState instance() {
		if(instance != null) return instance;
		instance = new KeyboardState();
		return instance;
	}
	
	// Constructor

	public KeyboardState() {
		P.p.registerMethod(PRegisterableMethods.post, this);
		P.p.registerMethod(PRegisterableMethods.keyEvent, this);
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
		
	public void post() {
		logValues();
		if(P.p.frameCount == lastUpdatedFrame) return; 
		for (Integer key : keyboardButtons.keySet()) {
			if(keyboardButtons.get(key) == InputState.TRIGGER) keyboardButtons.put(key, InputState.ON);
		}
	}
	
	///////////////////////////////
	// PUBLIC GETTERS
	///////////////////////////////
	
	public boolean isKeyTriggered(int keyCode) {
		return (keyboardButtons.containsKey(keyCode) && keyboardButtons.get(keyCode) == InputState.TRIGGER);
	}
	
	public boolean isKeyOn(int keyCode) {
		return (keyboardButtons.containsKey(keyCode) && (keyboardButtons.get(keyCode) == InputState.TRIGGER || keyboardButtons.get(keyCode) == InputState.ON));
	}
	
	
	///////////////////////////////
	// KEYBOARD LISTENER
	///////////////////////////////

	public void keyEvent(KeyEvent e) {
		if(e.getAction() == KeyEvent.PRESS) {
			setKeyOn(e.getKeyCode());
		}
		if(e.getAction() == KeyEvent.RELEASE) {
			setKeyOff(e.getKeyCode());
		}
	}
	
	public void setKeyOn(int keyCode) {
		keyboardButtons.put(keyCode, InputState.TRIGGER);
		lastUpdatedFrame = P.p.frameCount;
	}

	public void setKeyOff(int keyCode) {
		keyboardButtons.put(keyCode, InputState.OFF);
		lastUpdatedFrame = P.p.frameCount;
	}
	
	///////////////////////////////
	// DEBUG
	///////////////////////////////
	
	protected void logValues() {
		// debug print values if debug window is showing
		if(DebugView.active()) {
			for (Integer key : keyboardButtons.keySet()) {
				DebugView.setValue("Keyboard ["+key+"]", keyboardButtons.get(key).name());
			}
		}
	}

}
