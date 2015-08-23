package com.ibplan.michaelho.com.ibplan.michaelho.fragment_home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Type;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.BuildingConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.BuildingList;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.SQLcommands;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.BuildingListAdapter;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_building_details;
import com.ibplan.michaelho.com.ibplan.michaelho.util.AChartUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.PHPUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.R;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/5/27.
 */
public class BuildingDashboardFragment extends Fragment implements BuildingConstants,SQLcommands, BuildingList {

    private static sqlOpenHelper_building_details sqlBuildingDetail;
    View view;
    private AChartUtilities aChartUtilities;
    private AlertDialogManager adm;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private BuildingListAdapter buildingListAdapter;
    private ListView buildingsListView;

    //Dialog
    private Spinner sp;
    private ImageView imageView;
    private String spinnerSelect;
    private LinearLayout ll;
    private Dialog dialog;
    private View barView;
    private Button btn1, btn2;
    private String[][] WeeklyStat={{"Monday","20"},{"Tuesday","19"},{"Wednesday","17"},
            {"Thursday","16"},{"Friday","15"},{"Saturday","11"},{"Sunday","8"}};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_building, container, false);
        init();
        return view;
    }

    private void init() {
        new TaskGetBuildingList().execute(COMMAND_GET_BULDING_LIST);
        adm = new AlertDialogManager();
        aChartUtilities = new AChartUtilities(getActivity());
        sqlBuildingDetail = new sqlOpenHelper_building_details(getActivity());
        setListView();
    }

    private void setListView() {
        buildingsListView = (ListView) view
                .findViewById(R.id.fragment_home_listView);
        list = sqlBuildingDetail.getAllBuildingDetail();
        if (list != null) {
            buildingListAdapter = new BuildingListAdapter(getActivity(), list);
            buildingsListView.setAdapter(buildingListAdapter);
        }
        buildingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showDetailDialog(getActivity(), position);
            }
        });
    }

    public void showDetailDialog(Context context, int position) {
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.fragment_home_building_dialog, null);
        ll = (LinearLayout) dialogView.findViewById(R.id.fragment_home_dashboard_ll);
        btn1 = (Button) dialogView.findViewById(R.id.fragment_home_dashboard_btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn2 = (Button) dialogView.findViewById(R.id.fragment_home_dashboard_btn2);
        try{
            barView = aChartUtilities.getBarChart(spinnerSelect, "ErrCode", "Consumed Energy(kw-h)", WeeklyStat);
            ll.removeAllViews();
            ll.addView(barView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600));

        }catch(Exception e){

        }
        imageView = (ImageView) dialogView.findViewById(R.id.fragment_home_dashboard_iv);
        byte[] bitmapBytes = sqlBuildingDetail.getFullDetail(position+1).getImage();
        String bmpStr = new String(bitmapBytes, Charset.defaultCharset());
        Bitmap bmp = ImageUtilities.getRoundedCroppedBitmap(ImageUtilities.decodeBase64(bmpStr),
                (int) (getResources().getDimension(R.dimen.img_width)));
        imageView.setImageBitmap(bmp);
        sp = (Spinner) dialogView.findViewById(R.id.fragment_home_dashboard_spinner);
        SpinnerAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, paramList);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spinnerSelect = paramList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerSelect = paramList[0];
            }
        });
        dialog = new Dialog(getActivity());
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
        if (sqlBuildingDetail != null) {
            sqlBuildingDetail.close();
            sqlBuildingDetail = null;
        }
    }


    private class TaskGetBuildingList extends AsyncTask<String, Void, String> {
        ProgressDialog pd;
        Context mContext = getActivity();

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return new PHPUtilities().getBuildingDetail(params);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    String buildingName = jsonData.getString("building_name");
                    String detail = jsonData.getString("detail");
                    String buildingImage = jsonData.getString("building_image");
                    String addDate = TimeUtilities.getTimeyyyy_MM_dd_HH_mm();
                    Location location = new Location(buildingName, detail, buildingImage.getBytes());

                    if (!sqlBuildingDetail.checkIfExist(buildingName)) {
                        sqlBuildingDetail = new sqlOpenHelper_building_details(getActivity());
                        sqlBuildingDetail.insertDB(location);
                    }
                }
                new AlertDialogManager().showAlertDialog(mContext, "Success", "The attendance list has been refreshed.");
            } catch (Exception e) {
                Log.e("HomeFragment", e.toString());
                new AlertDialogManager().showAlertDialog(mContext,
                        "Error", "The ERROR might be caused by: " + "\n"
                                + "1.The server is offline." + "\n"
                                + "2.Your device is offline.");
            }
            pd.dismiss();
        }
    }
}
