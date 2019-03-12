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

import team7.tcss450.uw.edu.tcss_750_t7_2.weather.TenDayWeather;

public class MyTenDayRecyclerViewAdapter extends RecyclerView.Adapter<MyTenDayRecyclerViewAdapter.ViewHolder> {

    private final List<TenDayWeather> mValues;

    public MyTenDayRecyclerViewAdapter(List<TenDayWeather> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ten_day_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        //Set text of individual day of the ten day forecast here

        holder.mTemp.setText(mValues.get(position).getTemp());

        int timeStamp = Integer.parseInt(mValues.get(position).getDate());
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d");
        simpleDateFormat.setTimeZone(cal.getTimeZone());
        holder.mDate.setText(simpleDateFormat.format(cal.getTime())
                + "\n"
                + mValues.get(position).getWeatherText());

        if (contains(TodayWeatherFragment.CLEAR_DAY, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.clear_day);
        } else if (contains(TodayWeatherFragment.CLEAR_NIGHT, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.clear_night);
        } else if(contains(TodayWeatherFragment.CLOUDY, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.cloudy);
        } else if (contains(TodayWeatherFragment.CLOUDY_NIGHT, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.cloudy_night);
        } else if (contains(TodayWeatherFragment.FOG, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.fog);
        } else if (contains(TodayWeatherFragment.PARTLY_CLOUDY, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.partly_cloudy);
        } else if (contains(TodayWeatherFragment.RAIN, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.rain);
        } else if (contains(TodayWeatherFragment.SLEET, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.sleet);
        } else if (contains(TodayWeatherFragment.SNOW, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.snow);
        } else if (contains(TodayWeatherFragment.SUNNY, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.sunny);
        } else if (contains(TodayWeatherFragment.WIND, mValues.get(position).getWeatherCode())) {
            holder.mWeatherIcon.setImageResource(R.drawable.wind);
        }

    }

    private boolean contains(final int[] array, final int target) {
        boolean result = false;
        for (int i : array) {
            if (i == target) {
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
        public TenDayWeather mItem;

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
