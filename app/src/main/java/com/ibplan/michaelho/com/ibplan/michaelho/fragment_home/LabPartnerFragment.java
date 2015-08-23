package com.ibplan.michaelho.com.ibplan.michaelho.fragment_home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.BuildingConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.SQLcommands;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.LabListAdapter;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_buildingDetails;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_labDetails;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.PHPUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/6/22.
 */
public class LabPartnerFragment extends Fragment implements BuildingConstants,SQLcommands {
    View view;
    private static sqlOpenHelper_labDetails sqlLabDetails;
    AlertDialogManager adm;
    private ListView labListView;
    private Dialog dialog;
    private static Context context;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private LabListAdapter labListAdapter;

    private int clickPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_labs, container, false);
        init();
        return view;
    }

    private void init() {
        context = getActivity();
        new TaskGetLabList().execute(COMMAND_GET_LAB_LIST);
        adm = new AlertDialogManager();
        sqlLabDetails = new sqlOpenHelper_labDetails(context);
        setListView();
    }

    private void setListView() {
        labListView = (ListView) view
                .findViewById(R.id.fragment_home_labs_listView);
        list = sqlLabDetails.getAllLabDetail();
        if (list != null) {
            labListAdapter = new LabListAdapter(getActivity(), list);
            labListView.setAdapter(labListAdapter);
        }
        labListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                new TaskGetLabStats().execute("EE303");
                Log.d("LabPartnerFragment", sqlLabDetails.getName(position));
                clickPosition = position;
            }
        });
    }

    public void showDetailDialog(Context context, int position, String status) {
        String allToday = status.split("IB")[1];
        String allHistory = status.split("IB")[2];
        String allTemp = status.split("IB")[3];
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.fragment_home_labs_dialog, null);
        Button btnClose = (Button) dialogView.findViewById(R.id.fragment_home_labs_dialog_btn1);
        ImageView iv = (ImageView) dialogView.findViewById(R.id.fragment_home_labs_dialog_iv1);
        byte[] bitmapBytes = sqlLabDetails.getFullDetail(position+1).getImage();
        String bmpStr = new String(bitmapBytes, Charset.defaultCharset());
        iv.setImageBitmap(ImageUtilities.getRoundedCroppedBitmap(ImageUtilities.decodeBase64(bmpStr),
                (int) (context.getResources().getDimension(R.dimen.img_width))));
        TextView title = (TextView) dialogView.findViewById(R.id.fragment_home_labs_dialog_title);
        title.setText("EE303");
        TextView today = (TextView) dialogView.findViewById(R.id.fragment_home_labs_dialog_tv1);
        today.setText("Last Hour: " + allToday + " W");
        TextView history = (TextView) dialogView.findViewById(R.id.fragment_home_labs_dialog_tv2);
        history.setText("History Average: " + allHistory + " W");
        TextView temp = (TextView) dialogView.findViewById(R.id.fragment_home_labs_dialog_temp);
        temp.setText("Current Temperature: " + allTemp + " C");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        WebView myWebView = (WebView) dialogView.findViewById(R.id.fragment_home_labs_dialog_webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.requestFocus();
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl("https://www.youtube.com/watch?v=RG6NlwimR-g");
        Button btnFollow = (Button) dialogView.findViewById(R.id.fragment_home_labs_dialog_btn1);
        dialog = new Dialog(context);
        dialog.setContentView(dialogView);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private class TaskGetLabList extends AsyncTask<String, Void, String> {
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
            return new PHPUtilities().getLabDetail(params);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    String labName = jsonData.getString("lab_name");
                    String labDetail = jsonData.getString("lab_detail");
                    String labImage = jsonData.getString("lab_img");
                    Location location = new Location(labName, labDetail, labImage.getBytes());

                    if (!sqlLabDetails.checkIfExist(labName)) {
                        sqlLabDetails = new sqlOpenHelper_labDetails(getActivity());
                        sqlLabDetails.insertDB(location);
                    }
                }
                new AlertDialogManager().showAlertDialog(context, "Success", "The lab list has been refreshed.");
            } catch (Exception e) {
                Log.d("LabPartnerFragment", e.toString());
                new AlertDialogManager().showAlertDialog(context,
                        "Error", "The ERROR might be caused by: " + "\n"
                                + "1.The server is offline." + "\n"
                                + "2.Your device is offline.");
            }
            pd.dismiss();
        }
    }
    //IB今天電耗IB歷史IB溫度
    private class TaskGetLabStats extends AsyncTask<String, Void, String> {
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
            return new PHPUtilities().getLabStats(params);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("LabPartnerFragment", result);
            if(!"".equalsIgnoreCase(result)){
                if(result.substring(0, 2).equalsIgnoreCase("IB")){
                    showDetailDialog(context, clickPosition, result);
                }
            }else{
                adm.showMessageDialog(context, "Error", "Some errors occur.");
            }
            pd.dismiss();
        }
    }
}

