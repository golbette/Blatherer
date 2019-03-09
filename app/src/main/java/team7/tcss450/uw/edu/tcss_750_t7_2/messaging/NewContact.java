package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class NewContact implements Serializable {
    private final String mContactName;
    private final String mInitials;
    private final String mUsername;
    private final int mMemberId;
    private final String mEmail;


    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private int mMemberId = -1;
        private String mUsername = "";
        private String mEmail = "";

        public Builder(String firstName, String lastName) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
        }

        public Builder addMemberId(int val) {
            this.mMemberId = val;
            return this;
        }

        public Builder addUsername(String val) {
            this.mUsername = val;
            return this;
        }

        public Builder addEmail(String val) {
            this.mEmail = val;
            return this;
        }

        public NewContact build() {
            return new NewContact(this);
        }
    }

    private NewContact(final Builder builder) {
        this.mContactName = builder.mFirstName + " " + builder.mLastName;
        String initials = builder.mFirstName.substring(0, 1) + builder.mLastName.substring(0, 1) + "";
        this.mInitials = initials.toUpperCase();
        this.mEmail = builder.mEmail;
        this.mUsername = builder.mUsername;
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

    public String getUsername() {
        return mUsername;
    }

    public int getMemberId() {
        return mMemberId;
    }
}
