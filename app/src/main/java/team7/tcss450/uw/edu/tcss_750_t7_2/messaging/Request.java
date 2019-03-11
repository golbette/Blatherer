package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

import java.io.Serializable;

public class Request implements Serializable {
    private final String mContactName;
    private final String mInitials;
    private final String mRequestType;
    private final String mMemberId_b;
    private final String mMemberId_a;
    private final String mOtherEmail;


    public static class Builder {
        private String mOtherEmail;
        private final String mFirstName;
        private final String mLastName;
        private String mInitials = "";
        private String mRequestType = "";
        private  String mMemberId_b;
        private  String mMemberId_a;


        public Builder( String otherEmail, String firstName, String lastName, String memberId_b, String memberId_a) {
            this.mOtherEmail = otherEmail;
            this.mFirstName = firstName;
            this.mLastName = lastName;
            this.mMemberId_b = memberId_b;
            this.mMemberId_a  = memberId_a;

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
        String initials = builder.mFirstName.charAt(0) + builder.mLastName.charAt(0) + "";
        this.mInitials = initials.toUpperCase();
        this.mRequestType = builder.mRequestType;
        this.mMemberId_b = builder.mMemberId_b;
        this.mMemberId_a = builder.mMemberId_a;
        this.mOtherEmail = builder.mOtherEmail;
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

    public String getmMemberId_b(){return mMemberId_b;}

    public String getmMemberId_a(){return mMemberId_a;}

    public String getmOtherEmail(){ return mOtherEmail;}
}
