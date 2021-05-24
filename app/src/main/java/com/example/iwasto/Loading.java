package com.example.iwasto;
import android.app.ProgressDialog;
import android.content.Context;

public class Loading {
    public ProgressDialog loadDialog;
    public String messageTitle ="Few more seconds";
    public Loading(Context context, String message) {
        loadDialog =  new ProgressDialog(context);
        loadDialog.setTitle(messageTitle);
        loadDialog.setMessage(message);
        loadDialog.setCancelable(false);
    }

    public ProgressDialog getLoadDialog() {
        return loadDialog;
    }

    public void setLoadDialog(ProgressDialog loadDialog) {
        this.loadDialog = loadDialog;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }


}
