package com.haxademic.app.haxmapper.overlays;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class MeshLineSegment {

	protected PVector _point1;
	protected PVector _point2;
	protected PVector _utilVec;
	protected float _angle;
	protected float _radians;
	protected float _length;
	public static PShape waveformShape;
	public static int waveformShapeFrameDrew = 0;

	public MeshLineSegment( float x1, float y1, float x2, float y2 ) {
		_point1 = new PVector(x1, y1);
		_point2 = new PVector(x2, y2);
		_angle = MathUtil.getAngleToTarget(x1, y1, x2, y2);
		_radians = MathUtil.degreesToRadians(_angle + 90);
		_length = MathUtil.getDistance(x1, y1, x2, y2);
		_utilVec = new PVector();
		
		if( waveformShape == null ) {
			int width = 1000;
			waveformShape = P.p.createShape();
			waveformShape.beginShape();
			waveformShape.noFill();
			waveformShape.stroke(255);
			waveformShape.strokeWeight(4);
			float spacing = width / P.p._waveformData._waveform.length;
			float startX = 0;
			for (int i = 0; i < P.p._waveformData._waveform.length; i++ ) {			
				waveformShape.vertex(startX + i * spacing, 0);
			}
			waveformShape.endShape();
		}
	}

	public void update( PGraphics pg, int mode, float ampTotal, float amp ) {
		if( mode == MeshLines.MODE_EQ_TOTAL ) {
			pg.strokeWeight( ampTotal * 20f );
			pg.stroke(255);
			pg.line( _point1.x, _point1.y, _point2.x, _point2.y );
		} else if( mode == MeshLines.MODE_EQ_BARS_BLACK ) {
			pg.strokeWeight( ampTotal * 20f );
			pg.stroke(0);
			pg.line( _point1.x, _point1.y, _point2.x, _point2.y );
		} else if( mode == MeshLines.MODE_DOTS ) {
			pg.noStroke();
			pg.fill(255);
			pg.ellipse( _point1.x, _point1.y, 25 * amp, 25 * amp );
		} else if( mode == MeshLines.MODE_WAVEFORMS ) {
			DrawUtil.setDrawCorner(pg);
			pg.noFill();
			pg.stroke(255);
			pg.strokeWeight(3);
			
			amp = 5;
			
			if(waveformShapeFrameDrew != P.p.frameCount) {
				for (int i = 0; i < waveformShape.getVertexCount(); i++) {
					waveformShape.setVertex( i, waveformShape.getVertexX(i),  -0.5f + P.p._waveformData._waveform[i] * amp );
				}
				waveformShapeFrameDrew = P.p.frameCount;
			}
						
			pg.pushMatrix();
			pg.translate( _point2.x, _point2.y );
			pg.rotate(_radians);
			pg.shape(waveformShape, 0, 0, _length, waveformShape.height);
			
//			pg.translate( (_point1.x + _point2.x)/2f, (_point1.y + _point2.y)/2f );
//			for (int i = 1; i < waveformData._waveform.length; i++ ) {			
//				pg.line( 
//						startX + (i-1) * _spacing,
//						waveformData._waveform[(i-1)] * amp,
//						startX + i * _spacing,
//						waveformData._waveform[i] * amp 
//				);
//			}
			pg.popMatrix();
		} else if( mode == MeshLines.MODE_EQ_BARS ) {
			pg.strokeWeight( amp * 20f );
			pg.stroke(255);
			pg.line( _point1.x, _point1.y, _point2.x, _point2.y );
		} else if( mode == MeshLines.MODE_LINE_EXTEND ) {
			pg.strokeWeight( 4f );
			pg.stroke(255);

			_utilVec.set( _point2 );
			_utilVec.lerp( _point1, amp );
			pg.line( _utilVec.x, _utilVec.y, _point2.x, _point2.y );

			_utilVec.set( _point1 );
			_utilVec.lerp( _point2, amp );
			pg.line( _utilVec.x, _utilVec.y, _point1.x, _point1.y );
		} else if( mode == MeshLines.MODE_NONE ) {
			// do nothing
		}
	}

	// helps make sure that we don't add existing segments
	public boolean matches( float x1, float y1, float x2, float y2 ) {
		if( _point1.x == x1 && _point1.y == y1 && _point2.x == x2 && _point2.y == y2 ) {
			return true;
		} else {
			return false;
		}
	}
}
