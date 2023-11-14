package com.raffa064.engine.core;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			int random = (int) (Math.random() * 10);
			
			addSort(list, random);
		}
		
		System.out.println(list);
	}

	private static void addSort(List<Integer> list, int random) {
		for (int j = 0; j < list.size(); j++) {
			if (random < list.get(j)) {
				list.add(j, random);
				return;
			}
		}

		list.add(random);
	}
}
