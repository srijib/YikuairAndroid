package com.bestapp.yikuair.utils;

import com.baidu.mapapi.map.LocationData;


public interface LocationChangeListener {
    /**
     * 定位坐标类型定义
     */
    public enum CoordType {
        /**
         * 百度经纬度坐标
         */
        CoordType_BD09LL,
        /**
         * 百度坐标
         */
        CoordType_BD09,
    }

    ;

    /**
     * 定位变化的位置回调
     *
     * @param locData
     * @see com.baidu.baidumaps.api.location.LocationManager.LocData
     */
    public abstract void onLocationChange(LocationData locData);

    /**
     * 设置listener需要的坐标类型
     *
     * @return 坐标类型
     * @see CoordType
     */
    public abstract CoordType onGetCoordType();

}
