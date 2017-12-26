package com.tsungweiho.intelligentpowersaving.constants;

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
}
