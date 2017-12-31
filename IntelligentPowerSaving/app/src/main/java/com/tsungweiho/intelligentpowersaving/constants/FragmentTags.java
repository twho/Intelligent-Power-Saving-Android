package com.tsungweiho.intelligentpowersaving.constants;

import com.tsungweiho.intelligentpowersaving.R;

import java.util.ArrayList;

/**
 * Interface that stores all fragment tags
 *
 * This interface is used to store all fragment tags, which is used by fragmentManager
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
 */
public interface FragmentTags {
    String packageName = "com.tsungweiho.intelligentpowersaving";

    // Main Fragments
    String HOME_FRAGMENT = packageName + "HomeFragment";
    String EVENT_FRAGMENT = packageName + "EventFragment";
    String INBOX_FRAGMENT = packageName + "InboxFragment";
    String SETTINGS_FRAGMENT = packageName + "SettingsFragment";

    // Extended Fragment
    String BUILDING_FRAGMENT = packageName + "BuildingFragment";
    String BUILDING_FRAGMENT_KEY = packageName + "BuildingFragmentKey";
    String MESSAGE_FRAGMENT = packageName + "MessageFragment";
    String MESSAGE_FRAGMENT_KEY = packageName + "MessageFragmentKey";

    // Icons of fragments
    int[] activeIcons = {R.mipmap.ic_home, R.mipmap.ic_event, R.mipmap.ic_mail, R.mipmap.ic_settings};
    int[] inactiveIcons = {R.mipmap.ic_home_unclick, R.mipmap.ic_event_unclick, R.mipmap.ic_mail_unclick, R.mipmap.ic_settings_unclick};

    // Fragment order
    ArrayList<String> mainFragments = new ArrayList<String>() {{
        add(HOME_FRAGMENT);
        add(EVENT_FRAGMENT);
        add(INBOX_FRAGMENT);
        add(SETTINGS_FRAGMENT);
    }};
}
