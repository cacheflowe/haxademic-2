package com.haxademic.demo.hardware.mouse;

import java.awt.Point;

import com.haxademic.core.app.PAppletHax;

public class Demo_MouseLock
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int lastLoopFrame = 0;
	protected Point mousePoint;
	protected Point lastMousePoint = new Point();

	public void setupFirstFrame() {
		// keep mouse locked in window
		window.confinePointer(true);
		window.setPointerVisible(true);
	}

	public void drawApp() {
		p.background(0);

		// lock mouse in center, and check offset from last frame
		p.debugView.setValue("mouseMoveX", p.mouseX - lastMousePoint.x);
		p.debugView.setValue("mouseMoveY", p.mouseY - lastMousePoint.y);
		window.warpPointer(width/2, height/2);
		lastMousePoint.setLocation(width/2, height/2);
	}

}