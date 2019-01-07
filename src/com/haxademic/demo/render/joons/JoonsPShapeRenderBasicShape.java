package com.haxademic.demo.render.joons;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.render.JoonsWrapper;

public class JoonsPShapeRenderBasicShape
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, true );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_HIGH );

		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	public void drawApp() {
		if(joons == null) {
			p.background(0);
			p.lights();
		}
		joons.jr.background(JoonsWrapper.BACKGROUND_GI);

		setUpRoom();
		
		// set material
		p.fill( 50, 200, 50 );
		joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 50, 200, 50, 0.75f);

		// draw box
		p.rotateY(1);
		p.rotateZ(1);
		box(2);
	}

	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, 0);
		float radiance = 20;
		int samples = 16;
		joons.jr.background(JoonsWrapper.CORNELL_BOX, 
				4000, 3000, 3000,						// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				40, 40, 40, 							// left rgb
				40, 40, 40, 							// right rgb
				60, 60, 60,	 						// back rgb
				60, 60, 60, 							// top rgb
				60, 60, 60  							// bottom rgb
		); 
		popMatrix();		
	}
	
}