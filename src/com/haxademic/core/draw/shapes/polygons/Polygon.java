package com.haxademic.core.draw.shapes.polygons;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;

import processing.core.PGraphics;
import processing.core.PVector;

public class Polygon {

	protected PVector center = new PVector();
	protected ArrayList<PVector> vertices;
	protected HashMap<Edge, Polygon> neighbors;
	protected ArrayList<Edge> edges;
	protected int numVertices = 0;

	protected PVector collideVec = new PVector(); 
	protected PVector utilVec = new PVector(); 
	protected PVector newNeighborCenter = new PVector(); 
	
	protected boolean collided = false;
	
	/////////////////////////////////////
	// INIT
	/////////////////////////////////////

	public Polygon(float[] verticesXYZ) {
		// turn int of 3-component coordinates into PVectors
		ArrayList<PVector> verticesPVector = new ArrayList<PVector>();
		for (int i = 0; i < verticesXYZ.length; i+=3) {
			PVector pVector = new PVector(verticesXYZ[i], verticesXYZ[i+1], verticesXYZ[i+2]);
			verticesPVector.add(pVector);
		}
		init(verticesPVector);
	}
	
	public Polygon(ArrayList<PVector> vertices) {
		init(vertices);
	}
	
	protected void init(ArrayList<PVector> vertices) {
		this.vertices = vertices;
		numVertices = vertices.size();
		neighbors = new HashMap<Edge, Polygon>();
		edges = new ArrayList<Edge>();
		buildEdges();
		calcCentroid();
	}
	
	/////////////////////////////////////
	// GENERATORS
	/////////////////////////////////////

	public static Polygon buildShape(float x, float y, float vertices, float radius) {
		return buildShape(x, y, vertices, radius, -P.HALF_PI);
	}
	
	public static Polygon buildShape(float x, float y, float vertices, float radius, float radsOffset) {
		float vertexRads = P.TWO_PI / vertices;
		ArrayList<PVector> verticesPVector = new ArrayList<PVector>();
		
		for (int i = 0; i < vertices; i++) {
			verticesPVector.add(new PVector(
					x + radius * P.cos(radsOffset + vertexRads * i),
					y + radius * P.sin(radsOffset + vertexRads * i),
					0
			));
		}
		return new Polygon(verticesPVector);
	}

	/////////////////////////////////////
	// GETTERS / SETTERS
	/////////////////////////////////////
	
	public ArrayList<PVector> vertices() {
		return vertices;
	}
	
	public ArrayList<Edge> edges() {
		return edges;
	}
	
	public boolean collided() { return collided; }
	public void collided(boolean collided) { this.collided = collided; }
	

	/////////////////////////////////////
	// CALCULATE POSITIONS
	/////////////////////////////////////

	public void translate(float x, float y, float z) {
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			v.add(x, y, z);
		}
		calcCentroid();
	}
	
	protected void calcCentroid() {
		center.set(0, 0, 0);
		for (int i = 0; i < vertices.size(); i++) {
			center.add(vertices.get(i));
		}
		center.div(vertices.size());
	}
	
	protected PVector midPoint(PVector v1, PVector v2) {
		utilVec.set(v1);
		utilVec.lerp(v2, 0.5f);
		return utilVec;
	}
	
	/////////////////////////////////////
	// DRAW
	/////////////////////////////////////
	
	public void draw(PGraphics pg) {
		updateEdges();
		drawEdges(pg);
		drawShapeBg(pg);
		drawShapeOutline(pg);
		drawNeighborDebug(pg);
		drawCentroid(pg);
//		drawMouseOver(pg);
	}

	protected void drawShapeOutline(PGraphics pg) {
		pg.noFill();
		pg.stroke(255);
		pg.beginShape();
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			pg.vertex(v.x, v.y, v.z);
		}
		pg.endShape(P.CLOSE);
	}
	
	protected void drawCentroid(PGraphics pg) {
		pg.fill(0, 255, 0);
		pg.noStroke();
		pg.circle(center.x, center.y, 4);
	}
	
	protected void drawShapeBg(PGraphics pg) {
		pg.fill(0);
		if(collided) pg.fill(0, 255, 0, 50);
		pg.noStroke();
		pg.beginShape();
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			pg.vertex(v.x, v.y, v.z);
		}
		pg.endShape(P.CLOSE);

		
	}
		
	/////////////////////////////////////
	// EDGES
	/////////////////////////////////////

	protected void buildEdges() {
		for (int i = 0; i < vertices.size(); i++) {
			edges.add(new Edge(vertices.get(i), vertices.get((i+1) % numVertices)));
		}
	}
	
	protected void updateEdges() {
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).update();
		}
	}
	
	protected void drawEdges(PGraphics pg) {
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).draw(pg);
		}
	}
	
	/////////////////////////
	// NEIGHBORS
	/////////////////////////
	
	protected void drawNeighborDebug(PGraphics pg) {
		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			if(neighbors.containsKey(edge)) {
				pg.fill(0, 255, 0);
			} else {
				pg.fill(255, 0, 0);
			}
			pg.noStroke();
			PVector almostEdge = midPoint(center, edge.midPoint());
			pg.circle(almostEdge.x, almostEdge.y, 5);
		}
	}
	
	public boolean needsNeighbors() {
		P.out(neighbors.keySet().size(), edges.size());
		return (neighbors.keySet().size() < edges.size()); 
	}
	
	public Edge availableNeighborEdge() {
		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			if(neighbors.containsKey(edge) == false) {
				return edge;
			}
		}
		return null;
	}
	
	public PVector newNeighbor3rdVertex(Edge edge, float ampOut) {
		newNeighborCenter.set(center);
		newNeighborCenter.lerp(edge.midPoint(), ampOut);	// lerp beyond edge midpoint from polygon center
		return newNeighborCenter;
	}
	
	public void setNeighbor(Polygon newNeighbor) {
		Edge sharedEdge = findSharedEdge(newNeighbor);
		neighbors.put(sharedEdge, newNeighbor);
	}
	
	public Edge findSharedEdge(Polygon newNeighbor) {
		ArrayList<Edge> otherPolyEdges = newNeighbor.edges();
		boolean matchedEdge = false;
		for (int i = 0; i < otherPolyEdges.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				Edge otherEdge = otherPolyEdges.get(i);
				Edge myEdge = edges.get(j);
				if(myEdge.matchesEdge(otherEdge)) {
					matchedEdge = true;
					P.out("matchedEdge", myEdge.toString(), otherEdge.toString());
					return myEdge;
				}
			}
		}
		P.out("matchedEdge", matchedEdge);
		return null;
	}
	
	public void mergeWithNeighbor() {
		
	}
	
}
