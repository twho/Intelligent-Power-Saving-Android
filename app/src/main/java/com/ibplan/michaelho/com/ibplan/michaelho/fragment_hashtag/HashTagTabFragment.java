package com.ibplan.michaelho.com.ibplan.michaelho.fragment_hashtag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.BuildingList;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.EventConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.constants.SQLcommands;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Event;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.TagView;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.AlertDialogManager;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_events;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.PHPUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.MainActivity;
import com.ibplan.michaelho.ibplan.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/5/18.
 */
public class HashTagTabFragment extends Fragment implements EventConstants, SQLcommands, BuildingList {

    private static Context context;
    //database
    private static sqlOpenHelper_events sqlEvents;
    View view;
    private String TAG = "hashTagTabFragment";
    private ArrayList<HashMap<String, Object>> eventList;
    private AlertDialogManager adm;
    private ImageUtilities iu;
    private Button btn_reset, btn_refresh;
    private ImageButton btn_zoomIn, btn_zoomOut;
    private ImageButton btn_tag;
    private TextView tvZoom;
    private Button btn_tag_finish;
    private ImageView ivMap;
    private Bitmap bmp;
    private float scaleWidth = 1;
    private float scaleHeight = 1;
    private int i = 0;
    private ScrollView scrollView;
    private DecimalFormat format;
    private boolean ifTag = false;
    private RelativeLayout rl;
    private RelativeLayout.LayoutParams params;
    private String xPos, yPos;

    //Message Dialog
    private Dialog dialog;
    private EditText ed1;
    private Spinner sp1, sp2;
    private String buildingSelect, floorSelect;
    private Button btn_confirm, btn_cancel;
    private String ivPath;
    private Bitmap bmpNow;
    public static ImageView ivEvent;
    public static final int MODE_IMAGE_GALLERY = 3;
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_IMAGE = 0;


    //clickListener
    private MapClickListener mapClickListener;

