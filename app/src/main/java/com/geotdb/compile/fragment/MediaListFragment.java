/*
 * Copyright (C) 2015 The Android Open Source PowerLog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geotdb.compile.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TintContextWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.geotdb.compile.R;
import com.geotdb.compile.fragment.base.BaseFragment;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.CompressUtils;
import com.geotdb.compile.utils.DateUtil;
import com.geotdb.compile.utils.GPSutils;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.SPUtils;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.view.MarqueeTextView;
import com.geotdb.compile.view.RoundAngleImageView;
import com.geotdb.compile.view.camera.CameraActivity;
import com.geotdb.compile.view.video.VideoPlayerActivity;
import com.geotdb.compile.vo.Gps;
import com.geotdb.compile.vo.Media;
import com.geotdb.compile.vo.Record;
import com.j256.ormlite.dao.Dao;

import net.qiujuer.genius.ui.widget.Button;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;
import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.model.MediaRecorderConfig;

public class MediaListFragment extends BaseFragment {
    public static final String KEY_RECORD = ":rMediaListFragmentecord";

    public Context context;
    public Activity activity;

    private RecyclerView recyclerView;
    private RecyclerView recyclerView_video;

    public ItemAdapter itemAdapter, itemAdapter2;
    //    public MediaAdapter mediaAdapter;
    List<Media> photoList, videoList;

    String recordID;
    Record record;

    private Uri fileUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    AMapLocation amapLocation;
    LocationFragment locationFragment;

//    private Camera camera = null;
//    private Camera.Parameters parameters = null;

    int minFrame = 1000000;//获取设备最小帧频作为视频最大帧频

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
        try {
            if (getArguments().containsKey(KEY_RECORD)) {
                record = (Record) getArguments().getSerializable(KEY_RECORD);
                recordID = record.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_media_list, null);

        recyclerView = (RecyclerView) convertView.findViewById(R.id.recyclerView);
        recyclerView_video = (RecyclerView) convertView.findViewById(R.id.recyclerView_video);

        LinearLayoutManager mLayoutManager_photo = new LinearLayoutManager(recyclerView.getContext());
        mLayoutManager_photo.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager mLayoutManager_video = new LinearLayoutManager(recyclerView_video.getContext());
        mLayoutManager_video.setOrientation(LinearLayoutManager.HORIZONTAL);

        photoList = new ArrayList<>();
        videoList = new ArrayList<>();
        getMediaList();
        itemAdapter = new ItemAdapter(context, photoList, ItemAdapter.ITEM_MEDIA_TYPE_PHOTO);
        recyclerView.setLayoutManager(mLayoutManager_photo);
        recyclerView.setAdapter(itemAdapter);

        itemAdapter2 = new ItemAdapter(context, videoList, ItemAdapter.ITEM_MEDIA_TYPE_VIDEO);
        recyclerView_video.setLayoutManager(mLayoutManager_video);
        recyclerView_video.setAdapter(itemAdapter2);
        //实例化位置fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        locationFragment = (LocationFragment) fragmentManager.findFragmentById(R.id.locationFragment);

        SPUtils.put(context, "directoryPath", "");
        L.e("onCreateView-----" + SPUtils.get(context, "directoryPath", "") + "");
        //打开相机获取视频帧率
//        getFrame();
        photoOrVideo();
        return convertView;
    }

    public void photoOrVideo() {
        String recordType = record.getType();
        if (recordType.equals(Record.TYPE_SCENE_VIDEO) || recordType.equals(Record.TYPE_SCENE_PRINCIPAL) || recordType.equals(Record.TYPE_SCENE_TECHNICIAN) || recordType.equals(Record.TYPE_SCENE_OPERATEPERSON) || recordType.equals(Record.TYPE_SCENE_OPERATECODE) || recordType.equals(Record.TYPE_SCENE_RECORDPERSON) || recordType.equals(Record.TYPE_SCENE_SCENE)) {
            if (recordType.equals(Record.TYPE_SCENE_VIDEO)) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView_video.setVisibility(View.GONE);
            }
        }
    }


    public void onRefreshList() {
        photoList.clear();
        videoList.clear();
        getMediaList();
        itemAdapter.notifyDataSetChanged();
        itemAdapter2.notifyDataSetChanged();
    }


    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        private static final int ITEM_VIEW_TYPE_HEADER = 0;
        private static final int ITEM_VIEW_TYPE_ITEM = 1;

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Media> mValues;

        private View header;
        public static final int ITEM_MEDIA_TYPE_PHOTO = 0;
        public static final int ITEM_MEDIA_TYPE_VIDEO = 1;

        private int mediaType;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Media vo;

            public View mView;
            public RoundAngleImageView ivwPic;
            public MarqueeTextView tvwPic;
//            public ImageView ivwState;

            public CardView btnMedia;
            public MarqueeTextView midia_header_tv;
            public ImageView ivwPic_img;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                ivwPic = (RoundAngleImageView) view.findViewById(R.id.ivwPic);
                tvwPic = (MarqueeTextView) view.findViewById(R.id.tvwPic);
//                ivwState = (ImageView) view.findViewById(R.id.ivwState);
                btnMedia = (CardView) view.findViewById(R.id.btnMedia);
                midia_header_tv = (MarqueeTextView) view.findViewById(R.id.midia_header_tv);
                ivwPic_img = (ImageView) view.findViewById(R.id.ivwPic_img);
            }

            public ViewHolder(View view, int i) {
                super(view);
                mView = view;
                btnMedia = (CardView) view.findViewById(R.id.btnMedia);
                midia_header_tv = (MarqueeTextView) view.findViewById(R.id.midia_header_tv);
                ivwPic_img = (ImageView) view.findViewById(R.id.ivwPic_img);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + ivwPic.getTag();
            }
        }

        public ItemAdapter(Context context, List<Media> items, int mediaType) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
            this.mediaType = mediaType;
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ITEM_VIEW_TYPE_HEADER;
            }
            return ITEM_VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
//            L.e("size=" + mValues.size());
            return mValues.size();
        }

        public List<Media> getList() {
            return mValues;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            L.e("viewType=" + viewType);
            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_media_header, parent, false);
//                view.setBackgroundResource(mBackground);
                return new ViewHolder(view, 1);
            }
            view = LayoutInflater.from(context).inflate(R.layout.list_item_media, parent, false);
//            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(ViewHolder rv, int position) {
            final ViewHolder holder = (ViewHolder) rv;
            holder.vo = mValues.get(position);

            if (getItemViewType(position) == ITEM_VIEW_TYPE_HEADER) {
                if (mediaType == ITEM_MEDIA_TYPE_PHOTO) {
                    holder.midia_header_tv.setText("拍摄照片");
                    holder.ivwPic_img.setImageResource(R.drawable.ai_icon_photo);
                } else {
                    holder.midia_header_tv.setText("录制视频");
                    holder.ivwPic_img.setImageResource(R.drawable.ai_icon_video);
                }
                holder.btnMedia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        amapLocation = locationFragment.aMapLocation;
                        if (null == amapLocation) {
                            ToastUtil.showToastS(getActivity(), "未获取定位信息");
                        } else if (!Common.gPSIsOPen(context)) {
                            ToastUtil.showToastS(context, "GPS未开启，请开启以提高精度");
                        } else {
                            //分别录制视频和拍摄照片
                            if (mediaType == ITEM_MEDIA_TYPE_PHOTO) {
//                                goMyCamera();
                                goIMAGE();
                            } else {
                                goVideo();
                            }
                        }
                    }
                });
            } else {
                Uri uri;
                if (mediaType == ITEM_MEDIA_TYPE_PHOTO) {
                    uri = Uri.parse(holder.vo.getLocalPath());
                } else {
                    uri = Uri.parse(Common.getPicByDir(holder.vo.getLocalPath()));
                }
                holder.ivwPic.setImageURI(uri);
                holder.btnMedia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mediaType == ITEM_MEDIA_TYPE_VIDEO) {
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra("mediaId", holder.vo.getId());
                            intent.putExtra(MediaRecorderActivity.VIDEO_URI, Common.getVideoByDir((holder.vo.getLocalPath())));
                            intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, holder.vo.getLocalPath());
                            startActivity(intent);
                        } else {
//                            TintContextWrapper wrapper = (TintContextWrapper) view.getContext();
                            AppCompatActivity activity = (AppCompatActivity) getActivityFromView(view);
                            new MediaInfoDialogFragment().show(activity, holder.vo.getId());
                        }
                    }
                });
                if (mediaType == ITEM_MEDIA_TYPE_PHOTO) {
                    holder.ivwPic_img.setImageResource(R.drawable.ai_icon_havephoto);
                } else {
                    holder.ivwPic_img.setImageResource(R.drawable.ai_icon_video);
                }
                holder.tvwPic.setText(holder.vo.getName());
            }
        }


    }

    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * 指定Uri
     * 调用系统相机
     */
    private void goIMAGE() {
        // 利用系统自带的相机应用:拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Common.getOutputMediaFileUri();
        // 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 调用自定义相机
     *
     * @return
     */
    private void goMyCamera() {
        Intent intent = new Intent(context, CameraActivity.class);
        startActivityForResult(intent, 1000);
    }

    /**
     * 调用短视频录制
     *
     * @return
     */
    private void goVideo() {
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .doH264Compress(true)
                .smallVideoWidth(240)
                .smallVideoHeight(160)
                .recordTimeMax(1800 * 1000)
//                .maxFrameRate(minFrame / 1000)
                .maxFrameRate(20)
                .minFrameRate(8)
                .captureThumbnailsTime(1)
                .recordTimeMin((int) (1.5 * 1000))
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(activity, VideoPlayerActivity.class.getName(), config);
    }

//    private void getFrame() {
//        camera = Camera.open();
//        parameters = camera.getParameters(); // 获取各项参数
//        List<int[]> range = parameters.getSupportedPreviewFpsRange();
//        L.e("TAG", "range:" + range.size());
//        for (int j = 0; j < range.size(); j++) {
//            int[] r = range.get(j);
//            for (int k = 0; k < r.length; k++) {
//                L.e("TAG", "TAG" + r[k]);
//                if (r[k] < minFrame) {
//                    minFrame = r[k];
//                }
//            }
//        }
//        L.e("TAG", "最小Frame = " + minFrame);
//        if (minFrame < 8000) {
//            minFrame = 8000;
//        }
//        camera.release();
//        camera = null;
//    }


    private void getMediaList() {
        List<Media> list = new MediaDao(context).getMediaListByRecordID2(recordID);
        for (Media media : list) {
            if (media.getLocalPath().endsWith("jpg")) {
                photoList.add(media);
            } else {
                videoList.add(media);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("onResume-----" + SPUtils.get(context, "directoryPath", "") + "");
        String dirPath = (String) SPUtils.get(context, "directoryPath", "");
        //判断是否是录像
        if (!TextUtils.isEmpty(dirPath)) {
            if (dirPath.equals("delete")) {
                L.e("onResume-----delete");
            } else {
                L.e("onResume-----path");
                //获取位置信息，经纬度
                amapLocation = locationFragment.aMapLocation;
                try {
                    //这里保存的是文件夹路径，不修改属性，创建新的对象
                    Media media = new Media(context, record, dirPath, amapLocation);
                    Gps gps = new Gps(media, amapLocation, "录像");
                    media.setGpsID(gps.getId());
                    DBHelper dbHelper = DBHelper.getInstance(context);
                    //写入数据库
                    Dao<Media, String> mediaDao = dbHelper.getDao(Media.class);
                    mediaDao.create(media);
                    Dao<Gps, String> gpsDao = dbHelper.getDao(Gps.class);
                    gpsDao.create(gps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SPUtils.put(context, "directoryPath", "");
        }
        onRefreshList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //调用系统相机
        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                if (data != null) {
                    // 没有指定特定存储路径的时候,按说不会走这里，以防万一写上，之后在完善这个方法
                    if (data.hasExtra("data")) {
                        //获取未压缩、为保存的图片，
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        //压缩
                        CompressUtils.jniBitmap(Common.getOutputMediaFile(), CompressUtils.comp(thumbnail), amapLocation);
                    }
                } else {
                    //获取位置信息，经纬度
                    amapLocation = locationFragment.aMapLocation;
//                    String path = CompressUtils.getRealFilePath(getActivity(), fileUri);
//                    // 读取uri所在的图片,压缩过了  根据路径压缩  2+1
//                    Bitmap bitmap = CompressUtils.getimage(path);
//                    bitmap = CompressUtils.watermarkBitmap(bitmap, "纬度:" + amapLocation.getLatitude() + "\n" + "经度:" + amapLocation.getLongitude() + "\n" + "时间:" + DateUtil.date2Str(new Date()));
//                    File file = new File(path);
//                    //调用jni编程压缩图片，并且写入经纬度，写入经纬度只能在压缩图片之后，不然没有效果
//                    CompressUtils.jniBitmap(file, bitmap, amapLocation);
//                    L.e("TAG", "media--" + String.valueOf(amapLocation.getLatitude()) + "---" + String.valueOf(amapLocation.getLongitude()));
                    try {
                        //拍照之后存在老的文件夹里，压缩之后的存在新的文件夹里，删除老的文件
                        String imagePath = CompressUtils.getRealFilePath(getActivity(), fileUri);
                        File oldFile = new File(imagePath);
                        File file = new Compressor(context)
                                .setMaxWidth(200)
                                .setMaxHeight(220)
                                .setQuality(93)
                                .setDestinationDirectoryPath(Urls.APP_PATH)
                                .compressToFile(oldFile);

                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                        //修改图片的gps属性
                        GPSutils.setNewThumbnail(file.getAbsolutePath(), amapLocation);
                        //创建新的对象
                        Media media = new Media(context, record, file.getAbsolutePath(), amapLocation);

                        MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), "title", "description");
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File( file.getAbsolutePath()))));

                        Gps gps = new Gps(media, amapLocation, "照片");
                        media.setGpsID(gps.getId());
                        DBHelper dbHelper = DBHelper.getInstance(context);
                        //写入数据库
                        Dao<Media, String> mediaDao = dbHelper.getDao(Media.class);
                        mediaDao.create(media);
                        Dao<Gps, String> gpsDao = dbHelper.getDao(Gps.class);
                        gpsDao.create(gps);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onRefreshList();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                L.e("TAG", "User cancelled the image capture");
            } else {
                L.e("TAG", "UImage capture failed, advise user");
            }
        } else if (1000 == requestCode) {//调用自定义的相机，返回來imagePath
            if (Activity.RESULT_OK == resultCode) {
                String imagePath = data.getExtras().getString("imagePath");
                L.e("TAG", "imagePath=" + imagePath);
                if (!TextUtils.isEmpty(imagePath)) {
                    //获取位置信息，经纬度
                    amapLocation = locationFragment.aMapLocation;
                    try {
                        //bitmap上添加坐标信息
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        Bitmap newb = CompressUtils.watermarkBitmap(bitmap, "纬度:" + amapLocation.getLatitude() + "\n" + "经度:" + amapLocation.getLongitude() + "\n" + "时间:" + DateUtil.date2Str(new Date()));
                        CompressUtils.saveBitmap(newb, imagePath);
                        //修改图片的gps属性
//                        GPSutils.setNewThumbnail(imagePath, amapLocation);
                        //创建新的对象
                        Media media = new Media(context, record, imagePath, amapLocation);
                        Gps gps = new Gps(media, amapLocation, "照片");
                        media.setGpsID(gps.getId());
                        DBHelper dbHelper = DBHelper.getInstance(context);
                        //写入数据库
                        Dao<Media, String> mediaDao = dbHelper.getDao(Media.class);
                        mediaDao.create(media);
                        Dao<Gps, String> gpsDao = dbHelper.getDao(Gps.class);
                        gpsDao.create(gps);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onRefreshList();
                }

            }
        }
    }


}
