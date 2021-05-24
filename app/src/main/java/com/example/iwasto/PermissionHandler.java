package com.example.iwasto;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PermissionHandler {

    Boolean checkCameraPerm = false, checkStoragePermRead = false,checkStoragePermWrite = false,
            checkLocationPermCoarse = false, checkContactsRead = false;
    Boolean checkaudioperm = false, CALL_PHONE_PERM = false;
    public Boolean requestAudio(final Activity activity){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        checkaudioperm = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            checkaudioperm = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return checkaudioperm;
    }

    public Boolean requestCall(final Activity activity){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        CALL_PHONE_PERM = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            CALL_PHONE_PERM = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return CALL_PHONE_PERM;
    }


    public boolean checkCall(Activity activity){
        requestCall(activity);
        if(!CALL_PHONE_PERM)
            showSettingsDialog(activity, "Make a Call");
        return CALL_PHONE_PERM;
    }

    public boolean checkAudio(Activity activity){
        requestAudio(activity);
        if(!checkaudioperm)
            showSettingsDialog(activity, "Record Audio");
        return checkaudioperm;
    }
    public Boolean requestCoarsePerm(final Activity activity){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        checkLocationPermCoarse = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            checkLocationPermCoarse = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return checkLocationPermCoarse;
    }

    public Boolean requestReadContactPerm(final Activity activity){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        checkContactsRead = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            checkContactsRead = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return checkContactsRead;
    }
    public Boolean requestCameraPerm(final Activity activity){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        checkCameraPerm = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            checkCameraPerm = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return checkCameraPerm;
    }
    public Boolean requestWriteStorage(final Activity activity){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        checkStoragePermWrite = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            checkStoragePermWrite = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return checkStoragePermWrite;
    }

    public Boolean requestReadStorage(final Activity activity){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        checkStoragePermRead = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            checkStoragePermRead = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return checkStoragePermRead;
    }

    public Boolean requestStoragePermission(final Activity activity, final String PermissionType, final String ActivityName)
    {
        String permissiontype = "";

        if(ActivityName.trim().indexOf("contacts")>=0)
        {
            requestReadContactPerm(activity);
            if(!checkContactsRead){
                permissiontype += "[ Read Contacts ]";
                isDenied = true;
            }
            else
                isDenied = false;
        }
        else if(ActivityName.trim().indexOf("nearby")>=0) {
            requestCoarsePerm(activity);
            if (!checkLocationPermCoarse) {
                permissiontype += "[ GPS Location ]";
                isDenied = true;
            }

            else
                isDenied = false;
        }else if(ActivityName.trim().indexOf("call")>=0){
            checkCall(activity);
            if(!CALL_PHONE_PERM){
                permissiontype+="[Make a Call]";
            }
        }
        else {

            requestCameraPerm(activity);
            requestReadStorage(activity);
            requestWriteStorage(activity);
            if(!checkCameraPerm) {
                isDenied = true;
                permissiontype += "[ Camera ] ";
            }

            else
                isDenied = false;
            if(!checkStoragePermRead){
                isDenied = true;
                permissiontype += " [ Read Storage ] ";
            }

            else
                isDenied = false;
            if(!checkStoragePermWrite) {
                isDenied = true;
                permissiontype += "[ Write Storage ]";
            }

            else
                isDenied = false;

        }
        // show alert dialog navigating to Settings
        if(ActivityName.trim().indexOf("landing")!=0&&isDenied ==true)
            showSettingsDialog(activity, permissiontype);

        return  isDenied;
    }
    public Boolean isDenied = false;
    private void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }
    public void showSettingsDialog(final Activity activity, String PermissionType) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity, R.style.AppDialog);
        builder.setTitle("IWASTO needs permission to continue.");
        builder.setMessage("IWASTO needs "+PermissionType + " permissions. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings(activity);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
}