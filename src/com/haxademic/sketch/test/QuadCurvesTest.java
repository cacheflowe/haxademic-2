package com.haxademic.sketch.test;

import processing.core.PConstants;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

@SuppressWarnings("serial")
public class QuadCurvesTest
extends PAppletHax {
	
	protected PVector[] _points;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1600" );
		_appConfig.setProperty( "height", "1300" );
		_appConfig.setProperty( "rendering", "false" );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_points = new PVector[10];
		randomPoints();
	}
	
	protected void randomPoints() {
		for( int i=0; i < _points.length; i++ ) {
			if(i == 0)
				_points[i] = new PVector(p.random(p.width * 0.2f), p.height * 0.5f);
			else
				_points[i] = new PVector(p.random(_points[i-1].x + 50, _points[i-1].x + 200), p.random(_points[i-1].y - 200, _points[i-1].y + 200));
		}
	}
	
	public void drawApp() {
		p.background(ColorUtil.colorFromHex("#F6D3C0"));
		p.background(0);
		
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setDrawCenter(p);		
		
		// oscillate points
		for( int i=0; i < _points.length; i++ ) {
			_points[i].set(_points[i].x + P.sin(p.frameCount/100f * i), _points[i].y + P.sin(p.frameCount/120f * i));
		}

		
		// draw curves ------------------------
		PVector midpoint = new PVector();
		PVector prevpoint = new PVector();
		PVector curpoint = new PVector();
		int numLines = _points.length;
		int numPoints = _points.length;
		for( int k=0; k < numLines; k++ ) {
//			p.noStroke();
//			if(k%2 == 0) p.fill(255); else p.fill(0);
			p.beginShape();
			p.strokeCap(PConstants.ROUND);
			p.strokeWeight(1f * (numLines - k));
			p.stroke(255);
			p.noFill();

			for( int i=0; i < numPoints; i++ ) {
				float distFromEdge = P.min(i, (numPoints) - i);
				distFromEdge--;	// helps reduce down to 0 on both ends
				distFromEdge *= k * 8f;
				if(distFromEdge < 0) distFromEdge = 0;
				
				float nextDistFromEdge = P.min(i+1, (numPoints) - i-1);
				nextDistFromEdge--;	// helps reduce down to 0 on both ends
				nextDistFromEdge *= k * 8f;
				if(nextDistFromEdge < 0) nextDistFromEdge = 0;

				if( i > 0 ) {
					// get points, offset by dist push
					prevpoint.set(_points[i-1].x, _points[i-1].y + distFromEdge);
					curpoint.set(_points[i].x, _points[i].y + nextDistFromEdge);
					midpoint.set(PVector.lerp(prevpoint, curpoint, 0.5f));
					
					if( i == 1 ) {
						p.vertex(prevpoint.x, prevpoint.y);
					} else {					
						p.quadraticVertex(prevpoint.x, prevpoint.y, midpoint.x, midpoint.y);
					}
				}
			}
			p.endShape();
		}
		
		// draw accent circles
		for( int k=0; k < numLines; k++ ) {
//			p.noStroke();
			p.strokeWeight(2);

			for( int i=0; i < numPoints; i++ ) {
				float distFromEdge = P.min(i, (numPoints) - i);
				distFromEdge--;	// helps reduce down to 0 on both ends
				distFromEdge *= k * 5f;
				if(distFromEdge < 0) distFromEdge = 0;
				
				float nextDistFromEdge = P.min(i+1, (numPoints) - i-1);
				nextDistFromEdge--;	// helps reduce down to 0 on both ends
				nextDistFromEdge *= k * 5f;
				if(nextDistFromEdge < 0) nextDistFromEdge = 0;

				if( i > 0 ) {
					// get points, offset by dist push
					prevpoint.set(_points[i-1].x, _points[i-1].y + distFromEdge);
					curpoint.set(_points[i].x, _points[i].y + nextDistFromEdge);
					midpoint.set(PVector.lerp(prevpoint, curpoint, 0.5f));
					
					if(0 == k && i < numPoints-1) {
						p.stroke(255);
						// p.ellipse(curpoint.x, curpoint.y + nextDistFromEdge, 10 + nextDistFromEdge, 10 + nextDistFromEdge);
					}
				}
				
			}
			p.endShape();
		}

		
//		p.strokeCap(P.ROUND);
//		p.strokeWeight(3);
//		p.stroke(255);
//		p.noFill();
//		p.beginShape();
//
//		PVector midpoint = new PVector();
//		for( int i=0; i < _points.length; i++ ) {
//			if( i > 0 ) {
//				midpoint.set(PVector.lerp(_points[i-1], _points[i], 0.5f));
//				if( i == 1 ) {
//					p.vertex(midpoint.x, midpoint.y);
//				} else {					
//					p.quadraticVertex(_points[i-1].x, _points[i-1].y, midpoint.x, midpoint.y);
//				}
//			}
//	
//		}
//		p.endShape();

		
		// draw lines ------------------------
//		p.strokeCap(P.ROUND);
//		p.strokeWeight(3);
//		p.stroke(0,255,0);
//		p.noFill();
//		p.beginShape();
//
//		for( int i=0; i < _points.length; i++ ) {
//			if( i == 0 ) {
//				p.vertex(_points[i].x, _points[i].y);
//			} else {
//				midpoint.set(PVector.lerp(_points[i-1], _points[i], 0.5f));
//				p.vertex(_points[i].x, _points[i].y);
//			}
//	
//		}
//		p.endShape();

		// draw midpoints ------------------------
//		p.noStroke();
//		p.fill(0,0,255);
//		for( int i=0; i < _points.length; i++ ) {
//			if( i == 0 ) {
//			} else {
//				midpoint.set(PVector.lerp(_points[i-1], _points[i], 0.5f));
//				p.ellipse(midpoint.x, midpoint.y, 10, 10);
//			}
//		}

	}
	
	public void mousePressed() {
		super.mousePressed();
		randomPoints();
	}
	
	public void keyPressed() {
		if(p.key == ' ') {
		} else if (p.key == 'm') {
		}
		if ( p.key == '\\' ) { 
			p.save( FileUtil.getHaxademicOutputPath() + "_screenshots/" + SystemUtil.getTimestamp(p) + ".png");
			// ScreenUtil.screenshotHiRes( p, 3, P.P3D, FileUtil.getHaxademicOutputPath() + "_screenshots/" );
		}

	}
}