package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class Message implements Serializable {
    private final String mUsername;
    private final String mMessage;
    private final String mTimestamp;

    public static class Builder {
        private final String mUsername;
        private final String mMessage;
        private final String mTimestamp;
//                mFirstName
//                mLastName
//        private String mInitials = "";
//        private String mPreview = "";
//        private String mUsername ="";
//        private String mMessage = "";

        public Builder(String username, String message, String timestamp) {
            this.mUsername = username;
            this.mMessage = message;
            this.mTimestamp = timestamp;
        }

//        public Builder addInitials(final String val) {
//            this.mInitials = val;
//            return this;
//        }
//
//        public Builder addPreview(final String val) {
//            this.mPreview = val;
//            return this;
//        }
//
//        public Builder addUsername(final String val) {
//            this.mUsername = val;
//            return this;
//        }

        public Message build() {
            return new Message(this);
        }
    }

    private Message(final Builder builder) {
//        this.mContactName = builder.mFirstName + " " + builder.mLastName;
//        String initials = builder.mFirstName.substring(0, 1) + builder.mLastName.substring(0, 1) + "";
//        this.mInitials = initials.toUpperCase();
//        this.mPreview = builder.mPreview;
        this.mUsername = builder.mUsername;
        this.mMessage = builder.mMessage;
        this.mTimestamp = builder.mTimestamp;
    }

//    public String getContactName() {
//        return mContactName;
//    }
//
//    public String getInitials() {
//        return mInitials;
//    }
//
//    public String getPreview() {
//        return mPreview;
//    }

    public String getUsername() {
        return mUsername;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

}
