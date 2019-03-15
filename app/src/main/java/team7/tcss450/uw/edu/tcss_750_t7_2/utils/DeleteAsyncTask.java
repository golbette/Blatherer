package team7.tcss450.uw.edu.tcss_750_t7_2.utils;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Delete async task class for backend
 * Code is adopted from Charles
 */
public class DeleteAsyncTask extends AsyncTask<Void, String, String> {

    private final String mUrl;
    private final JSONObject mJsonMsg;

    private Runnable mOnPre;
    private Consumer<String[]> mOnProgress;
    private Consumer<String> mOnPost;
    private Consumer<String> mOnCancel;
    private final Map<String, String> mHeaders;

    /**
     * Helper class for building DeleteAsyncTask
     */
    public static class Builder {

        //Required Parameters
        private final String mUrl;
        private final JSONObject mJsonMsg;

        //Optional Parameters
        private Runnable onPre = () -> {};
        private Consumer<String[]> onProg = X -> {};
        private Consumer<String> onPost = x -> {};
        private Consumer<String> onCancel = x -> {};
        private Map<String, String> headers;

        /**
         * Constructs a new Builder.
         *
         * @param url the fully-formed url of the web service this task will connect to
         * @param json the JSON message to send
         */
        public Builder(final String url, final JSONObject json) {
            mUrl = url;
            mJsonMsg = json;
            headers = new HashMap<>();
        }

        /**
         * Set the action to perform during AsyncTask onPreExecute.
         *
         * @param val a action to perform during AsyncTask onPreExecute
         * @return
         */
        public Builder onPreExecute(final Runnable val) {
            onPre = val;
            return this;
        }

        /**
         * Set the action to perform during AsyncTask onProgressUpdate. An action for
         * onProgressUpdate is included but a call to publishProgress is never made in
         * doInBackground rendering onProgressUpdate unused.
         *
         * @param val a action to perform during AsyncTask onProgressUpdate
         * @return
         */
        public Builder onProgressUpdate(final Consumer<String[]> val) {
            onProg = val;
            return this;
        }

        /**
         * Set the action to perform during AsyncTask onPostExecute.
         *
         * @param val a action to perform during AsyncTask onPostExecute
         * @return
         */
        public Builder onPostExecute(final Consumer<String> val) {
            onPost = val;
            return this;
        }

        /**
         * Set the action to perform during AsyncTask onCancelled. The AsyncTask method cancel() is
         * called in doInBackGround during exception handling. Use this action to respond to
         * exceptional situations resulting from doInBackground execution. Note that external
         * cancellation will cause this action to execute.
         *
         * @param val a action to perform during AsyncTask onCancelled
         * @return
         */
        public Builder onCancelled(final Consumer<String> val) {
            onCancel = val;
            return this;
        }

        /**
         * Add a Key/Value pair to be set in the Header of the HTTP request.
         * @param key the key of the pair
         * @param value the vaue of the pair
         * @return
         */
        public Builder addHeaderField(final String key, final String value) {
            headers.put(key, value);
            return this;
        }

        /**
         * Constructs a DeletePostAsyncTask with the current attributes.
         *
         * @return a DeletePostAsyncTask with the current attributes
         */
        public DeleteAsyncTask build() {
            return new DeleteAsyncTask(this);
        }

    }

    /**
     * Constructs a DeletePostAsyncTask internally from a builder
     * @param builder the builder used to construct this object
     */
    private DeleteAsyncTask(final Builder builder) {
        mUrl = builder.mUrl;
        mJsonMsg = builder.mJsonMsg;

        mOnPre = builder.onPre;
        mOnProgress = builder.onProg;
        mOnPost = builder.onPost;
        mOnCancel = builder.onCancel;
        mHeaders = builder.headers;
    }

    /**
     * Override pre execute
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mOnPre.run();
    }

    /**
     * Override do in background for delete task
     * @param voids void arguments
     * @return string of response
     */
    @Override
    protected String doInBackground(Void... voids) {

        StringBuilder response = new StringBuilder();
        HttpURLConnection urlConnection = null;

        try {
            URL urlObject = new URL(mUrl);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            for (final String key: mHeaders.keySet()) {
                urlConnection.setRequestProperty(key, mHeaders.get(key));
            }

            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

            wr.write(mJsonMsg.toString());
            wr.flush();
            wr.close();

            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while((s = buffer.readLine()) != null) {
                response.append(s);
            }

            publishProgress();
        } catch (Exception e) {
            response = new StringBuilder("Unable to connect, Reason: "
                    + e.getMessage());
            cancel(true);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }


        return response.toString();
    }

    /**
     * Handle on cancelled
     * @param result string
     */
    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
        mOnCancel.accept(result);
    }

    /**
     * Handle on progress update
     * @param values vof type string
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mOnProgress.accept(values);

    }

    /**
     * Handle on post execute
     * @param result of type string
     */
    @Override
    protected void onPostExecute(String result) {
        mOnPost.accept(result);
    }
}
