package com.raffa064.engine.core.collision;

import com.badlogic.gdx.math.Rectangle;
import java.util.List;
import java.util.ArrayList;

public class QuadTree<T> {
	public int level;
	public int max_objects;
    public Rectangle bounds;
	public QuadTree<T>[] nodes = new QuadTree[4];
	public List<Entry<T>> entries;

	private QuadTree(int parent_level, int max_objects, Rectangle bounds) {
		level = parent_level + 1;
		this.max_objects = max_objects;
		this.bounds = bounds;
	}

	public void split() {
		nodes = new QuadTree[4];

		float x = bounds.x;
		float y = bounds.x;
		float hW = bounds.width / 2; // Half Width
		float hH = bounds.height / 2; // Half Height

		nodes[0] = new QuadTree<T>(
			level, 
			max_objects, 
			new Rectangle(
				x,
				y + hH,
				hW, 
				hH
			)
		);

		nodes[1] = new QuadTree<T>(
			level, 
			max_objects, 
			new Rectangle(
				x + hW,
				y + hH,
				hW, 
				hH
			)
		);

		nodes[2] = new QuadTree<T>(
			level, 
			max_objects, 
			new Rectangle(
				x,
				y,
				hW, 
				hH
			)
		);

		nodes[3] = new QuadTree<T>(
			level, 
			max_objects, 
			new Rectangle(
				x + hW,
				y,
				hW, 
				hH
			)
		);

		List<Entry<T>> objects = this.entries;
		this.entries = null;

		for (Entry<T> obj : objects) {
			insert(obj);
		}
	}

	public boolean ovelaps(List<Rectangle> rects) {
		for (Rectangle rect : rects) {
			if (bounds.overlaps(rect)) return true;
		}
		
		return false;
	}
	
	public List<Integer> getIndexes(List<Rectangle> rects) {
		List<Integer> indexes = new ArrayList<Integer>();
		
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].ovelaps(rects)) {
				indexes.add(i);
			}
		}
		
		return null;
	}

	public boolean insert(Entry<T> entry) {
		if (ovelaps(entry.rects)) {
			if (nodes == null) {
				if (entries.size() + 1 < max_objects) {
					entries.add(entry);
					return true;
				}

				split();
			}

			List<Integer> indexes = getIndexes(entry.rects);

			for (Integer index : indexes) {
				nodes[index].insert(entry);
			}

			return true;
		}

		return false;
	}
	

	public boolean insert(T obj, List<Rectangle> rects) {
		Entry<T> entry = new Entry<T>(obj, rects);
		return insert(entry);
	}

	public void retrieve(Rectangle area, List<T> output) {}

	public void clear() {
		if (nodes == null) { // tree haven't nodes
			entries.clear();
			return;
		}

		for (QuadTree node : nodes) {
			node.clear();
		}

		nodes = null;
		entries = new ArrayList<>();
	}
	
	public static class Entry<T> {
		public T obj;
		public List<Rectangle> rects;

		public Entry(T obj, List<Rectangle> rects) {
			this.obj = obj;
			this.rects = rects;
		}
	}
}
