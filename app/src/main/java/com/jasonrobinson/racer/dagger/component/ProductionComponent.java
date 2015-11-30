package com.jasonrobinson.racer.dagger.component;

import com.jasonrobinson.racer.dagger.module.RestModule;
import com.jasonrobinson.racer.dagger.module.SettingsModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RestModule.class, SettingsModule.class})
public interface ProductionComponent extends RacerComponent {
}
