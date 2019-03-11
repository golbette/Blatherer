package team7.tcss450.uw.edu.tcss_750_t7_2.weather;

import java.io.Serializable;

public class SavedLocations implements Serializable {

    private final String mNickname;

    private final String mLat;

    private final String mLong;

    private final String mZip;

    public static class Builder {

        private final String mNickname;

        private final String mLat;

        private final String mLong;

        private final String mZip;

        public Builder(String nickname, String lat, String lng, String zip) {
            mNickname = nickname;
            mLat = lat;
            mLong = lng;
            mZip = zip;
        }

        public SavedLocations build() {
            return new SavedLocations(this);
        }
    }

    private SavedLocations(final Builder builder) {
        this.mNickname = builder.mNickname;
        this.mLat = builder.mLat;
        this.mLong = builder.mLong;
        this.mZip = builder.mZip;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getLat() {
        return mLat;
    }

    public String getLong() {
        return mLong;
    }

    public String getZip() {
        return mZip;
    }
}
