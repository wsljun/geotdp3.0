package com.geotdb.compile.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.geotdb.compile.R;
import com.geotdb.compile.service.UploadService;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Media;
import com.rengwuxian.materialedittext.MaterialEditText;


public class MediaInfoDialogFragment extends DialogFragment {

    Context context;
    private ImageView ivwPic;
    private MaterialEditText edtCreateTime;
    private MaterialEditText edtState;

    private MaterialEditText edtLatitude;
    private MaterialEditText edtLongitude;


    Media media;

    public MediaInfoDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(media.getName())
                .customView(R.layout.dlg_media_info, true)
                .positiveText(R.string.disagree)
//                .negativeText(R.string.upload) //暂时不需要这个功能
                .neutralText(R.string.delete)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
//                        new UploadService(context).uploadMedia(media);
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        showDelDialog();
                    }

                })

                .build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失

        try {
            edtCreateTime = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtCreateTime);
            edtState = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtState);

            edtLatitude = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtLatitude);
            edtLongitude = (MaterialEditText) dialog.getCustomView().findViewById(R.id.edtLongitude);

            edtCreateTime.setInputType(InputType.TYPE_NULL);
            edtCreateTime.setText(media.getCreateTime());
            edtState.setText(media.getStateName());

            edtLatitude.setText(media.getGps().getLatitude());
            edtLongitude.setText(media.getGps().getLongitude());

            ivwPic = (ImageView) dialog.getCustomView().findViewById(R.id.ivwPic);
            ivwPic.setImageURI(Uri.parse(media.getLocalPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;

    }


    private void showDelDialog() {
        new MaterialDialog.Builder(getActivity())
                .content(R.string.confirmDelete)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (media.delete(context)) {
                            ToastUtil.showToastL(getActivity(), "删除图片成功");
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            MediaListFragment mediaListFragment = (MediaListFragment) fragmentManager.findFragmentById(R.id.mediaListFragment);
                            mediaListFragment.onRefreshList();
                            dismiss();
                        } else {
                            ToastUtil.showToastL(getActivity(), "删除图片失败");
                        }
                    }
                })
                .show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void show(AppCompatActivity context, String id) {
        try {
            media = new Media().get(context, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }


    /**
     * 启动检验项目的线程
     */
//    private void startGetProjectThread() {
//        AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
//            @Override
//            protected void onPreExecute() {
//                showProgressDialog(false);
//                super.onPreExecute();
//            }
//
//            protected void onProgressUpdate(Integer... progress) {
//                int mProgress = progress[0];
//            }
//
//            @Override
//            protected String doInBackground(String... params1) {
//                try {
//                    //启动上传记录
//                    if (media.uploadMedia(context)) { //如果上传成功
//                        return "长传成功";
//                    } else {
//                        return "上传失败";
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return "上传出错";
//            }
//
//            @Override
//            protected void onPostExecute(String json) {
//                dismissProgressDialog();
//                edtState.setText(media.getStateName());
//                ToastUtil.showToastL(context, json);
//            }
//
//        };
//        task.execute();
//    }


    MaterialDialog mDialog;

    //显示等待的对话框
    private void showProgressDialog(boolean horizontal) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.media_upload_progress_dialog)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .progressIndeterminateStyle(horizontal)
                    .build();
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    //销毁检验序列号的对话框
    private void dismissProgressDialog() {
        mDialog.dismiss();
    }


}
