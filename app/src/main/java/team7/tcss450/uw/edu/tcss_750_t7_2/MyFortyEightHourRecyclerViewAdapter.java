package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;

public class MyFortyEightHourRecyclerViewAdapter extends RecyclerView.Adapter<MyFortyEightHourRecyclerViewAdapter.ViewHolder> {

    private final List<FortyEightHourWeather> mValues;

    public MyFortyEightHourRecyclerViewAdapter(List<FortyEightHourWeather> items) {
        mValues = items;
    }

    @Override
    public MyFortyEightHourRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_forty_eight_hour_weather, parent, false);
        return new MyFortyEightHourRecyclerViewAdapter.ViewHolder(view);
    }

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
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDate;
        public final TextView mTemp;
        public final ImageView mWeatherIcon;
        public FortyEightHourWeather mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDate = (TextView) view.findViewById(R.id.weather_date);
            mWeatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
            mTemp = (TextView) view.findViewById(R.id.weather_temp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTemp.getText() + "'";
        }
    }
}
