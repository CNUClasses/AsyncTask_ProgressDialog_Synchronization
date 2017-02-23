package com.example.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AsyncTaskActivity extends Activity {

    private static final String TAG = "AsyncTaskActivity";
    private static final int P_BAR_MAX = 100;
    private static final boolean DO_PROGRESS_DIALOG = true;
    Button bStart;
    TextView textViewMessage;
    ProgressBar pBar;
    private UpdateTask myUpdateTask = null;
    ProgressDialog myProgressDialog;
    boolean bUIEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);

        bStart = (Button) findViewById(R.id.buttonStart);
        textViewMessage = (TextView) findViewById(R.id.textView2);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);

        //what is the max value
        pBar.setMax(P_BAR_MAX);

        //if there is a bundle then get UI state
        if (savedInstanceState != null)
            bUIEnabled = savedInstanceState.getBoolean("Start_State", true);

        if (bUIEnabled == false)
            //lets see if the device rotated and we need to regrab it
            myUpdateTask = (UpdateTask) getLastNonConfigurationInstance();

            //if a thread was retained then grab it
            if (myUpdateTask != null) {
                myUpdateTask.attach(this);
                pBar.setProgress(myUpdateTask.progress);
                doProgressDialog(DO_PROGRESS_DIALOG);
            }
        }


    /**
     * Called to retrieve per-instance state from an activity before being killed
     * so that the state can be restored in {@link #onCreate} or
     * {@link #onRestoreInstanceState} (the {@link android.os.Bundle} populated by this method
     * will be passed to both).
     * <p/>
     * <p>This method is called before an activity may be killed so that when it
     * comes back some time in the future it can restore its state.  For example,
     * if activity B is launched in front of activity A, and at some point activity
     * A is killed to reclaim resources, activity A will have a chance to save the
     * current state of its user interface via this method so that when the user
     * returns to activity A, the state of the user interface can be restored
     * via {@link #onCreate} or {@link #onRestoreInstanceState}.
     * <p/>
     * <p>Do not confuse this method with activity lifecycle callbacks such as
     * {@link #onPause}, which is always called when an activity is being placed
     * in the background or on its way to destruction, or {@link #onStop} which
     * is called before destruction.  One example of when {@link #onPause} and
     * {@link #onStop} is called and not this method is when a user navigates back
     * from activity B to activity A: there is no need to call {@link #onSaveInstanceState}
     * on B because that particular instance will never be restored, so the
     * system avoids calling it.  An example when {@link #onPause} is called and
     * not {@link #onSaveInstanceState} is when activity B is launched in front of activity A:
     * the system may avoid calling {@link #onSaveInstanceState} on activity A if it isn't
     * killed during the lifetime of B since the state of the user interface of
     * A will stay intact.
     * <p/>
     * <p>The default implementation takes care of most of the UI per-instance
     * state for you by calling {@link android.view.View#onSaveInstanceState()} on each
     * view in the hierarchy that has an id, and by saving the id of the currently
     * focused view (all of which is restored by the default implementation of
     * {@link #onRestoreInstanceState}).  If you override this method to save additional
     * information not captured by each individual view, you will likely want to
     * call through to the default implementation, otherwise be prepared to save
     * all of the state of each view yourself.
     * <p/>
     * <p>If called, this method will occur before {@link #onStop}.  There are
     * no guarantees about whether it will occur before or after {@link #onPause}.
     *
     * @param outState Bundle in which to place your saved state.
     * @see #onCreate
     * @see #onRestoreInstanceState
     * @see #onPause
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("Start_State", bStart.isEnabled());
    }

    /**
     * Called by the system, as part of destroying an
     * activity due to a configuration change, when it is known that a new
     * instance will immediately be created for the new configuration.  You
     * can return any object you like here, including the activity instance
     * itself, which can later be retrieved by calling
     * {@link #getLastNonConfigurationInstance()} in the new activity
     * instance.
     * <p/>
     * <em>If you are targeting {@link android.os.Build.VERSION_CODES#HONEYCOMB}
     * or later, consider instead using a {@link Fragment} with
     * {@link Fragment#setRetainInstance(boolean)
     * Fragment.setRetainInstance(boolean}.</em>
     * <p/>
     * <p>This function is called purely as an optimization, and you must
     * not rely on it being called.  When it is called, a number of guarantees
     * will be made to help optimize configuration switching:
     * <ul>
     * <li> The function will be called between {@link #onStop} and
     * {@link #onDestroy}.
     * <li> A new instance of the activity will <em>always</em> be immediately
     * created after this one's {@link #onDestroy()} is called.  In particular,
     * <em>no</em> messages will be dispatched during this time (when the returned
     * object does not have an activity to be associated with).
     * <li> The object you return here will <em>always</em> be available from
     * the {@link #getLastNonConfigurationInstance()} method of the following
     * activity instance as described there.
     * </ul>
     * <p/>
     * <p>These guarantees are designed so that an activity can use this API
     * to propagate extensive state from the old to new activity instance, from
     * loaded bitmaps, to network connections, to evenly actively running
     * threads.  Note that you should <em>not</em> propagate any data that
     * may change based on the configuration, including any data loaded from
     * resources such as strings, layouts, or drawables.
     * <p/>
     * <p>The guarantee of no message handling during the switch to the next
     * activity simplifies use with active objects.  For example if your retained
     * state is an {@link android.os.AsyncTask} you are guaranteed that its
     * call back functions (like {@link android.os.AsyncTask#onPostExecute}) will
     * not be called from the call here until you execute the next instance's
     * {@link #onCreate(android.os.Bundle)}.  (Note however that there is of course no such
     * guarantee for {@link android.os.AsyncTask#doInBackground} since that is
     * running in a separate thread.)
     *
     * @return Return any Object holding the desired state to propagate to the
     * next activity instance.
     * @deprecated Use the new {@link Fragment} API
     * {@link Fragment#setRetainInstance(boolean)} instead; this is also
     * available on older platforms through the Android compatibility package.
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        if (myUpdateTask != null) {
            Log.d(TAG, "onRetainNonConfigurationInstance");
            myUpdateTask.detach();
            if (myProgressDialog != null)
                myProgressDialog.dismiss();
            myProgressDialog = null;
            return (myUpdateTask);
        } else
            return super.onRetainNonConfigurationInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_async_task, menu);
        return true;
    }

    // personal asynctask============

    // start thread
    public void doStart(View v) {
        doProgressDialog(DO_PROGRESS_DIALOG);

        bStart.setEnabled(false);

        textViewMessage.setText("Working...");

        //create and start thread
        myUpdateTask = new UpdateTask(this);
        myUpdateTask.execute();
    }

    private void doProgressDialog(boolean bDoProgress) {
        if (!bDoProgress)
            return;

        myProgressDialog = new ProgressDialog(AsyncTaskActivity.this);
        myProgressDialog.setTitle("Please wait");
        myProgressDialog.setMessage("Notice user cannot interact with rest of UI");
        myProgressDialog.setCancelable(false);
        myProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                doStop();
                myProgressDialog = null;
            }
        });
        myProgressDialog.show();
    }

    //try to cancel thread
    public void doStop() {
        myUpdateTask.cancel(true);
        bStart.setEnabled(true);

        textViewMessage.setText("Stopping...");
        pBar.setProgress(0);
    }



    //****************************************
    //notice I only define what will be returned to
    //onCanceled or onPostExecute
    //notice also that this is static, so it does not hold an implicit reference to enclosing
    //activity, rotate the phone and activity is GCed
    private static class UpdateTask extends AsyncTask<Void, Integer, String> {
        private static final String TAG = "UpdateTask";
        int progress = 1;
        private AsyncTaskActivity activity = null;

        // ===========================================
        // important do not hold a reference so garbage collector can grab old
        // defunct dying activity
        void detach() {
            activity = null;
            Log.d(TAG, "DETACHING");
        }

        public UpdateTask(AsyncTaskActivity activity) {
            attach(activity);
            Log.d(TAG, "          ATACHING");

        }

        // grab a reference to this activity, mindful of leaks
        void attach(AsyncTaskActivity activity) {
            if (activity == null)
                throw new IllegalArgumentException("Activity cannot be null");

            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //do whatever setup you need here
        }

        /**
         * this is done in a new thread
         */
        @Override
        protected String doInBackground(Void... params) {
            for (int i = 1; i <= 10; i++) {
                //simulate some work sleep for .5 seconds
                SystemClock.sleep(500);

                //let main thread know we are busy
                //notice that we are autoboxing int to Integer
                publishProgress(i);

                //periodically check if the user canceled
                if (isCancelled())
                    return ("Canceled");
            }
            return "Finished";
        }

        /**
         * the following run on UI thread
         */
        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);

            //indicate how far we have gone
            progress = value[0] * 10;
            if (activity != null)
                activity.pBar.setProgress(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            setUIState(result);
        }
        @Override
        protected void onCancelled(String result) {
            super.onCancelled(result);

            setUIState(result);
        }
        private void setUIState(String result) {
            try {
                if (activity != null) {
                    activity.textViewMessage.setText(result);
                    activity.bStart.setEnabled(true);
                    //get rid of dialog
                    if (activity.myProgressDialog != null)
                        activity.myProgressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
