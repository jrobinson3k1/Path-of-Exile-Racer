package com.jasonrobinson.racer.dagger.component;

import com.jasonrobinson.racer.adapter.RacesAdapter;
import com.jasonrobinson.racer.receiver.RaceAlarmReceiver;
import com.jasonrobinson.racer.ui.MainActivity;
import com.jasonrobinson.racer.ui.race.RacesFragment;

public interface RacerComponent {

    void inject(RacesFragment obj);

    void inject(MainActivity obj);

    void inject(RacesAdapter obj);

    void inject(RaceAlarmReceiver obj);
}
