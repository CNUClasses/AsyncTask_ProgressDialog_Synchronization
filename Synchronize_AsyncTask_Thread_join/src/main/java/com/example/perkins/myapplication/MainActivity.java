package com.example.perkins.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static final  int HALF_TOTAL_ADDS = 50000;
    BankAccount myAccount;

    TextView myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myText = (TextView)findViewById(R.id.textViewValue);

        myAccount = new BankAccount();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void doAddMoney(View view) {
        myAccount.setAccountBalance(0.0);
        myText.setText(String.valueOf(myAccount.getAccountBalance()));

        AddMoneyTask myTask = new AddMoneyTask(this);
        myTask.execute(HALF_TOTAL_ADDS);

        for (int i = 0; i < HALF_TOTAL_ADDS; i++)
            myAccount.deposit(1.0);

        //wait for async task to finish
        if(myTask.bFinished == true)
            myText.setText(String.valueOf(myAccount.getAccountBalance()));
    }

    public void doAddMoneyThread(View view) {
        myAccount.setAccountBalance(0.0);
        myText.setText(String.valueOf(myAccount.getAccountBalance()));

        AddMoneyThread myThread = new AddMoneyThread(myAccount,HALF_TOTAL_ADDS);
        myThread.start();

        for (int i = 0; i < HALF_TOTAL_ADDS; i++) {
            myAccount.deposit(1.0);
        }

        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        myText.setText(String.valueOf(myAccount.getAccountBalance()));
    }

    //****************************************
    //notice I only define what will be returned to
    //onCanceled or onPostExecute
    //notice also that this is static, so it does not hold an implicit referenidce to enclosing
    //activity, rotate the phone and activity is GCed
    private static class AddMoneyTask extends AsyncTask<Integer, Void, Void> {
        public boolean bFinished =false;
        private static final String TAG = "AddMoneyTask";
         private MainActivity activity = null;

        // ===========================================
        // important do not hold a reference so garbage collector can grab old
        // defunct dying activity
        void detach() {
            activity = null;
            Log.d(TAG, "DETACHING");
        }

        public AddMoneyTask(MainActivity activity) {
            attach(activity);
            Log.d(TAG, "          ATACHING");

        }

        // grab a reference to this activity, mindful of leaks
        void attach(MainActivity activity) {
            if (activity == null)
                throw new IllegalArgumentException("Activity cannot be null");

            this.activity = activity;
        }


        /**
         * this is done in a new thread
         */
        @Override
        protected Void doInBackground(Integer... params) {
            for (int i = 0; i < params[0]; i++) {
                activity.myAccount.deposit(1.0);
             }
            return null;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param aVoid The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bFinished=true;
            activity.myText.setText(String.valueOf(activity.myAccount.getAccountBalance()));

        }

    }

}
