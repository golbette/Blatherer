package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class Contact implements Serializable {
    private final String mContactName;
    private final String mInitials;
    private final String mEmail;
    private final String mUsername;

    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private String mInitials = "";
        private String mEmail = "";
        private String mUsername = "";

        public Builder(String firstName, String lastName) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
        }

        public Builder addInitials(final String val) {
            this.mInitials = val;
            return this;
        }

        public Builder addEmail(final String val) {
            this.mEmail = val;
            return this;
        }

        public Builder addUsername(final String val) {
            this.mUsername = val;
            return this;
        }

        public Contact build() {
            return new Contact(this);
        }
    }

    private Contact(final Builder builder) {
        this.mContactName = builder.mFirstName + " " + builder.mLastName;
        String initials = builder.mFirstName.substring(0, 1) + builder.mLastName.substring(0, 1) + "";
        this.mInitials = initials.toUpperCase();
        this.mEmail = builder.mEmail;
        this.mUsername = builder.mUsername;
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

}