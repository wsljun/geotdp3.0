package com.geotdb.compile.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.amap.api.navi.model.NaviLatLng;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;
import com.geotdb.compile.adapter.ReleteLocationAdapter;
import com.geotdb.compile.utils.L;
import com.geotdb.compile.utils.SPUtils;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.utils.Urls;
import com.geotdb.compile.vo.Hole;
import com.geotdb.compile.vo.JsonResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/5/11.
 */
public class ReleteLocationActivity extends BaseAppCompatActivity implements LocationSource, AMapLocationListener {
    private MapView mMapView = null;
    private AMap aMap;
    private AMapLocation aMapLocation = null;

    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    //选定要去的点
    private Marker goMarker;
    private List<Hole> list;
    private RecyclerView relate_location_recyclerView;
    private ReleteLocationAdapter releteLocationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_relate_location);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.relate_location_toolbar);
        toolbar.setTitle("发布点");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);


        mMapView = (MapView) findViewById(R.id.relate_location_map);
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
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
            //解决ScorllView冲突问题
            aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent motionEvent) {
                    mMapView.getParent().getParent().requestDisallowInterceptTouchEvent(true);  //scorllview是父控件就用给一个getParent就好了，多个就用多个 getParent
                }
            });
        }

        relate_location_recyclerView = (RecyclerView) findViewById(R.id.relate_location_recyclerView);
        relate_location_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doLocation();
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_relate_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.act_save:
                if (goMarker != null) {
                    startAMapNavi(goMarker);
                } else {
                    ToastUtil.showToastS(this, "请选择要去的位置");
                }
                return true;
            case R.id.act_help:
                ToastUtil.showToastS(this, "未添加");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取勘察点列表
     */
    public void getHoleListForIntrnet(String serialNumber) {
        showProgressDialog(false);
        Map<String, String> params = new HashMap<>();
        params.put("serialNumber", serialNumber);
        OkHttpUtils.post().url(Urls.GET_RELATE_HOLE).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                list = null;
                L.e("getHoleListForIntrnet--onError-->>" + e.getMessage());
                ToastUtil.showToastS(context, "获取勘察点失败");
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                L.e("TAG", "getHoleListForIntrnet--response-->>" + response);
                dismissProgressDialog();
                Gson gson = new Gson();
                JsonResult jsonResult = gson.fromJson(response.toString(), JsonResult.class);
                if (jsonResult.getStatus()) {
                    list = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Hole>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        addMarkersToMap();
                        createListView();
                    } else {
                        ToastUtil.showToastS(context, "服务端未创建勘察点，无法获取");
                    }
                } else {
                    ToastUtil.showToastS(context, jsonResult.getMessage());
                }


            }
        });
    }

    /**
     * 获取list之后 创建listview
     */
    private void createListView() {
        releteLocationAdapter = new ReleteLocationAdapter(list);
        relate_location_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        relate_location_recyclerView.setAdapter(releteLocationAdapter);
        releteLocationAdapter.setOnItemListener(onItemListener);
    }

    ReleteLocationAdapter.OnItemListener onItemListener = new ReleteLocationAdapter.OnItemListener() {
        @Override
        public void onClick(int position) {
            if (list.get(position).getLatitude() != null && list.get(position).getLongitude() != null) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(list.get(position).getLatitude()), Double.parseDouble(list.get(position).getLongitude())), 19));
            } else {
                ToastUtil.showToastS(ReleteLocationActivity.this, "没有位置信息");
            }
        }
    };


    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        for (Hole hole : list) {
            L.e("TAG", "Lat=" + hole.getLatitude() + "----Long" + hole.getLongitude());
            if (hole.getLatitude() != null && !"".equals(hole.getLatitude()) && hole.getLongitude() != null && !"".equals(hole.getLongitude())) {
                MarkerOptions options = new MarkerOptions();
                options.anchor(0.5f, 0.5f);
                options.position(new LatLng(Double.valueOf(hole.getLatitude()), Double.valueOf(hole.getLongitude())));
                options.title(hole.getCode());
                options.snippet(hole.getLongitude() + "," + hole.getLatitude());
                options.draggable(false);
                aMap.addMarker(options);
                aMap.setOnMarkerClickListener(listener);
            }
        }
    }

    AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            L.e("----------listener----------");
            marker.showInfoWindow();
            goMarker = marker;
            return true;
        }
    };

    /**
     * 点击一键导航按钮跳转到导航页面
     *
     * @param marker
     */
    private void startAMapNavi(Marker marker) {
        if (aMapLocation == null) {
            return;
        }
        Intent intent = new Intent(this, RouteNaviActivity.class);
        intent.putExtra("gps", false);
        intent.putExtra("start", new NaviLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
        intent.putExtra("end", new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));

        startActivity(intent);
    }

    public void stop() {
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
    protected void onDestroy() {
        super.onDestroy();
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

    private boolean isFirstLoc = true;
    private int i = 1;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    L.e("---------moveCamera---------" + isFirstLoc);
                    isFirstLoc = false;
                    stop();
                }
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);
                }
                //启动获取其他点的线程
                if (i == 1) {
                    i++;
                    getHoleListForIntrnet(getIntent().getExtras().getString("serialNumber"));
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ToastUtil.showToastS(context, "信号弱,请耐心等待");
                this.aMapLocation = null;
            }
        }
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

}
