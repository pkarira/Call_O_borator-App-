package com.android.internal.telephony;

/**
 * Created by pulkit on 9/10/17.
 */

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}
