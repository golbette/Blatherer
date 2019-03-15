package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class NamesByChatId implements Serializable {
    private int mChatId;
    private String mNames;

    public NamesByChatId(int chatid, String names) {
        this.mChatId = chatid;
        this.mNames = names;
    }

    public int getmChatId() {
        return mChatId;
    }

    public String getmNames() {
        return mNames;
    }
}
