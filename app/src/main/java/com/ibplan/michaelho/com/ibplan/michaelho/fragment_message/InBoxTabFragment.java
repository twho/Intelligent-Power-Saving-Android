package com.ibplan.michaelho.com.ibplan.michaelho.fragment_message;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ibplan.michaelho.com.ibplan.michaelho.objects.SingleMessage;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.TagView;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.MessageListAdatpter;
import com.ibplan.michaelho.com.ibplan.michaelho.tools.sqlOpenHelper_messageBox;
import com.ibplan.michaelho.com.ibplan.michaelho.util.ImageUtilities;
import com.ibplan.michaelho.com.ibplan.michaelho.util.TimeUtilities;
import com.ibplan.michaelho.ibplan.MainActivity;
import com.ibplan.michaelho.ibplan.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/5/26.
 */
public class InBoxTabFragment extends Fragment {
    private View view;
    private Context context;
    private ListView lv;
    private MessageListAdatpter messageListAdatpter;
    private sqlOpenHelper_messageBox sqlMessage;
    private ArrayList<HashMap<String, Object>> messageList;

    //Dialog
    private Dialog dialog;
    private DialogClickListener dialogClickListener;
    private TextView tvTitle, tvSender, tvReceiver, tvTime, tvDetail;
    private ImageView ivSender;
    private ImageButton ibFeedback, ibClose, ibDel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message_inbox, container, false);
        init();
        return view;
    }

    private void init(){
        context = getActivity();
        dialogClickListener = new DialogClickListener();
        lv = (ListView) view.findViewById(R.id.fragment_inbox_listView);
        sqlMessage = new sqlOpenHelper_messageBox(context);
        setListview();
    }

    private void setListview(){
        messageList = sqlMessage.getAllMessageDetail();
        messageListAdatpter = new MessageListAdatpter(context, messageList);
        lv.setAdapter(messageListAdatpter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setMailDialog(messageList.size()-position-1);
            }
        });
    }


    private void setMailDialog(int position){
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.fragment_message_inbox_dialog, null);
        findDialogViews(dialogView);
        SingleMessage singleMessage = sqlMessage.getFullDetail(position+1);
        String sender = singleMessage.getSender();
        tvTitle.setText(singleMessage.getTitle());
        tvSender.setText(sender);
        if("SYSTEM".equalsIgnoreCase(sender)){
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.img_message_system);
            ivSender.setImageBitmap(
                    ImageUtilities.getRoundedCroppedBitmap(bmp, (int) (context.getResources().getDimension(R.dimen.img_width))));
        }else if("LIKE".equalsIgnoreCase(sender)){
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.img_message_like);
            ivSender.setImageBitmap(
                    ImageUtilities.getRoundedCroppedBitmap(bmp, (int) (context.getResources().getDimension(R.dimen.img_width))));
        }
        tvReceiver.setText("Receiver: Me");
        tvTime.setText("Received time: "+singleMessage.getTime());
        tvDetail.setText(singleMessage.getDetail());
        sqlMessage.UpdateIfRead(singleMessage.getDetail());
        dialog = new Dialog(getActivity());
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sqlMessage == null) {
            sqlMessage = new sqlOpenHelper_messageBox(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sqlMessage != null) {
            sqlMessage.close();
            sqlMessage = null;
        }
    }

    private void findDialogViews(View view){
        ivSender = (ImageView) view.findViewById(R.id.fragment_message_inbox_dialog_iv);
        tvTitle = (TextView) view.findViewById(R.id.fragment_message_inbox_dialog_title);
        tvSender = (TextView) view.findViewById(R.id.fragment_message_inbox_dialog_sender);
        tvReceiver = (TextView) view.findViewById(R.id.fragment_message_inbox_dialog_tv1);
        tvTime = (TextView) view.findViewById(R.id.fragment_message_inbox_dialog_tv2);
        tvDetail = (TextView) view.findViewById(R.id.fragment_message_inbox_dialog_container);
        ibFeedback = (ImageButton) view.findViewById(R.id.message_fragment_dialog_iB1);
        ibFeedback.setOnClickListener(dialogClickListener);
        ibClose = (ImageButton) view.findViewById(R.id.message_fragment_dialog_iB2);
        ibClose.setOnClickListener(dialogClickListener);
        ibDel = (ImageButton) view.findViewById(R.id.message_fragment_dialog_iB3);
        ibDel.setOnClickListener(dialogClickListener);
    }

    public class DialogClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.message_fragment_dialog_iB1:
                    break;
                case R.id.message_fragment_dialog_iB2:
                    dialog.dismiss();
                    setListview();
                    break;
                case R.id.message_fragment_dialog_iB3:
                    break;
            }
        }
    }
}
