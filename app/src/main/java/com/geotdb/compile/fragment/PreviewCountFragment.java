package com.geotdb.compile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.geotdb.compile.activity.PreviewActivity;
import com.geotdb.compile.R;
import com.geotdb.compile.db.GpsDao;
import com.geotdb.compile.db.MediaDao;
import com.geotdb.compile.db.RecordDao;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.vo.Gps;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.LocalUser;
import com.geotdb.compile.vo.Record;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class PreviewCountFragment extends Fragment {
    private static PreviewCountFragment instance = null;

    public static PreviewCountFragment newInstance() {
        if (instance == null) {
            instance = new PreviewCountFragment();
        }
        return instance;
    }

    public PreviewCountFragment() {
    }

    private LocalUser localUser;
    private Hole hole;
    private Record person, op, oc, scene;
    private int person_num = 0;//描述员照片数
    private int op_num = 0;//机长照片数
    private int oc_num = 0;//钻机照片数
    private int scene_num = 0;//场景照片数
    private int photoNumber = 0;//点总照片数
    private Map<Integer, Integer> countMap;
    private Context context;
    private MaterialEditText preview_count_person;//描述员
    private MaterialEditText preview_count_person_num;//描述员照片数量
    private MaterialEditText preview_count_op;//机长
    private MaterialEditText preview_count_op_num;//机长照片数量
    private MaterialEditText preview_count_oc;//钻机
    private MaterialEditText preview_count_oc_num;//钻机数量
    private MaterialEditText preview_count_scene;//场景
    private MaterialEditText preview_count_scene_num;//场景数量
    private MaterialEditText preview_count_frequency;//回次
    private MaterialEditText preview_count_layer;//岩土
    private MaterialEditText preview_count_water;//水位
    private MaterialEditText preview_count_getlayer;//取土
    private MaterialEditText preview_count_getwater;//取水
    private MaterialEditText preview_count_dpt;//动探
    private MaterialEditText preview_count_spt;//标贯
    private MaterialEditText preview_count_photo;//总照片数
    private MaterialEditText preview_count_totaltime;//总时间
    private MaterialEditText preview_count_worktime;//时间分布
    //位置偏移数量与比例
    private MaterialEditText preview_count_place1, preview_count_place2, preview_count_place3, preview_count_place4;
    private String place1 = "";
    private String place2 = "";
    private String place3 = "";
    private String place4 = "";
    private String totalTime = "";
    private String wordTime = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments().containsKey(PreviewActivity.EXTRA_HOLE)) {
            hole = (Hole) getArguments().getSerializable(PreviewActivity.EXTRA_HOLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frt_preview_count, container, false);
        initView(view);
        loadData();
        return view;
    }

    private void initView(View view) {
        preview_count_person = (MaterialEditText) view.findViewById(R.id.preview_count_person);
        preview_count_person_num = (MaterialEditText) view.findViewById(R.id.preview_count_person_num);
        preview_count_op = (MaterialEditText) view.findViewById(R.id.preview_count_op);
        preview_count_op_num = (MaterialEditText) view.findViewById(R.id.preview_count_op_num);
        preview_count_oc = (MaterialEditText) view.findViewById(R.id.preview_count_oc);
        preview_count_oc_num = (MaterialEditText) view.findViewById(R.id.preview_count_oc_num);
        preview_count_scene = (MaterialEditText) view.findViewById(R.id.preview_count_scene);
        preview_count_scene_num = (MaterialEditText) view.findViewById(R.id.preview_count_scene_num);
        preview_count_frequency = (MaterialEditText) view.findViewById(R.id.preview_count_frequency);
        preview_count_layer = (MaterialEditText) view.findViewById(R.id.preview_count_layer);
        preview_count_water = (MaterialEditText) view.findViewById(R.id.preview_count_water);
        preview_count_getlayer = (MaterialEditText) view.findViewById(R.id.preview_count_getlayer);
        preview_count_getwater = (MaterialEditText) view.findViewById(R.id.preview_count_getwater);
        preview_count_dpt = (MaterialEditText) view.findViewById(R.id.preview_count_dpt);
        preview_count_spt = (MaterialEditText) view.findViewById(R.id.preview_count_spt);
        preview_count_photo = (MaterialEditText) view.findViewById(R.id.preview_count_photo);
        preview_count_totaltime = (MaterialEditText) view.findViewById(R.id.preview_count_totaltime);
        preview_count_worktime = (MaterialEditText) view.findViewById(R.id.preview_count_worktime);
        preview_count_place1 = (MaterialEditText) view.findViewById(R.id.preview_count_place1);
        preview_count_place2 = (MaterialEditText) view.findViewById(R.id.preview_count_place2);
        preview_count_place3 = (MaterialEditText) view.findViewById(R.id.preview_count_place3);
        preview_count_place4 = (MaterialEditText) view.findViewById(R.id.preview_count_place4);


    }

    public void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                localUser = Common.getLocalUser(context);
                RecordDao recordDao = new RecordDao(context);
                //每条记录的个数
                countMap = recordDao.getSortCountMap(hole.getId());
                //四条 特殊记录
                person = recordDao.getRecordByType(hole.getId(), Record.TYPE_SCENE_RECORDPERSON);
                op = recordDao.getRecordByType(hole.getId(), Record.TYPE_SCENE_OPERATEPERSON);
                oc = recordDao.getRecordByType(hole.getId(), Record.TYPE_SCENE_OPERATECODE);
                scene = recordDao.getRecordByType(hole.getId(), Record.TYPE_SCENE_SCENE);
                MediaDao mediaDao = new MediaDao(context);
                //照片总数
                photoNumber = mediaDao.getMediaCountByHoleID(hole.getId());
                //四条 特殊记录的照片数
                if (person != null) {
                    person_num = mediaDao.getMediaCountByrdcordID(person.getId());
                }
                if (op != null) {
                    op_num = mediaDao.getMediaCountByrdcordID(op.getId());
                }
                if (oc != null) {
                    oc_num = mediaDao.getMediaCountByrdcordID(oc.getId());
                }
                if (scene != null) {
                    scene_num = mediaDao.getMediaCountByrdcordID(scene.getId());
                }
                GpsDao gpsDao = new GpsDao(context);
                //找出所有的gps的偏移，并计算个数和所占比例
                List<Gps> gpsList = gpsDao.getGpsListByHoleID(hole.getId());
                double p1 = 0;
                double p2 = 0;
                double p3 = 0;
                double p4 = 0;
                if (null != gpsList) {
                    for (Gps gps : gpsList) {
                        double l = Double.valueOf(gps.getDistance());
                        if (l <= 5) {
                            p1++;
                        }
                        if (5 < l && l <= 15) {
                            p2++;
                        }
                        if (15 < l && l <= 25) {
                            p3++;
                        }
                        if (25 < l) {
                            p4++;
                        }
                    }
                    int total = gpsList.size();
                    place1 = p1 + getFormat(p1, total);
                    place2 = p2 + getFormat(p2, total);
                    place3 = p3 + getFormat(p3, total);
                    place4 = p4 + getFormat(p4, total);
                }

                //找出最后一个gps的时间
                Gps lastGps = gpsDao.getGpsByHoleID(hole.getId());
                if (lastGps != null) {
                    String lastTime = lastGps.getGpsTime();
                    totalTime = hole.getCreateTime() + " ~ " + lastTime;
                    //工作用时
                    try {
                        wordTime = stringDaysBetween(hole.getCreateTime(), lastTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendMessage(new Message());
            }
        }).start();
    }

    private String getFormat(double num, int total) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后几位
        numberFormat.setMaximumFractionDigits(1);
        String result = numberFormat.format((float) num / (float) total * 100);
        return " (" + result + "%" + ")";
    }

    public String stringDaysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1);
