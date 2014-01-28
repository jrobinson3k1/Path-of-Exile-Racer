package com.jasonrobinson.racer.model;

public enum PoeClass {

	MARAUDER("Marauder"),
	RANGER("Ranger"),
	WITCH("Witch"),
	DUELIST("Duelist"),
	TEMPLAR("Templar"),
	SHADOW("Shadow"),
	SCION("Scion");

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
