package com.geotdb.compile.view.camera;

import android.hardware.Camera;

import com.geotdb.compile.utils.L;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/24.
 */
public class CameraParameter {
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraParameter myCamPara = null;

    private CameraParameter() {

    }

    public static CameraParameter getInstance() {
        if (myCamPara == null) {
            myCamPara = new CameraParameter();
            return myCamPara;
        } else {
            return myCamPara;
        }
    }

    public Camera.Size getPreviewSize(List<Camera.Size> list, int th, float surWh) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        L.i("list.size= " + list.size());
        for (Camera.Size s : list) {
            L.i("所有尺寸---w=" + s.width + "--h=" + s.height);
            if ((s.width > th) && equalRate(s, surWh)) {
                L.i("最终设置预览尺寸:w = " + s.width + "--h = " + s.height + "----surWh---" + surWh);
                break;
            }
            i++;
        }
        return list.get(i);
    }

    public Camera.Size getPictureSize(List<Camera.Size> list, int th, float surWh) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        L.i("list.size= " + list.size());
        for (Camera.Size s : list) {
            L.i("所有尺寸---w=" + s.width + "--h=" + s.height);
            if ((s.width > th) && equalRate(s, surWh)) {
                L.i("最终设置图片尺寸:w = " + s.width + "--h = " + s.height + "----surWh---" + surWh);
                break;
            }
            i++;
        }
        return list.get(i);
    }

    public boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }

    public class CameraSizeComparator implements Comparator<Camera.Size> {
        //按升序排列
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
