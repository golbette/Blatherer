package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import team7.tcss450.uw.edu.tcss_750_t7_2.weather.FortyEightHourWeather;

/**
 * Recycler view class for saved weather location
 */
public class MyFortyEightHourRecyclerViewAdapter extends RecyclerView.Adapter<MyFortyEightHourRecyclerViewAdapter.ViewHolder> {

    private final List<FortyEightHourWeather> mValues;

    /**
     * Constructor for 48 hour adapter
     * @param items of hourly forecast
     */
    public MyFortyEightHourRecyclerViewAdapter(List<FortyEightHourWeather> items) {
        mValues = items;
    }

    /**
     * Creates view holder
     * @param parent view group parent
     * @param viewType int of view type
     * @return viewHolder object
     */
    @Override
    public MyFortyEightHourRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_forty_eight_hour_weather, parent, false);
        return new MyFortyEightHourRecyclerViewAdapter.ViewHolder(view);
    }

    /**
     * Binds view
     * @param holder view holder
     * @param position int position
     */
    @Override
    public void onBindViewHolder(final MyFortyEightHourRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        int tempRound = (int) Math.round(Double.parseDouble(mValues.get(position).getTemp()));
        holder.mTemp.setText(tempRound + "\u00B0");

        int timeStamp = Integer.parseInt(mValues.get(position).getDate());
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh a, EEEE");
        simpleDateFormat.setTimeZone(cal.getTimeZone());
        holder.mDate.setText(simpleDateFormat.format(cal.getTime())
                + "\n"
                + mValues.get(position).getWeatherText());

        if (contains(TodayWeatherFragment.CLEAR_DAY_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.clear_day);
        } else if (contains(TodayWeatherFragment.CLEAR_NIGHT_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.clear_night);
        } else if(contains(TodayWeatherFragment.CLOUDY_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.cloudy);
        } else if (contains(TodayWeatherFragment.CLOUDY_NIGHT_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.cloudy_night);
        } else if (contains(TodayWeatherFragment.FOG_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.fog);
        } else if (contains(TodayWeatherFragment.PARTLY_CLOUDY_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.partly_cloudy);
        } else if (contains(TodayWeatherFragment.RAIN_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.rain);
        } else if (contains(TodayWeatherFragment.SLEET_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.sleet);
        } else if (contains(TodayWeatherFragment.SNOW_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.snow);
        } else if (contains(TodayWeatherFragment.SUNNY_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.sunny);
        } else if (contains(TodayWeatherFragment.WIND_STRING, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.wind);
        }
    }

    /**
     * Searches array for target
     * @param array to be searched
     * @param target to be found
     * @return boolean if target is found
     */
    private boolean contains(final String[] array, final String target) {
        boolean result = false;
        for (String i : array) {
            if (i.equals(target)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Retrieves item count
     * @return item size
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Class extending recycler view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDate;
        public final TextView mTemp;
        public final ImageView mWeatherIcon;
        public FortyEightHourWeather mItem;

        /**
         * ViewHolder constructor
         * @param view of view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDate = (TextView) view.findViewById(R.id.weather_date);
            mWeatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
            mTemp = (TextView) view.findViewById(R.id.weather_temp);
        }

        /**
         * Override to string
         * @return string of temp
         */
        @Override
        public String toString() {
            return super.toString() + " '" + mTemp.getText() + "'";
        }
    }
}
