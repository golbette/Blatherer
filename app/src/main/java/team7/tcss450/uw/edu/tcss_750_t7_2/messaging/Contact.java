package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class Contact implements Serializable {
    private final String mContactName;
    private final String mInitials;
    private final String mEmail;

    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private String mInitials = "";
        private String mEmail = "";

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

        public Contact build() {
            return new Contact(this);
        }
    }

    private Contact(final Builder builder) {
        this.mContactName = builder.mFirstName + " " + builder.mLastName;
        this.mInitials = builder.mInitials;
        this.mEmail = builder.mEmail;
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

}