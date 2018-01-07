package com.tsungweiho.intelligentpowersaving.constants;

import com.tsungweiho.intelligentpowersaving.R;

/**
 * Interface that stores all constants used by ListAdapters
 * <p>
 * This interface is used to store all constants used by ListAdapters
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
 */
public interface ListAdapterConstants {

    // Constants used in DrawerListAdpater
    int[] MESSAGE_DRAWER = {R.string.inbox, R.string.starred, R.string.followed, R.string.trash};
    int[] MESSAGE_DRAWER_IMG = {R.mipmap.ic_inbox, R.mipmap.ic_star, R.mipmap.ic_mark, R.mipmap.ic_trash};

    // Constants used in MessageListAdapter
    enum InboxMode {
        VIEW(0),
        EDIT(1);

        private final int mode;

        /**
         * @param mode the integer to set to enum items
         */
        InboxMode(final int mode) {
            this.mode = mode;
        }

        public int toInt() {
            return mode;
        }
    }
}