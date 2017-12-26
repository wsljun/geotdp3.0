package com.geotdb.compile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geotdb.compile.R;
import com.geotdb.compile.activity.RecordEditActivity;
import com.geotdb.compile.fragment.base.BaseFragment;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.view.MarqueeTextView;
import com.geotdb.compile.view.RoundAngleImageView;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Media;
import com.geotdb.compile.vo.Record;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

/**
 * Created by Administrator on 2016/7/29.
 */
public class HoleSceneFragment extends BaseFragment {
    public static final String EXTRA_HOLE = "hole";

    private CardView scene_operatePerson_rl;
    private CardView scene_operateCode_rl;
    private CardView scene_recordPerson_rl;
    private CardView scene_scene_rl;
    private CardView scene_principal_rl;
    private CardView scene_technician_rl;
    private CardView scene_video_rl;

    private TextView scene_operatePerson_et;
    private TextView scene_operateCode_et;
    private TextView scene_recordPerson_et;
    private TextView scene_scene_et;
    private MarqueeTextView scene_principal_et;
    private TextView scene_technician_et;
    private TextView scene_video_et;

    private RoundAngleImageView scene_operatePerson_iv;
    private RoundAngleImageView scene_operateCode_iv;
    private RoundAngleImageView scene_recordPerson_iv;
    private RoundAngleImageView scene_scene_iv;
    private RoundAngleImageView scene_principal_iv;
    private RoundAngleImageView scene_technician_iv;
    private RoundAngleImageView scene_video_iv;

    private ImageView scene_operatePerson_photo;
    private ImageView scene_operateCode_photo;
    private ImageView scene_recordPerson_photo;
    private ImageView scene_scene_photo;
    private ImageView scene_principal_photo;
    private ImageView scene_technician_photo;
    private ImageView scene_video_photo;


    LinearLayout scene_ll;
    Hole hole;
    Record record = new Record();
    List<Record> record1, record2, record3, record4, record5, record6, record7;
    Media media1, media2, media3, media4, media5, media6, media7;
    private LocalUser localUser;

    String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getArguments().containsKey(EXTRA_HOLE)) {
            hole = (Hole) getArguments().getSerializable(EXTRA_HOLE);
            type = getArguments().getString("holeType");
        }
        localUser = Common.getLocalUser(context);
        final LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_hole_scene, null);
        scene_operatePerson_rl = (CardView) convertView.findViewById(R.id.scene_operatePerson_rl);
        scene_operateCode_rl = (CardView) convertView.findViewById(R.id.scene_operateCode_rl);
        scene_recordPerson_rl = (CardView) convertView.findViewById(R.id.scene_recordPerson_rl);
        scene_scene_rl = (CardView) convertView.findViewById(R.id.scene_scene_rl);
        scene_principal_rl = (CardView) convertView.findViewById(R.id.scene_principal_rl);
        scene_technician_rl = (CardView) convertView.findViewById(R.id.scene_technician_rl);
        scene_video_rl = (CardView) convertView.findViewById(R.id.scene_video_rl);

        scene_ll = (LinearLayout) convertView.findViewById(R.id.scene_ll);
        if ("探井".equals(type)) {
            scene_ll.setVisibility(View.GONE);
        } else {
            scene_ll.setVisibility(View.VISIBLE);
        }

        scene_operatePerson_et = (TextView) convertView.findViewById(R.id.scene_operatePerson_et);
        scene_operateCode_et = (TextView) convertView.findViewById(R.id.scene_operateCode_et);
        scene_recordPerson_et = (TextView) convertView.findViewById(R.id.scene_recordPerson_et);
        scene_scene_et = (TextView) convertView.findViewById(R.id.scene_scene_et);
        scene_principal_et = (MarqueeTextView) convertView.findViewById(R.id.scene_principal_et);
        scene_technician_et = (TextView) convertView.findViewById(R.id.scene_technician_et);
        scene_video_et = (TextView) convertView.findViewById(R.id.scene_video_et);

        scene_operatePerson_iv = (RoundAngleImageView) convertView.findViewById(R.id.scene_operatePerson_iv);
        scene_operateCode_iv = (RoundAngleImageView) convertView.findViewById(R.id.scene_operateCode_iv);
        scene_recordPerson_iv = (RoundAngleImageView) convertView.findViewById(R.id.scene_recordPerson_iv);
        scene_scene_iv = (RoundAngleImageView) convertView.findViewById(R.id.scene_scene_iv);
        scene_principal_iv = (RoundAngleImageView) convertView.findViewById(R.id.scene_principal_iv);
        scene_technician_iv = (RoundAngleImageView) convertView.findViewById(R.id.scene_technician_iv);
        scene_video_iv = (RoundAngleImageView) convertView.findViewById(R.id.scene_video_iv);

        scene_operatePerson_photo = (ImageView) convertView.findViewById(R.id.scene_operatePerson_photo);
        scene_operateCode_photo = (ImageView) convertView.findViewById(R.id.scene_operateCode_photo);
        scene_recordPerson_photo = (ImageView) convertView.findViewById(R.id.scene_recordPerson_photo);
        scene_scene_photo = (ImageView) convertView.findViewById(R.id.scene_scene_photo);
        scene_principal_photo = (ImageView) convertView.findViewById(R.id.scene_principal_photo);
        scene_technician_photo = (ImageView) convertView.findViewById(R.id.scene_technician_photo);
        scene_video_photo = (ImageView) convertView.findViewById(R.id.scene_video_photo);
        initData();
