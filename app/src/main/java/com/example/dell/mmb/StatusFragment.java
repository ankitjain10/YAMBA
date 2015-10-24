package com.example.dell.mmb;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;


public class StatusFragment extends Fragment implements View.OnClickListener {
    private static final int MAX_LENGTH = 140;
    private TextView statusText, counterText;
    private Button updateButton;
    private int defaultColor;
    SharedPreferences prefs;
    ProgressDialog mProgressDialog;
    Twitter twitter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Loading...");
        statusText.addTextChangedListener(new MyTextWatcher());
        counterText.setText(Integer.toString(MAX_LENGTH - statusText.length()));
        defaultColor = counterText.getTextColors().getDefaultColor();
        updateButton.setOnClickListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        // Find the views
        statusText = (EditText) view.findViewById(R.id.etInput);
        counterText = (TextView) view.findViewById(R.id.textCounter);
        updateButton = (Button) view.findViewById(R.id.btnUpdate);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called when update button is clicked on.
     */
    public void onClick(View v) {
        final String inputText = statusText.getText().toString();
        new postToTwitter().execute(inputText);
        //mProgressDialog.show();
        statusText.setText("");
    }

    /**
     * Posts the status update in a separate task.
     */

    class postToTwitter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                /*MyApplication myApplication = new MyApplication();
                myApplication.getTwitter().setStatus(params[0]);
                */
                String username = prefs.getString(getString(R.string.username_pref_key),
                        getString(R.string.username_default));
                String password = prefs.getString(getString(R.string.password_pref_key)
                        , getString(R.string.password_default));
                String server = prefs.getString(getString(R.string.server_pref_key)
                        , getString(R.string.server_default));
                String delay = prefs.getString(getString(R.string.delay_pref_key),
                        getString(R.string.delay_default));
                try {
                    twitter = new Twitter(username, password);
                    twitter.setAPIRootUrl(server);
                    twitter.setStatus(params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                //   ((MyApplication) getActivity()).getTwitter().setStatus(params[0]);
                return "Successfully posted " + params[0];
            } catch (TwitterException e) {
                e.printStackTrace();
                return "Failed to post due to " + e;
            } catch (IllegalArgumentException i) {
                i.printStackTrace();
                return "Failed to post due to " + i;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //mProgressDialog.cancel();
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }

    // --- Part of being TextWatcher --- //
    class MyTextWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            int count = MAX_LENGTH - s.length();
            counterText.setText(Integer.toString(count));

            // Change the color
            if (count < 30) {
                counterText.setTextColor(Color.RED);
                counterText.setTextScaleX(2);
            } else {
                counterText.setTextColor(defaultColor);
                counterText.setTextScaleX(1);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }
    }
}