package com.raffa064.engine.core.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class Shape { 
    public float[] shape = {-1, -1, -1, 1, 1, 1, 1, -1};

	public Shape() {

	}
	
	public Shape(float... shape) {
		this.shape = shape;
	}

	public void setShape(float... shape) {
		this.shape = shape;
	}

	public void rect(float w, float h) {
		setShape(-w/2, -h/2, -w/2, h/2, w/2, h/2, w/2, -h/2);
	}
	
	public void square(float s) {
		setShape(-s/2, -s/2, -s/2, s/2, s/2, s/2, s/2, -s/2);
	}
	
	public void circle(int segments, float radius) {
		float[] shape = new float[segments * 2];
		
		for (int i = 0; i < segments; i++) {
			float angle = i * (360f / segments);
			shape[i*2] = MathUtils.cosDeg(angle) * radius;
			shape[i*2+1] = MathUtils.sinDeg(angle) * radius; 
		}
		
		setShape(shape);
	}

	public void nor() {
		nor(shape);
	}

	public float[] scaled(float x, float y) {
		return scaled(shape, x, y);
	}
	
	public float[] scaled(Vector2 scale) {
		return scaled(shape, scale);
	}
	
	public static void nor(float[] shape) {
		float maxDst = 0;
		for (int i = 0; i < shape.length; i += 2) {
			float dst = (float) Math.sqrt(Math.pow(shape[i], 2) + Math.pow(shape[i+1], 2));
			
			if (dst > maxDst) {
				maxDst = dst;
			}
		}
		
		for (int i = 0; i < shape.length; i += 2) {
			shape[i] = shape[i] / maxDst;	
			shape[i+1] = shape[i+1] / maxDst;	
		}
	}
	
	public static float[] scaled(float[] shape, float scaleX, float scaleY) {
		float[] scaled = new float[shape.length];
		
		for (int i = 0; i < scaled.length; i += 2) {
			scaled[i] = shape[i] * scaleX;	
			scaled[i+1] = shape[i+1] * scaleY;	
		}
		
		return scaled;
	}
	
	public static float[] scaled(float[] shape, Vector2 scale) {
		return scaled(shape, scale.x, scale.y);
	}
}
