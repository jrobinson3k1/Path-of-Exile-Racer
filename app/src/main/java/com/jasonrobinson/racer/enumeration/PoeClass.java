package com.jasonrobinson.racer.enumeration;

public enum PoEClass {

    DUELIST("Duelist"),
    MARAUDER("Marauder"),
    RANGER("Ranger"),
    SCION("Scion"),
    SHADOW("Shadow"),
    TEMPLAR("Templar"),
    WITCH("Witch");

    private String mName;

    PoEClass(String name) {
        mName = name;
    }

    public static PoEClass getClassForName(String name) {
        PoEClass[] poEClasses = values();
        for (PoEClass poEClass : poEClasses) {
            if (poEClass.getName().equalsIgnoreCase(name)) {
                return poEClass;
            }
        }

        return null;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return getName();
    }
}
