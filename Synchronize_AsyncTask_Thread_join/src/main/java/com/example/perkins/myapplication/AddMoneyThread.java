package com.example.perkins.myapplication;

/**
 * Created by Perkins on 2/19/2015.
 */
public class AddMoneyThread extends Thread {
private BankAccount myAccount;
    private long totalAdds;
    /**
     * Constructs a new {@code Thread} with no {@code Runnable} object and a
     * newly generated name. The new {@code Thread} will belong to the same
     * {@code ThreadGroup} as the {@code Thread} calling this constructor.
     *
     * @see ThreadGroup
     * @see Runnable
     */
    public AddMoneyThread(BankAccount myAccount, long totalAdds) {
        this.myAccount = myAccount;
        this.totalAdds = totalAdds;
    }

    public void run(){
        for (int i = 0; i < totalAdds; i++) {
            myAccount.deposit(1.0);
        }
    }
}