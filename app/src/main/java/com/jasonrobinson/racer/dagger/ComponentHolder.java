package com.jasonrobinson.racer.dagger;

import com.jasonrobinson.racer.dagger.component.DaggerProductionComponent;
import com.jasonrobinson.racer.dagger.component.RacerComponent;

public class ComponentHolder {

    private static ComponentHolder ourInstance = new ComponentHolder();

    private RacerComponent mRacerComponent;
    private boolean mApplicationCreateCalled;

    private ComponentHolder() {
    }

    public static ComponentHolder getInstance() {
        return ourInstance;
    }

    public void onApplicationCreate() {
        if (mRacerComponent == null) {
            mRacerComponent = DaggerProductionComponent.create();
        }

        mApplicationCreateCalled = true;
    }

    public void setComponent(RacerComponent component) {
        if (mApplicationCreateCalled) {
            throw new IllegalStateException("Cannot set component after onApplicationCreate has been called");
        }

        mRacerComponent = component;
    }

    public RacerComponent component() {
        if (!mApplicationCreateCalled) {
            throw new IllegalStateException("The component has not been set yet. Did you call onApplicationCreate?");
        }

        return mRacerComponent;
    }
}
