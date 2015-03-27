package com.sjtu.icarer.events;

import retrofit.RetrofitError;

/**
 * The event that is posted when a network error event occurs.
 * TODO: Consume this event in the {@link com.com.sjtu.icarer.ui.HomeActivity} and
 * show a dialog that something went wrong.
 */
public class NetworkErrorEvent {
    private RetrofitError cause;

    public NetworkErrorEvent(RetrofitError cause) {
        this.cause = cause;
    }

    public RetrofitError getCause() {
        return cause;
    }
}
