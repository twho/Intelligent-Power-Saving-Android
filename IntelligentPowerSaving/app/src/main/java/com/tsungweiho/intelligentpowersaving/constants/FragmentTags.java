package com.tsungweiho.intelligentpowersaving.constants;

import com.tsungweiho.intelligentpowersaving.R;

import java.util.ArrayList;

/**
 * Interface that stores all fragment tags
 * <p>
 * This interface is used to store all fragment tags, which is used by fragmentManager
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
 */
public interface FragmentTags {
    String packageName = "com.tsungweiho.intelligentpowersaving";

    /**
     * Main fragments
     */
    enum MainFragment {
        HOME(packageName + "HomeFragment"),
        EVENT(packageName + "EventFragment"),
        INBOX(packageName + "InboxFragment"),
        SETTINGS(packageName + "SettingsFragment");

        private final String text;

        /**
         * @param text the String text to set to enum items
         */
        MainFragment(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    /**
     * The order of main fragments
     */
    ArrayList<MainFragment> mainFragments = new ArrayList<MainFragment>() {{
        add(MainFragment.HOME);
        add(MainFragment.EVENT);
        add(MainFragment.INBOX);
        add(MainFragment.SETTINGS);
    }};

    /**
     * Child fragments
     */
    enum ChildFragment {
        BUILDING(packageName + "BuildingFragment"),
        MESSAGE(packageName + "MessageFragment"),
        REPORT(packageName + "ReportFragment");

        private final String text;

        /**
         * @param text the String text to set to enum items
         */
        ChildFragment(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    /**
     * Keys of the bundle passed to the fragment
     */
    String BUILDING_FRAGMENT_KEY = packageName + "BuildingFragmentKey";
    String MESSAGE_FRAGMENT_KEY = packageName + "MessageFragmentKey";

    /**
     * Icons of fragments
     */
    int[] activeIcons = {R.mipmap.ic_home, R.mipmap.ic_event, R.mipmap.ic_mail, R.mipmap.ic_settings};
    int[] inactiveIcons = {R.mipmap.ic_home_unclick, R.mipmap.ic_event_unclick, R.mipmap.ic_mail_unclick, R.mipmap.ic_settings_unclick};
}
