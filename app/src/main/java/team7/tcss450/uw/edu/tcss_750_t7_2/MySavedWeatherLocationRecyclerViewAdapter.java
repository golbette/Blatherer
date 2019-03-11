package team7.tcss450.uw.edu.tcss_750_t7_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import team7.tcss450.uw.edu.tcss_750_t7_2.model.Credentials;
import team7.tcss450.uw.edu.tcss_750_t7_2.utils.DeleteAsyncTask;
import team7.tcss450.uw.edu.tcss_750_t7_2.weather.SavedLocations;

public class MySavedWeatherLocationRecyclerViewAdapter extends RecyclerView.Adapter<MySavedWeatherLocationRecyclerViewAdapter.ViewHolder> {

    private final List<SavedLocations> mValues;

    private final Context mContext;

    private final Credentials mCredentials;


    public MySavedWeatherLocationRecyclerViewAdapter(List<SavedLocations> items, Context fragment, Credentials credentials) {
        mValues = items;
        mContext = fragment;
        mCredentials = credentials;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_saved_weather_location, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Log.e("test2", mValues.get(position).getNickname());
        //Set text of all the previously saved locations here
        holder.mLocation.setText(mValues.get(position).getNickname());
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
               builder.setMessage("Delete " + mValues.get(position).getNickname() + "?");
               builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // call http delete here

                       Uri uri = new Uri.Builder()
                               .scheme("https")
                               .appendPath(v.getContext().getResources().getString(R.string.ep_base_url))
                               .appendPath(v.getContext().getResources().getString(R.string.ep_weather))
                               .appendPath(v.getContext().getResources().getString(R.string.ep_location))
                               .appendQueryParameter("username", mCredentials.getUsername())
                               .appendQueryParameter("nickname", mValues.get(position).getNickname())
                               .build();
                       Log.e("url", uri.toString());
                       new DeleteAsyncTask.Builder(uri.toString(), new JSONObject())
                               .onPostExecute(MySavedWeatherLocationRecyclerViewAdapter.this::handleDeleteOnPost)
                               .build().execute();
                   }
               });
               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // cancel
                   }
               });
               builder.show();
            }
        });
    }

    private void handleDeleteOnPost(final String result) {
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has("success")) {
                boolean isDeleted = root.getBoolean("success");
                if (isDeleted) {
                    Log.e("position", root.getString("nickname"));
                    String nickname = root.getString("nickname");
                    int position = 0;
                    for (position = 0; position < mValues.size(); position++) {
                        if (mValues.get(position).getNickname().equals(nickname)) {
                            break;
                        }
                    }

                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());

                    Toast toast = Toast.makeText(mContext, "Removed " + nickname, Toast.LENGTH_LONG);
                    toast.show();

                }
            }
            Log.e("result", root.toString());
        } catch (JSONException error) {
            Log.e("error", error.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLocation;
        public final Button mDeleteButton;
        public SavedLocations mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLocation = (TextView) view.findViewById(R.id.saved_weather_location_name);
            mDeleteButton = (Button) view.findViewById(R.id.saved_location_delete_button);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


}
