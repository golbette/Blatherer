package team7.tcss450.uw.edu.tcss_750_t7_2.weather;

import java.io.Serializable;

/**
 * Builder design class for saved locations
 */
public class SavedLocations implements Serializable {

    private final String mNickname;

    private final String mLat;

    private final String mLong;

    private final String mZip;

    /**
     * Public class to create saved location object
     */
    public static class Builder {

        private final String mNickname;

        private final String mLat;

        private final String mLong;

        private final String mZip;

        /**
         * Public constructor for builder class
         * @param nickname to be seen for user
         * @param lat latitude of location saved
         * @param lng longitude of location  saved
         * @param zip zip code of location saved
         */
        public Builder(String nickname, String lat, String lng, String zip) {
            mNickname = nickname;
            mLat = lat;
            mLong = lng;
            mZip = zip;
        }

        /**
         * Creates SavedLocation object
         * @return the saved location object
         */
        public SavedLocations build() {
            return new SavedLocations(this);
        }
    }

    /**
     * Private constructor called from builder class
     * @param builder from builder object
     */
    private SavedLocations(final Builder builder) {
        this.mNickname = builder.mNickname;
        this.mLat = builder.mLat;
        this.mLong = builder.mLong;
        this.mZip = builder.mZip;
    }

    /**
     * Gets nickname of saved location
     * @return nickname
     */
    public String getNickname() {
        return mNickname;
    }

    /**
     * Gets latitude of saved location
     * @return latitude
     */
    public String getLat() {
        return mLat;
    }

    /**
     * Gets longitude of saved location
     * @return longitude
     */
    public String getLong() {
        return mLong;
    }

    /**
     * Gets zip code of saved location
     * @return zip code
     */
    public String getZip() {
        return mZip;
    }
}