//        return Integer.parseInt(String.valueOf(between_days));
        long days = between_days / (1000 * 60 * 60 * 24);
        long hours = (between_days % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (between_days % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (between_days % (1000 * 60)) / 1000;
        return days + " 天 " + hours + " 小时 " + minutes + " 分 " + seconds + " 秒 ";

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //描述员暂时是user里的真是姓名
            preview_count_person.setText(localUser.getRealName());
            if (person != null) {
//                preview_count_person.setText(person.getOperatePerson());
                preview_count_person_num.setText(person_num + "");
            }
            if (op != null) {
                preview_count_op.setText(op.getOperatePerson());
                preview_count_op_num.setText(op_num + "");
            }
            if (oc != null) {
                preview_count_oc.setText(oc.getTestType());
                preview_count_oc_num.setText(oc_num + "");
            }
            if (scene != null) {
                //场景就叫场景.....
                preview_count_scene.setText("场景");
                preview_count_scene_num.setText(scene_num + "");
            }
            preview_count_frequency.setText(countMap.get(2) + "");
            preview_count_layer.setText(countMap.get(3) + "");
            preview_count_water.setText(countMap.get(4) + "");
            preview_count_dpt.setText(countMap.get(5) + "");
            preview_count_spt.setText(countMap.get(6) + "");
            preview_count_getlayer.setText(countMap.get(7) + "");
            preview_count_getwater.setText(countMap.get(8) + "");
            preview_count_photo.setText(photoNumber + "");

            preview_count_totaltime.setText(totalTime);
            //定位位置间隔
            preview_count_place1.setText(place1);
            preview_count_place2.setText(place2);
            preview_count_place3.setText(place3);
            preview_count_place4.setText(place4);
            //
            preview_count_worktime.setText(wordTime);
        }
    };


}
