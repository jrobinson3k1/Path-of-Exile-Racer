package com.jasonrobinson.racer.model;

public enum PoeClass {

	DUELIST("Duelist"),
	MARAUDER("Marauder"),
	RANGER("Ranger"),
	SCION("Scion"),
	SHADOW("Shadow"),
	TEMPLAR("Templar"),
	WITCH("Witch");

	private String mName;

	PoeClass(String name) {

		mName = name;
	}

	public static PoeClass getClassForName(String name) {

		PoeClass[] poeClasses = values();
		for (PoeClass poeClass : poeClasses) {
			if (poeClass.getName().equalsIgnoreCase(name)) {
				return poeClass;
			}
		}

		return null;
	}

	public String getName() {

		return mName;
	}

	@Override
	public String toString() {

		return mName;
	}
}