    public static Fragment newInstance(Context context) {
        Fragment f = new HashTagTabFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hashtag, container, false);
        mapClickListener = new MapClickListener();
        context = getActivity();
        new TaskGetEvent().execute(COMMAND_GET_EVENTS);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sqlEvents == null) {
            sqlEvents = new sqlOpenHelper_events(context);
        }
        if (context == null) {
            context = getActivity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sqlEvents != null) {
            sqlEvents.close();
            sqlEvents = null;
        }
    }

    private void init() {
        findViews();
        sqlEvents = new sqlOpenHelper_events(context);
        adm = new AlertDialogManager();
        iu = new ImageUtilities();
        format = new DecimalFormat("#.#");
        params = new RelativeLayout.LayoutParams(MainActivity.screenWidth / 8, MainActivity.screenWidth / 8);
        eventList = new ArrayList<HashMap<String, Object>>();
        eventList = sqlEvents.getAllEvents();
        tvZoom.setTextColor(getResources().getColor(R.color.white));
        tvZoom.setText("Zoom: 3X");
        bmp = ((BitmapDrawable) getResources().getDrawable(
                R.drawable.ntust_map)).getBitmap();
        ivMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (ifTag) {
                    params.leftMargin = (int) event.getX() - params.width / 2;
                    params.topMargin = (int) event.getY() - params.height;
                    xPos = String.valueOf(event.getX() / ivMap.getWidth());
                    yPos = String.valueOf(event.getY() / ivMap.getHeight());
                    btn_tag_finish.setEnabled(false);
                    tagConfirmDialog(context);
                    ifTag = false;
                }
                Log.d("POSITION", "圖片大小：" + ivMap.getWidth() + "   " + ivMap.getHeight());
                Log.d("POSITION", "現在位置：" + event.getX() + "   " + event.getY());
                Log.d("POSITION", "螢幕大小：" + MainActivity.screenWidth + "   " + MainActivity.screenHeight);
                return false;
            }
        });
        btn_tag_finish.setEnabled(false);
        btn_tag_finish.setOnClickListener(mapClickListener);
        btn_reset.setOnClickListener(mapClickListener);
        btn_zoomIn.setOnClickListener(mapClickListener);
        btn_zoomOut.setOnClickListener(mapClickListener);
        btn_tag.setOnClickListener(mapClickListener);
        btn_refresh.setOnClickListener(mapClickListener);
        setTag(3.0);
    }

    private void findViews() {
        ivMap = (ImageView) view.findViewById(R.id.imageView1);
        btn_tag_finish = (Button) view.findViewById(R.id.fragment_hashtag_btn1);
        btn_refresh = (Button) view.findViewById(R.id.fragment_hashtag_btn3);
        btn_reset = (Button) view.findViewById(R.id.fragment_hashtag_btn2);
        btn_zoomIn = (ImageButton) view.findViewById(R.id.fragment_hashtag_imageButton1);
        btn_zoomOut = (ImageButton) view.findViewById(R.id.fragment_hashtag_imageButton2);
        btn_tag = (ImageButton) view.findViewById(R.id.fragment_hashtag_imageButton3);
        tvZoom = (TextView) view.findViewById(R.id.fragment_hashtag_tv);
        rl = (RelativeLayout) view.findViewById(R.id.fragment_events_hashtag_rl);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView1);
    }

    private void updateTag() {
        new TaskGetEvent().execute(COMMAND_GET_EVENTS);
        setTag(1);
    }

    private void setTag(double zoom) {
        Float mapWidth = Float.valueOf(String.valueOf(ivMap.getWidth() * zoom));
        Float mapHeight = Float.valueOf(String.valueOf(ivMap.getHeight() * zoom));
        if (eventList.size() > 0) {
            for (int k = 0; k < eventList.size(); k++) {
                Float x_pos = Float.valueOf((String) eventList.get(k).get(X_POS));
                Float y_pos = Float.valueOf((String) eventList.get(k).get(Y_POS));
                RelativeLayout.LayoutParams parameter = new RelativeLayout.LayoutParams(MainActivity.screenWidth / 8, MainActivity.screenWidth / 8);
                parameter.leftMargin = (int) (x_pos * mapWidth) - parameter.width / 2;
                parameter.topMargin = (int) (y_pos * mapHeight) - parameter.height;
                byte[] bitmapBytes = (byte[]) eventList.get(k).get(IMG);
                String bmpStr = new String(bitmapBytes, Charset.defaultCharset());
                Bitmap bmp = ImageUtilities.decodeBase64(bmpStr);
                TagView tagView = new TagView(context, (String) eventList.get(k).get(EVENTS),
                        (String) eventList.get(k).get(LOCATION), bmp, (String) eventList.get(k).get(TIME), (String) eventList.get(k).get(IFFIXED));
                if (x_pos <= mapWidth && y_pos <= mapHeight) {
                    rl.addView(tagView.getView(), parameter);
                }
            }
        }
    }

    private float zoomValue() {
        return Float.valueOf(ivMap.getWidth()) / Float.valueOf(MainActivity.screenWidth);
    }

    private void setScaleImg(Matrix matrix) {
        matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                bmp.getHeight(), matrix, true);
        ivMap.setImageBitmap(resizeBmp);
        rl.addView(ivMap);
    }

    public void tagConfirmDialog(Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.fragment_hashtag_dialog, null);
        ed1 = (EditText) dialogView.findViewById(R.id.fragment_events_hashtag_dialog_ed1);
        ivEvent = (ImageView) dialogView.findViewById(R.id.fragment_events_hashtag_dialog_iv1);
        ivEvent.setOnClickListener(mapClickListener);
        sp1 = (Spinner) dialogView.findViewById(R.id.fragment_events_hashtag_dialog_spinner1);
        sp2 = (Spinner) dialogView.findViewById(R.id.fragment_events_hashtag_dialog_spinner2);
        SpinnerAdapter adapter1 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, buildingList);
        sp1.setAdapter(adapter1);
        sp1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                buildingSelect = buildingList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                buildingSelect = buildingList[0];
            }
        });
        SpinnerAdapter adapter2 = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, buildingFloorList);
        sp2.setAdapter(adapter2);
        sp2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                floorSelect = buildingFloorList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                floorSelect = buildingFloorList[0];
            }
        });
        btn_confirm = (Button) dialogView.findViewById(R.id.fragment_events_hashtag_dialog_btn1);
        btn_cancel = (Button) dialogView.findViewById(R.id.fragment_events_hashtag_dialog_btn2);
        btn_confirm.setOnClickListener(mapClickListener);
        btn_cancel.setOnClickListener(mapClickListener);
        dialog = new Dialog(context);
        dialog.setContentView(dialogView);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE) {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = data.getData();

                try {
                    bmpNow = MediaStore.Images.Media.getBitmap(resolver, uri);
                    ivPath = ImageUtilities.getPath(context, uri);
                    ivEvent.setImageBitmap(bmpNow);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                bmpNow = (Bitmap) data.getExtras().get("data");
                ivEvent.setImageBitmap(bmpNow);
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class TaskPostEvent extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = new PHPUtilities().postEvent(params);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            if (!"".equalsIgnoreCase(result)) {
                if (result.equalsIgnoreCase("InsertSuccess")) {
                    adm.showMessageDialog(context, "Success", "Your event has been uploaded.");
                    updateTag();
                } else {
                    adm.showAlertDialog(context, "Error", context.getResources().getString(R.string.error_message));
                }
            }
        }
    }

    ;

    private class TaskGetEvent extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return new PHPUtilities().getEvent(params);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                sqlEvents.deleteAll();
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    String name = jsonData.getString("name");
                    String department = jsonData.getString("department");
                    String location = jsonData.getString("location");
                    String image = jsonData.getString("img");
                    String poster_img = jsonData.getString("poster_img");
                    String x_pos = jsonData.getString("x_pos");
                    String y_pos = jsonData.getString("y_pos");
                    String events = jsonData.getString("event");
                    String time = jsonData.getString("time");
                    String if_fixed = jsonData.getString("if_fixed");
                    Event event = new Event(name, department, location, x_pos, y_pos, events, image.getBytes(), poster_img.getBytes(), time, if_fixed);
                    Log.d(TAG, events);
                    sqlEvents = new sqlOpenHelper_events(context);
                    sqlEvents.insertDB(event);
                }
            } catch (Exception e) {
                Log.e("HashTagTabFragment", e.toString());
                adm.showAlertDialog(context, "Error", context.getResources().getString(R.string.error_message));
            }
            pd.dismiss();
        }
    };

    public class MapClickListener implements View.OnClickListener {
        Matrix matrix;
        Context context = getActivity();

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fragment_hashtag_btn1:
                    btn_tag_finish.setEnabled(false);
                    ifTag = false;
                    break;
                case R.id.fragment_hashtag_btn2:
                    rl.removeAllViews();
                    double scale = Float.valueOf(MainActivity.screenWidth) / Float.valueOf(ivMap.getWidth());
                    scaleWidth = (float) (scaleWidth * scale);
                    scaleHeight = (float) (scaleHeight * scale);
                    setScaleImg(matrix);
                    setTag(scale);
                    tvZoom.setText("Zoom: 1X");
                    break;
                case R.id.fragment_hashtag_btn3:
                    updateTag();
                    break;
                case R.id.fragment_hashtag_imageButton1:
                    if (zoomValue() * 1.25 < 3.5) {
                        rl.removeAllViews();
                        scale = 1.25;
                        scaleWidth = (float) (scaleWidth * scale);
                        scaleHeight = (float) (scaleHeight * scale);
                        setScaleImg(matrix);
                        setTag(scale);
                        tvZoom.setText("Zoom: " + format.format(zoomValue() * 1.25) + "X");
                    }
                    break;
                case R.id.fragment_hashtag_imageButton2:
                    if (zoomValue() * 0.75 >= 1) {
                        rl.removeAllViews();
                        scale = 0.75;
                        scaleWidth = (float) (scaleWidth * scale);
                        scaleHeight = (float) (scaleHeight * scale);
                        setScaleImg(matrix);
                        setTag(0.75);
                        tvZoom.setText("Zoom: " + format.format(zoomValue() * 0.75) + "X");
                    }
                    break;
                case R.id.fragment_hashtag_imageButton3:
                    btn_tag_finish.setEnabled(true);
                    ifTag = true;
                    break;
                case R.id.fragment_events_hashtag_dialog_btn1:
                    if (!"".equalsIgnoreCase(ed1.getText().toString()) && bmpNow != null) {
                        //POST MESSAGE TO SERVER
                        rl.addView(new TagView(context, ed1.getText().toString(), buildingSelect + " " + floorSelect, bmpNow, TimeUtilities.getTimehhmm(), "0").getView(), params);
                        new TaskPostEvent().execute(MainActivity.MacAddr, buildingSelect + " " + floorSelect, ed1.getText().toString(),
                                TimeUtilities.getTimeyyyyMMddHHmm(), xPos, yPos, iu.base64EncodeToString(bmpNow), "0");
                        dialog.dismiss();
                    } else {
                        adm.showMessageDialog(context, "Error", "Event blank should not be NULL");
                    }
                    break;
                case R.id.fragment_events_hashtag_dialog_btn2:
                    dialog.dismiss();
                    break;
                case R.id.fragment_events_hashtag_dialog_iv1:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
                                            Intent takeIntent = new Intent();
                                            takeIntent.setType("image/*");
                                            takeIntent
                                                    .setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(Intent
                                                            .createChooser(takeIntent,
                                                                    "data source: "),
                                                    REQUEST_CODE_IMAGE);
                                        }
                                    }).show();
                    break;
            }
        }
    }
}
