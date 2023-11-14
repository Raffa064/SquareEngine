package com.raffa064.engine.core;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		// ADDING CHILD:
		
		GameObject A = new GameObject();
		GameObject B = new GameObject();
		
		A.addChild(B); // Add before ready -> call ready within obj
		A.ready();

		GameObject C = new GameObject();
		GameObject D = new GameObject();

		C.ready();
		C.addChild(D); // Add after ready -> call ready in the next frame
	}
}
