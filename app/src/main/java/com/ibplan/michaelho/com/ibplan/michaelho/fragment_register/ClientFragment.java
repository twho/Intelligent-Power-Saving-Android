package com.ibplan.michaelho.com.ibplan.michaelho.fragment_register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.GCMConfig;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.PHPUtilities;
import com.ibplan.michaelho.ibplan.MainActivity;
import com.ibplan.michaelho.ibplan.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MichaelHo on 2015/5/11.
 */
public class ClientFragment extends Fragment implements GCMConfig{
    // php recall
    public static String ifSent;
    public static String phpStatus;
    private View rootView;
    // basic UIs
    private EditText nameToReg;
    private EditText deptToReg;
    private EditText emailToReg;
    private ImageView personalImg;
    private Button Login;
    private Button Register;
    // photo upload
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_IMAGE = 0;
    private Bitmap photoBmp;
    // Error message
    private String ErrorMessage;
    //tools
    private ImageUtilities iu;
    private GoogleCloudMessaging gcm;

    public String regId;

    public ClientFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getActivity());
            regId = getRegId();
            if (regId.isEmpty()) {
                new RegisterTask().execute();
            }
        }
        findView();
        return rootView;
    }

    private void findView() {
        iu = new ImageUtilities();
        Log.d("GCM", "my reg_id=" + regId);
        nameToReg = (EditText) rootView.findViewById(R.id.client_fragment_editText1);
        deptToReg = (EditText) rootView.findViewById(R.id.client_fragment_editText2);
        emailToReg = (EditText) rootView.findViewById(R.id.client_fragment_editText3);
        Login = (Button) rootView.findViewById(R.id.client_fragment_button1);
        Login.setEnabled(false);
        Register = (Button) rootView.findViewById(R.id.client_fragment_button2);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Put "final" in this layer, otherwise, getRegistrationId()
                // cannot work.
                gcm = new GoogleCloudMessaging();

                final String mac = MainActivity.MacAddr;
                ErrorMessage = "";
                if (AccountCheck(nameToReg.getText().toString(), emailToReg.getText().toString(), mac) && gcmCheck(regId)) {
                    if(photoBmp!=null){
                        new TaskAddMember().execute(nameToReg.getText().toString(), deptToReg.getText().toString(),
                                emailToReg.getText().toString(), mac, regId, iu.base64EncodeToString(photoBmp));
                    }else{
                        new TaskAddMember().execute(nameToReg.getText().toString(), deptToReg.getText().toString(),
                                emailToReg.getText().toString(), mac, regId, iu.base64EncodeToString(
                                        BitmapFactory.decodeResource(getActivity().getResources(),
                                                R.drawable.default_por)));
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    builder.setTitle("Error")
                            .setMessage(ErrorMessage + " " + "Error").show();
                }
            }
        });
        personalImg = (ImageView) rootView.findViewById(R.id.client_fragment_personal_img);
        personalImg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity());
                dialog.setTitle("Choose by : ")
                        .setPositiveButton("From Camera",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(takeIntent, REQUEST_CODE_CAMERA);
                                    }
                                })
                        .setNegativeButton("From My Photo",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent takeIntent;
                                        takeIntent = new Intent();
                                        takeIntent.setType("image/*");
                                        takeIntent
                                                .setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent
                                                        .createChooser(takeIntent,
                                                                "data source: "),
                                                REQUEST_CODE_IMAGE);
                                    }
                                }).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE) {
                ContentResolver resolver = getActivity().getContentResolver();
                Uri uri = data.getData();
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(resolver, uri);
                    String photoPath = iu.getPath(getActivity(), uri);
                    photoBmp = bmp;
                    setPortrait(bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                photoBmp = (Bitmap) data.getExtras().get("data");
                setPortrait(photoBmp);
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private Boolean AccountCheck(String name, String email, String mac) {
        if (!"".equals(name) && !"".equalsIgnoreCase(mac)) {
            if(!"".equalsIgnoreCase(email) && email.matches(
                            "(([A-Za-z0-9]+\\.?)+([A-Za-z0-9]+_?)+)+[A-Za-z0-9]+@([a-zA-Z0-9]+\\.)+(com|edu|gov)(\\.(tw|ch|hk))?")){
                return true;
            }else{
                return false;
            }
        } else {
            ErrorMessage += "Name";
            return false;
        }
    }
    private Boolean gcmCheck(String gcmid){
        if(!"".equals(gcmid)){
            return true;
        }else{
            Log.d("GCM", "Error");
            return false;
        }
    }

    private void setPortrait(Bitmap bmp) {
        personalImg.setImageBitmap(iu.getRoundedCroppedBitmap(bmp, (int) (getActivity().getResources().getDimension(R.dimen.img_width))));
    }

    private String getRegId() {
        final SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_FILE,
                Context.MODE_PRIVATE);
        String regId = prefs.getString(PROPERTY_REG_ID, "");
        if (regId.isEmpty()) {
            Log.i(TAG, "Registration id not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with the
        // new app version.
        int appVersionInPrefs = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (appVersionInPrefs != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return regId;
    }

    private int getAppVersion() {
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // the returned dialog displays a localized message about the
                // error and upon user confirmation (by tapping on dialog) will
                // direct them to the Play Store
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                String errMsg = "This device is not supported by Google Play Services.";
                Log.e(TAG, errMsg);
                Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    private void storeInfoLocal(String name, String email, String regId) {
        SharedPreferences sp = getActivity().getSharedPreferences(
                MainActivity.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor se = sp.edit();
        se.putString(MainActivity.PREF_NAME, name.trim());
        se.putString(MainActivity.PREF_EMAIL, email.trim());
        se.commit();
    }


    private class TaskAddMember extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = new PHPUtilities().regMember(params);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            // TODO "DEBUG"
            Log.d("DEBUG", result);
            if (result.equalsIgnoreCase("insertSuccess")) {
                storeInfoLocal(nameToReg.getText().toString(), emailToReg.getText().toString(), regId);
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setTitle("Error")
                        .setMessage("Please check your internet").show();
            }
            pd.dismiss();
        }

    };

    class RegisterTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getActivity());
                }
                regId = gcm.register(SENDER_ID);
                msg = "Dvice registered, registration ID = " + regId;
                Log.d(TAG, msg);
                sendRegIdToServer();
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
        }

        private void sendRegIdToServer() {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("regId", regId));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.toString());
            }

            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    Log.e(TAG, "send regId to server");
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
