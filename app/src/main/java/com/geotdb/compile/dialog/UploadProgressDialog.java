package com.geotdb.compile.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.utils.L;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/6/29.
 */
public class UploadProgressDialog extends DialogFragment {

    private ProgressBar progressBar;
    private TextView speed;
    private TextView total;
    private TextView complete;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title("上传数据").customView(R.layout.progress_dialog, true).build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失

        progressBar = (ProgressBar) dialog.findViewById(R.id.dialog_progressBar);
        speed = (TextView) dialog.findViewById(R.id.dialog_speed);
        total = (TextView) dialog.findViewById(R.id.dialog_total);
        complete = (TextView) dialog.findViewById(R.id.dialog_complete);
        return dialog;
    }

    public void setProgressBar(float progress, long t) {
        progressBar.setProgress((int) (100 * progress));
        complete.setText(convertFileSize((long) (progress * t)) + "");
        total.setText(convertFileSize(t) + "");
        newComplete = (long) (progress * t);
    }

    public void scheduleTimer() {
        timer.schedule(task, 0, 1000); //延时1000ms后执行，1000ms执行一次
    }

    public void cancelTimer() {
        timer.cancel();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            speed.setText(convertFileSize((long) msg.obj) + "/s");
        }
    };

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            L.e("TAG", "TimerTask");
            s = newComplete - oldComplete;
            Message msg = new Message();
            msg.obj = s;
            handler.sendMessage(msg);
            oldComplete = newComplete;
            L.e("TAG", "newComplete=" + newComplete);
            L.e("TAG", "oldComplete=" + oldComplete);
        }
    };

    private long s = 0;
    private long oldComplete = 0;
    private long newComplete = 0;


    Timer timer = new Timer(true);


    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

//    public void show(AppCompatActivity context) {
//        this.show(context.getSupportFragmentManager(), "dialog");
//    }
}
