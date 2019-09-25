package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;

public class Demo_TextToPShapeWobble
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextToPShape textToPShape;
	protected PShape word3d;
	
	PShaderHotSwap wobbleShader;
	protected SimplexNoiseTexture displaceTexture;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1040 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setupFirstFrame()	{
		// build text
		textToPShape = new TextToPShape(TextToPShape.QUALITY_HIGH);
		String fontFile = DemoAssets.fontOpenSansPath;
		word3d = textToPShape.stringToShape3d("CACHEFLOWE", 100, fontFile);
		PShapeUtil.scaleShapeToExtent(word3d, 400);
		PShapeUtil.addTextureUVToShape(word3d, null);
		PShapeUtil.addTestColorToShape(word3d, 0.01f);
		word3d.disableStyle();
		
		// load displacement shader & texture
		wobbleShader = new PShaderHotSwap(
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-vert.glsl"),
			FileUtil.getFile("haxademic/shaders/vertex/mesh-2d-deform-frag.glsl") 
		);
		displaceTexture = new SimplexNoiseTexture(256, 256);
		p.debugView.setTexture("displacement map", displaceTexture.texture());
	}

	public void drawApp() {
		// set context
		background(0);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);

		// update displacement map
		displaceTexture.offsetX(p.frameCount/200f);
		displaceTexture.zoom(1f);
		displaceTexture.update();
		
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		wobbleShader.shader().set("time", p.frameCount);
		wobbleShader.shader().set("displacementMap", displaceTexture.texture());
		wobbleShader.shader().set("displaceAmp", 40f);
		wobbleShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		wobbleShader.update();
		p.shader(wobbleShader.shader());  
		
		// draw word
		p.fill(255);
		p.shape(word3d);
		
		// pop shader
		p.resetShader();
	}

}
