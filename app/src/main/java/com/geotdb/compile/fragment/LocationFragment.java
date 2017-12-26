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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.geotdb.compile.R;
import com.geotdb.compile.activity.RecordEditBaseActivity;
import com.geotdb.compile.fragment.base.BaseFragment;
import com.geotdb.compile.utils.Common;
import com.geotdb.compile.utils.SPUtils;
import com.geotdb.compile.utils.ToastUtil;
import com.geotdb.compile.vo.Hole;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationFragment extends BaseFragment implements LocationSource, AMapLocationListener {
    public Context context;
    public Activity activity;

    public MaterialEditText edtLongitude;
    public MaterialEditText edtLatitude;
    public MaterialEditText edtLocationTime;
    public MaterialEditText edtAccuracy;

    public AMapLocation aMapLocation = null;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private LocationSource.OnLocationChangedListener mListener = null;

    Hole hole;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
        if (getArguments().containsKey(RecordEditBaseActivity.EXTRA_HOLE)) {
            hole = (Hole) getArguments().getSerializable(RecordEditBaseActivity.EXTRA_HOLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.frt_record_add_base, null);

        edtLongitude = (MaterialEditText) convertView.findViewById(R.id.edtLongitude);
        edtLatitude = (MaterialEditText) convertView.findViewById(R.id.edtLatitude);
        edtLocationTime = (MaterialEditText) convertView.findViewById(R.id.edtLocationTime);
        edtAccuracy = (MaterialEditText) convertView.findViewById(R.id.edtAccuracy);
        location();
        return convertView;
    }


    public void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
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
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }


    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
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
                //定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                edtLocationTime.setText(df.format(date));
                //偏移值
                if (null != hole) {
                    edtAccuracy.setText(Common.GetDistance(aMapLocation.getLongitude(), aMapLocation.getLatitude(), Double.valueOf(hole.getMapLongitude()), Double.valueOf(hole.getMapLatitude())));
                    aMapLocation.setAccuracy(Float.valueOf(edtAccuracy.getText().toString()));
                }
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ToastUtil.showToastS(context, "信号弱,请耐心等待");
            }
        }
    }

}
