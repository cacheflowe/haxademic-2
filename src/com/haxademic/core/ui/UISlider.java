package com.haxademic.core.ui;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.PrefToText;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class UISlider
implements IUIControl {
	
	protected String id;
	protected float value;
	protected EasingFloat valueEased;
	protected float valueMin;
	protected float valueMax;
	protected float dragStep;
	protected int x;
	protected int y;
	protected int w;
	protected int h;
	protected float layoutW;
	protected int activeTime = 0;
	protected Point mousePoint = new Point();
	protected Rectangle uiRect = new Rectangle();
	protected boolean mouseHovered = false;
	protected boolean mousePressed = false;
	protected boolean saves = false;

	public UISlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h) {
		this(property, value, low, high, dragStep, x, y, w, h, true);
	}
	
	public UISlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h, boolean saves) {
		this.id = property;
		this.value = (saves) ? PrefToText.getValueF(property, value) : value;
		this.valueMin = low;
		this.valueMax = high;
		this.dragStep = dragStep;
		this.layoutW = 1;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.saves = saves;
		valueEased = new EasingFloat(this.value, 0.1f);
		P.p.registerMethod("mouseEvent", this);
		P.p.registerMethod("keyEvent", this);
	}
	
	/////////////////////////////////////////
	// Disable/enable
	/////////////////////////////////////////
	
	public boolean isActive() {
		return (P.p.millis() - activeTime) < 10; // when drawing, time is tracked. if not drawing, time will be out-of-date
	}
	
	/////////////////////////////////////////
	// IUIControl interface
	/////////////////////////////////////////
	
	public String type() {
		return IUIControl.TYPE_SLIDER;
	}
	
	public String id() {
		return id;
	}
	
	public float value() {
		return value;
	}
	
	public float valueEased() {
		return valueEased.value();
	}
	
	public float valueMin() {
		return valueMin;
	}
	
	public float valueMax() {
		return valueMax;
	}
	
	public float step() {
		return dragStep;
	}
	
	public float toggles() {
		return 0;
	}
	
	public float layoutW() {
		return layoutW;
	}
	
	public void layoutW(float val) {
		layoutW = val;
	}
	
	public void set(float val) {
		value = val;
	}
	
	public void update(PGraphics pg) {
		valueEased.setTarget(value);
		valueEased.update(true);
		
		PG.setDrawCorner(pg);
		
		// outline
		pg.noStroke();
		pg.fill(ColorsHax.BUTTON_OUTLINE);
		pg.rect(x-1, y-1, w+2, h+2);
		
		// background
		if(mouseHovered) pg.fill(ColorsHax.BUTTON_BG_HOVER);
		else pg.fill(ColorsHax.BUTTON_BG);
		pg.rect(x, y, w, h);
		
		// text label
		PFont font = FontCacher.getFont(DemoAssets.fontInterPath, 11);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(id + ": " + MathUtil.roundToPrecision(value, 5), P.round(x + TEXT_INDENT), P.round(y - 2) + 7.1f, w, h);
		uiRect.setBounds(x, y, w, h);
		
		// draw current value
		pg.noStroke();
		if(mousePressed) pg.fill(ColorsHax.WHITE, 180);
		else pg.fill(ColorsHax.WHITE, 90);
		float handleW = 20;
		float mappedX = P.map(value, valueMin, valueMax, x, x + w - handleW);
		pg.rect(mappedX - 0.5f, y, handleW, h);
		
		// set active if drawing
		activeTime = P.p.millis();
	}
	
	/////////////////////////////////////////
	// Mouse events
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		if(isActive() == false) return;
		// collision detection
		mousePoint.setLocation(event.getX(), event.getY());
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			if(uiRect.contains(mousePoint)) mousePressed = true;
			break;
		case MouseEvent.RELEASE:
			if(mousePressed) {
				mousePressed = false;
				if(saves) PrefToText.setValue(id, value);
			}
			break;
		case MouseEvent.MOVE:
			mouseHovered = uiRect.contains(mousePoint);
			break;
		case MouseEvent.DRAG:
			if(mousePressed) {
				float deltaX = (P.p.mouseX - P.p.pmouseX) * dragStep;
				value += deltaX;
				value = P.constrain(value, valueMin, valueMax);
			}
			break;
		}
	}

	/////////////////////////////////////////
	// Keyboard events
	/////////////////////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(isActive() == false) return;
		if(mousePressed == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKeyCode() == P.LEFT) { value -= dragStep; value = P.max(value, valueMin); }
			if(e.getKeyCode() == P.RIGHT) { value += dragStep; value = P.min(value, valueMax); }
			if(saves) PrefToText.setValue(id, value);
		}
	}

}
