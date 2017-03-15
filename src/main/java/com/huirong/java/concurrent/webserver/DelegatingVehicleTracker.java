package com.huirong.java.concurrent.webserver;

import com.huirong.java.concurrent.Point;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huirong on 17-3-15.
 * 委托模式
 */
@ThreadSafe
public class DelegatingVehicleTracker {
    private final ConcurrentHashMap<String, Point> locations;
    private final Map<String, Point> unmodifiableMap;

    public DelegatingVehicleTracker(ConcurrentHashMap<String, Point> locations) {
        this.locations = locations;
        //维护location的一个只读模式的快照, 会实时进行更新
        this.unmodifiableMap = Collections.unmodifiableMap(locations);
    }

    public Map<String, Point> getLocations(){
        return unmodifiableMap;
    }

    //返回一个静态的拷贝
    public Map<String, Point> getStaticLocations(){
        return Collections.unmodifiableMap(new HashMap<>(locations));
    }

    public Point getLocation(String id){
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y){
        if (locations.replace(id, new Point(x, y)) == null){
            throw new IllegalArgumentException("id 不存在");
        }
    }
}
