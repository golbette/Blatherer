package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class Recents implements Serializable {
    private final String mContactName;
    private final String mInitials;
    private final String mEmail;
    private final String mUsername;
    private final int mChatId;
    private final int mMemberId;

    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private String mEmail = "";
        private String mUsername = "";
        private int mChatId = -1;
        private int mMemberId = -1;

        public Builder(String firstName, String lastName) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
        }

        public Builder addEmail(final String val) {
            this.mEmail = val;
            return this;
        }

        public Builder addUsername(final String val) {
            this.mUsername = val;
            return this;
        }

        public Builder addChatId(final int val) {
            this.mChatId = val;
            return this;
        }

        public Builder addMemberId(final int val) {
            this.mMemberId = val;
            return this;
        }

        public Recents build() {
            return new Recents(this);
        }
    }

    private Recents(final Builder builder) {
        this.mContactName = builder.mFirstName + " " + builder.mLastName;
        String initials = builder.mFirstName.substring(0, 1) + builder.mLastName.substring(0, 1) + "";
        this.mInitials = initials.toUpperCase();
        this.mEmail = builder.mEmail;
        this.mUsername = builder.mUsername;
        this.mChatId = builder.mChatId;
        this.mMemberId = builder.mMemberId;
    }

    public String getContactName() {
        return mContactName;
    }

    public String getInitials() {
        return mInitials;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getmUsername() {
        return mUsername;
    }

    public int getmChatId() {return mChatId;}

    public int getmMemberId() {return mMemberId;}

}