//        unEnabled();
        return convertView;
    }


    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                record1 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_OPERATEPERSON);
                if (null != record1 && record1.size() > 0) {
                    media1 = new MediaDao(context).getMediaByRecordID(record1.get(0).getId());
                }
                record2 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_OPERATECODE);
                if (null != record2 && record2.size() > 0) {
                    media2 = new MediaDao(context).getMediaByRecordID(record2.get(0).getId());
                }
                record3 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_RECORDPERSON);
                if (null != record3 && record3.size() > 0) {
                    media3 = new MediaDao(context).getMediaByRecordID(record3.get(0).getId());
                }
                record4 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_SCENE);
                if (null != record4 && record4.size() > 0) {
                    media4 = new MediaDao(context).getMediaByRecordID(record4.get(0).getId());
                }
                record5 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_PRINCIPAL);
                if (null != record5 && record5.size() > 0) {
                    media5 = new MediaDao(context).getMediaByRecordID(record5.get(0).getId());
                }
                record6 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_TECHNICIAN);
                if (null != record6 && record6.size() > 0) {
                    media6 = new MediaDao(context).getMediaByRecordID(record6.get(0).getId());
                }
                record7 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_VIDEO);
                if (null != record7 && record7.size() > 0) {
                    media7 = new MediaDao(context).getMediaByRecordID(record7.get(0).getId());
                }

                handler.sendMessage(new Message());
            }
        }).start();
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // 机长
            if (null != record1 && record1.size() > 0) {
                if (!TextUtils.isEmpty(record1.get(0).getOperatePerson())) {
                    scene_operatePerson_et.setText("机长:" + record1.get(0).getOperatePerson());
                    scene_operatePerson_photo.setImageResource(R.drawable.ai_icon_havephoto);
                }
                if (null != media1) {
                    scene_operatePerson_iv.setImageURI(Uri.parse(setPic(media1.getLocalPath())));
//                    scene_operatePerson_iv.setImageBitmap(BitmapFactory.decodeFile(setPic(media1.getLocalPath())));
                }
            }
            //钻机
            if (null != record2 && record2.size() > 0) {
                if (null != record2.get(0).getTestType()) {
                    scene_operateCode_et.setText("钻机:" + record2.get(0).getTestType());
                    scene_operateCode_photo.setImageResource(R.drawable.ai_icon_havephoto);
                }
                if (null != media2) {
                    scene_operateCode_iv.setImageURI(Uri.parse((setPic(media2.getLocalPath()))));
                }
            }
            //描述员
            if (null != record3 && record3.size() > 0) {
                if (null != record3.get(0).getRecordPerson()) {
                    scene_recordPerson_et.setText("描述员:" + record3.get(0).getRecordPerson());
                    scene_recordPerson_photo.setImageResource(R.drawable.ai_icon_havephoto);
                }
                if (null != media3) {
                    scene_recordPerson_iv.setImageURI(Uri.parse((setPic(media3.getLocalPath()))));
                }
            }
            scene_recordPerson_et.setText("描述员:" +localUser.getRealName());
            //场景
            if (null != record4 && record4.size() > 0) {
                if (null != media4) {
                    scene_scene_iv.setImageURI(Uri.parse((setPic(media4.getLocalPath()))));
                    scene_scene_et.setHintTextColor(Color.parseColor("#f2000000"));
                    scene_scene_photo.setImageResource(R.drawable.ai_icon_havephoto);
                }
            }
            //负责人
            if (null != record5 && record5.size() > 0) {
                if (!TextUtils.isEmpty(record5.get(0).getOperatePerson())) {
                    scene_principal_et.setText("负责人:" + record5.get(0).getOperatePerson());
                    scene_principal_photo.setImageResource(R.drawable.ai_icon_havephoto);
                }
                if (null != media5) {
                    scene_principal_iv.setImageURI(Uri.parse((setPic(media5.getLocalPath()))));
                }
            }
            //工程师
            if (null != record6 && record6.size() > 0) {
                if (!TextUtils.isEmpty(record6.get(0).getOperatePerson())) {
                    scene_technician_et.setText("工程师:" + record6.get(0).getOperatePerson());
                    scene_technician_photo.setImageResource(R.drawable.ai_icon_havephoto);
                }
                if (null != media6) {
                    scene_technician_iv.setImageURI(Uri.parse((setPic(media6.getLocalPath()))));
                }
            }
            //短视频
            if (null != record7 && record7.size() > 0) {
                if (null != media7) {
                    //视频缩略图地址
                    scene_video_iv.setImageURI(Uri.parse((setPic((media7.getLocalPath())))));
                    scene_video_et.setHintTextColor(Color.parseColor("#f2000000"));
                }
            }
            scene_video_photo.setImageResource(R.drawable.ai_icon_video);
            return true;
        }
    });


    public String setPic(String path) {
        if (path.endsWith("jpg")) {
            return path;
        } else {
            return Common.getPicByDir(path);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scene_operatePerson_rl.setOnClickListener(operatePersonListener);
        scene_operateCode_rl.setOnClickListener(operateCodeListener);
        scene_recordPerson_rl.setOnClickListener(recordPersonListener);
        scene_scene_rl.setOnClickListener(sceneListener);
        scene_principal_rl.setOnClickListener(principalListener);
        scene_technician_rl.setOnClickListener(technicianListener);
        scene_video_rl.setOnClickListener(videoListener);

//        scene_operatePerson_et.setOnClickListener(operatePersonListener);
//        scene_operateCode_et.setOnClickListener(operateCodeListener);
//        scene_recordPerson_et.setOnClickListener(recordPersonListener);
//        scene_scene_et.setOnClickListener(sceneListener);
//        scene_principal_et.setOnClickListener(principalListener);
//        scene_technician_et.setOnClickListener(technicianListener);
//        scene_video_rl.setOnClickListener(videoListener);
    }


    View.OnClickListener operatePersonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record1 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_OPERATEPERSON);
            if (null == record1 || record1.size() == 0) {
                goRecordEditActivity(Record.TYPE_SCENE_OPERATEPERSON, 100);
            } else {
                goEdit(record1.get(0), 100);
            }
        }
    };
    View.OnClickListener operateCodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record2 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_OPERATECODE);
            if (null == record2 || record2.size() == 0) {
                goRecordEditActivity(Record.TYPE_SCENE_OPERATECODE, 200);
            } else {
                goEdit(record2.get(0), 200);
            }
        }
    };
    View.OnClickListener recordPersonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record3 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_RECORDPERSON);
            if (null == record3 || record3.size() == 0) {
                goRecordEditActivity(Record.TYPE_SCENE_RECORDPERSON, 300);
            } else {
                goEdit(record3.get(0), 300);
            }


        }
    };
    View.OnClickListener sceneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record4 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_SCENE);
            if (null == record4 || record4.size() == 0) {
                goRecordEditActivity(Record.TYPE_SCENE_SCENE, 400);
            } else {
                goEdit(record4.get(0), 400);
            }

        }
    };
    View.OnClickListener principalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record5 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_PRINCIPAL);
            if (null == record5 || record5.size() == 0) {
                goRecordEditActivity(Record.TYPE_SCENE_PRINCIPAL, 500);
            } else {
                goEdit(record5.get(0), 500);
            }

        }
    };
    View.OnClickListener technicianListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record6 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_TECHNICIAN);
            if (null == record6 || record6.size() == 0) {
                goRecordEditActivity(Record.TYPE_SCENE_TECHNICIAN, 600);
            } else {
                goEdit(record6.get(0), 600);
            }

        }
    };

    View.OnClickListener videoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record7 = record.getRecordByType(context, hole.getId(), Record.TYPE_SCENE_VIDEO);
            if (null == record7 || record7.size() == 0) {
                goRecordEditActivity(Record.TYPE_SCENE_VIDEO, 700);
            } else {
                goEdit(record7.get(0), 700);
            }
        }
    };

    //添加的时候传递hole
    public void goRecordEditActivity(String type, int requestCode) {
        Log.e("TAG", "------------>>>goRecordEditActivity");
        Intent intent = new Intent();
        intent.setClass(context, RecordEditActivity.class);
        intent.putExtra(RecordEditActivity.EXTRA_HOLE, hole);
        intent.putExtra(RecordEditActivity.EXTRA_RECORD_TYPE, type);
        startActivityForResult(intent, requestCode);
    }

    //编辑的时候传递record
    public void goEdit(Record record, int requestCode) {
        Log.e("TAG", "------------>>>goEdit");
        Intent intent = new Intent();
        intent.setClass(context, RecordEditActivity.class);
        intent.putExtra(RecordEditActivity.EXTRA_RECORD, record);
        intent.putExtra(RecordEditActivity.EXTRA_HOLE, hole);
        startActivityForResult(intent, requestCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "requestCode=" + requestCode + "---resultCode=" + resultCode);
        if (resultCode == -1) {
            Record record = (Record) data.getExtras().get("record");
            //根据recordID获取图片
            Media media = new MediaDao(context).getMediaByRecordID(record.getId());
            switch (requestCode) {
                case 100:
                    if (null != media) {
                        scene_operatePerson_iv.setImageURI(Uri.parse(setPic(media.getLocalPath())));
                        scene_operatePerson_photo.setImageResource(R.drawable.ai_icon_havephoto);
                    } else {
//                        scene_operatePerson_iv.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
                        scene_operatePerson_photo.setImageResource(R.drawable.ai_icon_photo);
                    }
                    if (null != record.getOperatePerson()) {
                        scene_operatePerson_et.setText("机长:" + record.getOperatePerson());
                    } else {
                        scene_operatePerson_et.setText("");
                    }
                    break;
                case 200:
                    if (null != media) {
                        scene_operateCode_iv.setImageURI(Uri.parse(setPic(media.getLocalPath())));
                        scene_operateCode_photo.setImageResource(R.drawable.ai_icon_havephoto);
                    } else {
//                        scene_operateCode_iv.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
                        scene_operateCode_photo.setImageResource(R.drawable.ai_icon_havephoto);
                    }
                    if (null != record.getTestType()) {
                        scene_operateCode_et.setText("钻机:" + record.getTestType());
                    } else {
                        scene_operateCode_et.setText("");
                    }
                    break;
                case 300:
                    if (null != media) {
                        scene_recordPerson_iv.setImageURI(Uri.parse(setPic(media.getLocalPath())));
                        scene_recordPerson_photo.setImageResource(R.drawable.ai_icon_havephoto);
                    } else {
//                        scene_recordPerson_iv.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
                        scene_recordPerson_photo.setImageResource(R.drawable.ai_icon_photo);
                    }
                    //描述员采用当前用户信息中的真是姓名
//                    if (null != record.getRecordPerson()) {
//                        scene_recordPerson_et.setText(record.getRecordPerson());
//                    } else {
//                        scene_recordPerson_et.setText("");
//                    }
                    break;
                case 400:
                    if (null != media) {
                        scene_scene_iv.setImageURI(Uri.parse(setPic(media.getLocalPath())));
//                        scene_scene_et.setHintTextColor(Color.parseColor("#f2000000"));
                        scene_scene_photo.setImageResource(R.drawable.ai_icon_havephoto);
                    } else {
//                        scene_scene_iv.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
//                        scene_scene_et.setHintTextColor(Color.parseColor("#9aa7a7a7"));
                        scene_scene_photo.setImageResource(R.drawable.ai_icon_photo);
                    }
//                    scene_scene_et
                    break;
                case 500:
                    if (null != media) {
                        scene_principal_iv.setImageURI(Uri.parse(setPic(media.getLocalPath())));
                        scene_principal_photo.setImageResource(R.drawable.ai_icon_havephoto);
                    } else {
//                        scene_principal_iv.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
                        scene_principal_photo.setImageResource(R.drawable.ai_icon_photo);
                    }
                    if (null != record.getOperatePerson()) {
                        scene_principal_et.setText("负责人:" + record.getOperatePerson());
                    } else {
                        scene_principal_et.setText("");
                    }
                    break;
                case 600:
                    if (null != media) {
                        scene_technician_iv.setImageURI(Uri.parse(setPic(media.getLocalPath())));
                        scene_technician_photo.setImageResource(R.drawable.ai_icon_havephoto);
                    } else {
//                        scene_technician_iv.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
                        scene_technician_photo.setImageResource(R.drawable.ai_icon_photo);
                    }
                    if (null != record.getOperatePerson()) {
                        scene_technician_et.setText("工程师:" + record.getOperatePerson());
                        scene_technician_et.setSelected(true);
                    } else {
                        scene_technician_et.setText("");
                    }
                    break;
                case 700:
                    if (null != media) {
                        scene_video_iv.setImageURI(Uri.parse(setPic(media.getLocalPath())));
//                        scene_video_et.setHintTextColor(Color.parseColor("#f2000000"));
                        scene_video_photo.setImageResource(R.drawable.ai_icon_video);
                    } else {
//                        scene_video_iv.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
//                        scene_video_et.setHintTextColor(Color.parseColor("#9aa7a7a7"));
                        scene_video_photo.setImageResource(R.drawable.ai_icon_video);
                    }

                    break;

            }
        }

    }


}
