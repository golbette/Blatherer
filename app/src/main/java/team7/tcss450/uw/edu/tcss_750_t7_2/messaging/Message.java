package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class Message implements Serializable {
    private final String mContactName;
    private final String mInitials;
    private final String mPreview;

    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private String mInitials = "";
        private String mPreview = "";

        public Builder(String firstName, String lastName) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
        }

        public Builder addInitials(final String val) {
            this.mInitials = val;
            return this;
        }

        public Builder addPreview(final String val) {
            this.mPreview = val;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    private Message(final Builder builder) {
        this.mContactName = builder.mFirstName + " " + builder.mLastName;
        this.mInitials = builder.mInitials;
        this.mPreview = builder.mPreview;
    }

    public String getContactName() {
        return mContactName;
    }

    public String getInitials() {
        return mInitials;
    }

    public String getPreview() {
        return mPreview;
    }

}
