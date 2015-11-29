package com.jasonrobinson.racer.ui.race;

import com.jasonrobinson.racer.enumeration.RaceOptions;

public class RacesFragmentFactory {

    private RacesFragmentFactory() {
    }

    public static RacesFragment newFragment(RaceOptions options) {
        switch (options) {
            case UPCOMING:
                return UpcomingRacesFragment.newInstance();
            case IN_PROGRESS:
                return InProgressRacesFragment.newInstance();
            case FINISHED:
                return FinishedRacesFragment.newInstance();
        }

        throw new IllegalArgumentException("Race option not supported.");
    }
}
