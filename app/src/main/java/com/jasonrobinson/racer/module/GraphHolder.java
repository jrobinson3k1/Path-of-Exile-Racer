package com.jasonrobinson.racer.module;

import dagger.ObjectGraph;

public class GraphHolder {
    private static GraphHolder sInstance;

    private Object[] mModules;

    private ObjectGraph mGraph;

    private GraphHolder() {
    }

    public static GraphHolder getInstance() {
        if (sInstance == null) {
            sInstance = new GraphHolder();
        }

        return sInstance;
    }

    public void inject(Object object) {
        if (mGraph == null) {
            create();
        }

        mGraph.inject(object);
    }

    public <T> T get(Class<T> type) {
        if (mGraph == null) {
            create();
        }

        return mGraph.get(type);
    }

    public void addModules(Object... modules) {
        if (mGraph != null) {
            mGraph.plus(modules);
        } else {
            if (mModules == null) {
                mModules = modules;
            } else {
                mModules = concatenate(mModules, modules);
            }
        }
    }

    private void create() {
        mGraph = ObjectGraph.create(mModules);
        mModules = null;
    }

    private Object[] concatenate(Object[] a, Object[] b) {
        int aLength = a.length;
        int bLength = b.length;

        Object[] c = new Object[aLength + bLength];
        System.arraycopy(a, 0, c, 0, aLength);
        System.arraycopy(b, 0, c, aLength, bLength);

        return c;
    }
}
