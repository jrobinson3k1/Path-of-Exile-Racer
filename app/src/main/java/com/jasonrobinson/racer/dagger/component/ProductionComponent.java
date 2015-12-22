package com.jasonrobinson.racer.dagger.component;

import com.jasonrobinson.racer.dagger.module.ContextModule;
import com.jasonrobinson.racer.dagger.module.ManagerModule;
import com.jasonrobinson.racer.dagger.module.RestModule;
import com.jasonrobinson.racer.dagger.module.SettingsModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, RestModule.class, SettingsModule.class, ManagerModule.class})
public interface ProductionComponent extends RacerComponent {

}
