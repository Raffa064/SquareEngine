package com.raffa064.engine.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class INI {
	public File file;
	public List<Section> sections = new ArrayList<>();

	public INI(File file) throws IOException {
		loadFile(file);
	}

	private void loadFile(File file) throws IOException {
		this.file = file;

		if (!file.exists()) {
			file.createNewFile();
		}

		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();

		String content = new String(buffer, "UTF-8");
		String[] splittedContent = content.split("\n+");

		sections.clear();
		Section current = null;
		for (String line : splittedContent) {
			line = line.trim();

			if (line.matches("\\[.+\\]")) {
				String sectionName = line.substring(1, line.length()-1);
				current = section(sectionName);
				continue;
			}

			if (current != null) {
				if (line.matches("(.+)?=(.*)?")) {
					int equalIndex = line.indexOf("=");
					String attributeName = line.substring(0, equalIndex).trim();
					String attributeValue = line.substring(equalIndex+1, line.length()).trim();

					INI.Attribute attribute = current.getAttribute(attributeName);
					attribute.value = attributeValue;
				}
			}
		}
	}

	public Section section(String sectionName) {
		for (Section section : sections) {
			if (section.name.equals(sectionName)) {
				return section;
			}
		}	

		Section section = new Section(this, sectionName);
		sections.add(section);
		return section;
	}

	public INI string(String sectionName, String attributeName, String value) {
		Attribute attribute = section(sectionName).getAttribute(attributeName);
		attribute.value = value;

		return this;
	}

	public INI integer(String sectionName, String attributeName, int value) {
		return string(sectionName, attributeName, String.valueOf(value));
	}

	public INI longInt(String sectionName, String attributeName, long value) {
		return string(sectionName, attributeName, String.valueOf(value));
	}

	public INI bool(String sectionName, String attributeName, boolean value) {
		return string(sectionName, attributeName, String.valueOf(value));
	}

	public INI file(String sectionName, String attributeName, File value) {
		return string(sectionName, attributeName, String.valueOf(value));
	}

	public String string(String sectionName, String attributeName) {
		return section(sectionName).getAttribute(attributeName).value;
	}

	public Integer integer(String sectionName, String attributeName) {
		try {
			return Integer.parseInt(string(sectionName, attributeName));
		} catch(Exception e) {
			return null;
		}
	}

	public Long longint(String sectionName, String attributeName) {
		try {
			return Long.parseLong(string(sectionName, attributeName));
		} catch(Exception e) {
			return null;
		}
	}

	public Boolean bool(String sectionName, String attributeName) {
		try {
			return Boolean.parseBoolean(string(sectionName, attributeName));
		} catch(Exception e) {
			return null;
		}
	}

	public File file(String sectionName, String attributeName) {
		String path = string(sectionName, attributeName);

		if (path != null) {
			File file = new File(path);
			return file;	
		}

		return null;
	}

	public void commit() throws IOException {
		String content = toString();
		byte[] buffer = content.getBytes();

		FileOutputStream fis = new FileOutputStream(file);
		fis.write(buffer);
		fis.flush();
		fis.close();
	}

	@Override
	public String toString() {
		String str = "";

		for (Section section : sections) {
			str += section + "\n";
		}

		return str;
	}

    private static class Section {
		public INI ini;
		public String name;
		public List<Attribute> attributes = new ArrayList<>();

		public Section(INI ini, String name) {
			this.ini = ini;
			this.name = name;
		}

		public Attribute getAttribute(String attributeName) {
			attributeName = attributeName.trim();

			for (Attribute attribute : attributes) {
				if (attribute.name.equals(attributeName)) {
					return attribute;
				}
			}	

			Attribute attribute = new Attribute();
			attribute.name = attributeName;

			attributes.add(attribute);
			return attribute;
		}

		public Section string(String attributeName, String value) {
			Attribute attribute = getAttribute(attributeName);
			attribute.value = value;

			return this;
		}

		public Section integer(String attributeName, int value) {
			return string(attributeName, String.valueOf(value));
		}

		public Section longInt(String attributeName, long value) {
			return string(attributeName, String.valueOf(value));
		}

		public Section bool(String attributeName, boolean value) {
			return string(attributeName, String.valueOf(value));
		}

		public Section file(String attributeName, File value) {
			return string(attributeName, String.valueOf(value));
		}

		public String string(String attributeName) {
			return getAttribute(attributeName).value;
		}

		public Integer integer(String attributeName) {
			try {
				return Integer.parseInt(string(attributeName));
			} catch(Exception e) {
				return null;
			}
		}

		public Long longint(String attributeName) {
			try {
				return Long.parseLong(string(attributeName));
			} catch(Exception e) {
				return null;
			}
		}

		public Boolean bool(String attributeName) {
			try {
				return Boolean.parseBoolean(string(attributeName));
			} catch(Exception e) {
				return null;
			}
		}

		public File file(String attributeName) {
			String path = string(attributeName);

			if (path != null) {
				File file = new File(path);
				return file;	
			}

			return null;
		}

		public void commit() throws IOException {
			ini.commit();		
		}

		@Override
		public String toString() {
			String str = "[" + name + "]";
			for (Attribute attribute : attributes) {
				str += "\n" + attribute;
			}

			return str;
		}
	}

	private static class Attribute {
		public String name;
		public String value;

		@Override
		public String toString() {
			String str = name + "="+value;
			return str;
		}
	}
}
