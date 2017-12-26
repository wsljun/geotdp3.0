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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.geotdb.compile.R;
import com.geotdb.compile.fragment.base.BaseFragment;
import com.geotdb.compile.db.DBHelper;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.SPUtils;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Hole;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class HoleLocationFragment extends BaseFragment implements LocationSource, AMapLocationListener {
    public static final String EXTRA_HOLE = "hole";

    public Context context;
    public Activity activity;

    public MaterialEditText edtLongitude;
    public MaterialEditText edtLatitude;
    public MaterialEditText edtLocationTime;
    public MaterialEditText edtZoom;

    private MapView mMapView = null;
    private AMap aMap;
    private AMapLocation aMapLocation = null;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private MarkerOptions markerOption;
    private Marker marker;
    private boolean isFirstLoc = true;
    String projectID;
    Hole hole;
    List<Hole> list;
    DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
        if (getArguments().containsKey(EXTRA_HOLE)) {
            hole = (Hole) getArguments().getSerializable(EXTRA_HOLE);
            projectID = hole.getProjectID();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_hole_location_base, null);

        edtLongitude = (MaterialEditText) convertView.findViewById(R.id.edtLongitude);
        edtLatitude = (MaterialEditText) convertView.findViewById(R.id.edtLatitude);
        edtLocationTime = (MaterialEditText) convertView.findViewById(R.id.edtLocationTime);
        edtZoom = (MaterialEditText) convertView.findViewById(R.id.edtZoom);

        mMapView = (MapView) convertView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mMapView.getMap();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听
            // 是否显示指南针
//            settings.setCompassEnabled(true);
            //Map的一些风格
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
            myLocationStyle.strokeColor(Color.argb(50, 30, 1360, 229));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(15, 30, 1360, 229));// 设置圆形的填充颜色
            // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
            myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setOnCameraChangeListener(onCameraChangeListener);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
            //解决ScorllView冲突问题
            aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent motionEvent) {
                    mMapView.getParent().getParent().requestDisallowInterceptTouchEvent(true);  //scorllview是父控件就用给一个getParent就好了，多个就用多个 getParent
                }
            });
        }
        doLocation();
        return convertView;
    }


    private boolean mWorking = true;
    private Thread mThread;

    /**
     * 获取定位信息之后，启动线程，获取其他的点，并与当前的点坐标进行比较，离得近的10个点，在地图上标注出来
     */
    public void loadData() {
        if (mThread != null && mThread.isAlive()) {
            L.e("TAG", "start: thread is alive");
        } else {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mWorking) {
                        mWorking = false;
                        list = getList(projectID);
                        handler.sendMessage(new Message());
                    }
                }
            });
            mThread.start();
        }
    }

    //为地图上添加其他的点
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            addMarkersToMap();
        }
    };

    //获取所有的点信息
    private List<Hole> getList(String projectID) {
        list = new ArrayList<Hole>();
        dbHelper = DBHelper.getInstance(getActivity());
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            String allSql = "select id,code,type,state,recordsCount,updateTime,mapLatitude,mapLongitude,mapTime from hole where projectID='" + projectID + "' and locationState ='0' and id <> '" + hole.getId() + "' order by updateTime desc";
            String oneSql = "select id,code,type,state,recordsCount,updateTime,cast(mapLatitude as text),cast(mapLongitude as text),mapTime ,abs(mapLatitude - " + aMapLocation.getLatitude() + ") + abs(mapLongitude - " + aMapLocation.getLongitude() + ") as  adsvalue  from hole where projectID='" + projectID + "' and locationState ='0' and id <> '" + hole.getId() + "'order by  adsvalue LIMIT 10";
            L.e("TAG", oneSql);
            GenericRawResults<Hole> results = dao.queryRaw(oneSql, new RawRowMapper<Hole>() {
                @Override
                public Hole mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Hole hole = new Hole();
                    hole.setId(resultColumns[0]);
                    hole.setCode(resultColumns[1]);
                    hole.setType(resultColumns[2]);
                    hole.setState(resultColumns[3]);
                    hole.setRecordsCount(resultColumns[4]);
                    hole.setUpdateTime(resultColumns[5]);
                    hole.setMapLatitude(resultColumns[6]);
                    hole.setMapLongitude(resultColumns[7]);
                    hole.setMapTime(resultColumns[8]);
                    hole.jieMi();
                    return hole;
                }
            });
            Iterator<Hole> iterator = results.iterator();
            while (iterator.hasNext()) {
                Hole hole = iterator.next();
                list.add(hole);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    //EditActivity按钮点击
    public AMapLocation location() {
        if (marker != null) {
            marker.destroy();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        drawMarkers("当前勘探点", aMapLocation.getLatitude(), aMapLocation.getLongitude(), String.valueOf(df.format(new Date(aMapLocation.getTime()))));
        return aMapLocation;
    }


    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        L.e("TAG", "list===" + list.size());
        for (Hole hole : list) {
            L.e("TAG", "LatLng=" + hole.getMapLatitude() + "----Long" + hole.getMapLongitude());
            aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .position(new LatLng(Double.valueOf(hole.getMapLatitude()), Double.valueOf(hole.getMapLongitude()))).title(hole.getCode())
                    .snippet(hole.getMapLatitude() + ":" + hole.getMapLongitude()).draggable(true));
        }
    }

    AMap.OnCameraChangeListener onCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            System.out.println("onCameraChange");

        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            edtZoom.setText(String.valueOf(cameraPosition.zoom));
        }
    };


    private void doLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        switch ((int) SPUtils.get(context, "map_mode", 0)) {
            case 1:
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                break;
            case 2:
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                break;
            case 3:
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
                break;
        }
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void stop(){
        mLocationClient.stopLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                // 定位成功回调信息，设置相关消息
                edtLongitude.setText(String.valueOf(aMapLocation.getLongitude()));
                edtLatitude.setText(String.valueOf(aMapLocation.getLatitude()));

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                edtLocationTime.setText(df.format(date));
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    isFirstLoc = false;
                }
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);
                }
                //启动获取其他点的线程
                int i = 1;
                if (i == 1) {
                    i++;
                    loadData();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ToastUtil.showToastS(context, "信号弱,请耐心等待");
                this.aMapLocation = null;
                edtLongitude.setText("");
                edtLatitude.setText("");
                edtLocationTime.setText("");
            }
        }
    }


    /**
     * 绘制系统默认的1种marker背景图片
     */
    public void drawMarkers(String title, double latitude, double longitude, String time) {
        markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude, longitude));
        markerOption.title(title).snippet("基准坐标:" + latitude + "," + longitude + "\n" + "基准时间:" + time);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = aMap.addMarker(markerOption);

        // marker旋转90度
        marker.showInfoWindow();
    }


}
