package com.aionemu.gameserver.geoEngine.collision.bih;

import com.aionemu.gameserver.geoEngine.math.FastMath;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

public final class BIHTriangle {

    private final Vector3f pointa = new Vector3f();
    private final Vector3f pointb = new Vector3f();
    private final Vector3f pointc = new Vector3f();
    private final Vector3f center = new Vector3f();

    public BIHTriangle(Vector3f p1, Vector3f p2, Vector3f p3) {
        pointa.set(p1);
        pointb.set(p2);
        pointc.set(p3);
        center.set(pointa);
        center.addLocal(pointb).addLocal(pointc).multLocal(FastMath.ONE_THIRD);
    }

    public Vector3f get1(){
        return pointa;
    }

    public Vector3f get2(){
        return pointb;
    }

    public Vector3f get3(){
        return pointc;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getNormal(){
        Vector3f normal = new Vector3f(pointb);
        normal.subtractLocal(pointa).crossLocal(pointc.x-pointa.x, pointc.y-pointa.y, pointc.z-pointa.z);
        normal.normalizeLocal();
        return normal;
    }

    public float getExtreme(int axis, boolean left){
        float v1, v2, v3;
        switch (axis){
            case 0: v1 = pointa.x; v2 = pointb.x; v3 = pointc.x; break;
            case 1: v1 = pointa.y; v2 = pointb.y; v3 = pointc.y; break;
            case 2: v1 = pointa.z; v2 = pointb.z; v3 = pointc.z; break;
            default: assert false; return 0;
        }
        if (left){
            if (v1 < v2){
                if (v1 < v3)
                    return v1;
                else
                    return v3;
            }else{
                if (v2 < v3)
                    return v2;
                else
                    return v3;
            }
        }else{
            if (v1 > v2){
                if (v1 > v3)
                    return v1;
                else
                    return v3;
            }else{
                if (v2 > v3)
                    return v2;
                else
                    return v3;
            }
        }
    }
}