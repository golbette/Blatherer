package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class Request implements Serializable {
    private final String mContactName;
    private final String mInitials;
    private final String mRequestType;

    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private String mInitials = "";
        private String mRequestType = "";

        public Builder(String firstName, String lastName) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
        }

        public Builder addInitials(final String val) {
            this.mInitials = val;
            return this;
        }

        public Builder addRequestType(final String val) {
            this.mRequestType = val;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }

    private Request(final Builder builder) {
        this.mContactName = builder.mFirstName + " " + builder.mLastName;
        this.mInitials = builder.mInitials;
        this.mRequestType = builder.mRequestType;
    }

    public String getContactName() {
        return mContactName;
    }

    public String getInitials() {
        return mInitials;
    }

    public String getmRequestType() {
        return mRequestType;
    }
}
