package com.example.dell.mmb;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineFragment extends Fragment implements
        LoaderCallbacks<Cursor> {
    private static final String TAG = TimelineFragment.class.getSimpleName();
    static final int START_LOADER = 47;
    android.support.v4.widget.SimpleCursorAdapter adapter;
    ListView listView;
    Cursor cursor;
    TimeLineReceiver receiver;
    static final String[] from = {DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT};
    static final int[] to = {R.id.createdAtTv, R.id.userTv, R.id.textTv,};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_time_line, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cursor = getActivity().getContentResolver().query(StatusProvider.CONTENT_URI, null, null, null,
                DbHelper.C_CREATED_AT + " DESC");
        Log.v("cursor length: ", String.valueOf(cursor.getCount()));
        adapter = new android.support.v4.widget.SimpleCursorAdapter(getActivity(), R.layout.list_item, cursor, from, to);
        adapter.setViewBinder(viewBinder);
        listView.setAdapter(adapter);
    }


    android.support.v4.widget.SimpleCursorAdapter.ViewBinder viewBinder = new android.support.v4.widget.SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() != R.id.createdAtTv)
                return false;
            long time = cursor.getLong(cursor.getColumnIndex(DbHelper.C_CREATED_AT));
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(time);
            ((TextView) view).setText(relativeTime);
            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (receiver == null) {
            receiver = new TimeLineReceiver();
        }
        getActivity().registerReceiver(receiver, new IntentFilter(MyApplication.ACTION_NEW_STATUS));
        Log.v(TAG, "TimeLineActivity onResume called");
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        Log.v(TAG, "TimeLineActivity onPause called");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), StatusProvider.CONTENT_URI, null, null, null,
                DbHelper.C_CREATED_AT + " DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(cursor);

    }

    class TimeLineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //getLoaderManager().restartLoader(START_LOADER, null, TimeLineActivity.this);
            cursor = getActivity().getContentResolver().query(StatusProvider.CONTENT_URI, null, null, null,
                    DbHelper.C_CREATED_AT + " DESC");
            adapter.changeCursor(cursor);
            int count = intent.getIntExtra("counter", 0);
            Log.v(TAG, "TimeLineReceiver called & count: " + count);
            String text = intent.getStringExtra(DbHelper.C_USER) + ": "
                    + intent.getStringExtra(DbHelper.C_TEXT);

            // Create the notification object
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), 0);

            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle("New Status!").setContentText(text)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);
            Notification notification = builder.getNotification();

            // Post the notification
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(0, notification);

            Log.d("NewStatusReceiver", "onReceive with text: " + text);

        }
    }
}
