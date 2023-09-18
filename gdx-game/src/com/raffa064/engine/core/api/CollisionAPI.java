package com.raffa064.engine.core.api;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.collision.QuadTree;
import com.raffa064.engine.core.components.Transform2D;
import java.util.ArrayList;
import java.util.List;

public class CollisionAPI extends API {
	private List<QuadTree> quadtrees = new ArrayList<>();
    private List<Areas> areasList = new ArrayList<>();
	
	public CollisionAPI(App app) {
		super(app);
	}
	
	public Areas createAreas(GameObject obj, List<String> layer, List<String> mask, List<Rectangle> rects) {
		Areas areas = new Areas(obj, layer, mask, rects);
		
		areasList.add(areas);
		
		return areas;
	}
	
	public Areas createAreas(GameObject obj) {
		List<String> layer = new ArrayList<String>();
		List<String> mask = new ArrayList<String>();
		List<Rectangle> rects = new ArrayList<Rectangle>();
		
		return createAreas(obj, layer, mask, rects);
	}
	
	public List<Areas> getCollided(Areas A) {
		List<Areas> collidedAreas = new ArrayList<>();
		for (Areas B : areasList) {
			if (B == A) continue;
			
			if (A.masks(B)) {
				if (A.collides(B)) {
					collidedAreas.add(B);
				}
			}
		}
		
		return collidedAreas;
	}
	
	public List<GameObject> getCollidedObjects(Areas A) {
		List<GameObject> collidedObjects = new ArrayList<>();
		for (Areas B : areasList) {
			if (B == A) continue;

			if (A.masks(B)) {
				if (A.collides(B)) {
					collidedObjects.add(B.obj);
				}
			}
		}

		return collidedObjects;
	}
	
	public void removeArea(Areas areas) {
		areasList.remove(areas);
	}
	
	public static class Areas {
		public GameObject obj;
		public List<String> layer = new ArrayList<>();
		public List<String> mask = new ArrayList<>();
		public List<Rectangle> rects = new ArrayList<>();
		public Vector2 pos, scale;

		public Areas(GameObject obj, List<String> layer, List<String> mask, List<Rectangle> rects) {
			this.obj = obj;
			this.layer = layer;
			this.mask = mask;
			this.rects = rects;
			
			Transform2D transform = (Transform2D) obj.get("Transform2D");
			if (transform != null) {
				pos = transform.pos;
				scale = transform.scale;
				return;
			}
		}
		
		public boolean masks(Areas areas) {
			for (String layer : areas.layer) {
				if (mask.contains(layer)) {
					return true;
				}
			}
			
			return false;
		}
		
		public void transformRect(Rectangle rect, Rectangle out) {
			out.x = pos.x - rect.width * scale.x / 2;
			out.y = pos.y - rect.width * scale.y / 2;
			out.width = rect.width * scale.x;
			out.height = rect.height * scale.y;
		}
		
		public boolean collides(Areas areas) {
			Rectangle A = new Rectangle();
			Rectangle B = new Rectangle();
			
			for (Rectangle r1 : rects) {
				for (Rectangle r2 : areas.rects) {
					transformRect(r1, A);
					areas.transformRect(r2, B);
					
					if (A.overlaps(B)) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
