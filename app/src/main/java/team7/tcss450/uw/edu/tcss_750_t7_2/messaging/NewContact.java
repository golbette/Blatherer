package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class NewContact implements Serializable {
    private final String mContactName;
    private final String mInitials;

    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private String mInitials = "";

        public Builder(String firstName, String lastName) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
        }

        public Builder addInitials(final String val) {
            this.mInitials = val;
            return this;
        }

        public NewContact build() {
            return new NewContact(this);
        }
    }

    private NewContact(final Builder builder) {
        this.mContactName = builder.mFirstName + " " + builder.mLastName;
        String initials = builder.mFirstName.charAt(0) + builder.mLastName.charAt(0) + "";
        this.mInitials = initials.toUpperCase();
    }

    public String getContactName() {
        return mContactName;
    }

    public String getInitials() {
        return mInitials;
    }
}
