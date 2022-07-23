import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

import java.util.List;

class InheritExample {
    public int gear;
    public int speed;
    public String name;
    public JSONObject json;

    public InheritExample(int gear, int speed, String name,JSONObject json) {
        this.gear = gear;
        this.speed = speed;
        this.name = name;
        this.json = json;
    }

    public void applyBrake(int decrement) {
        speed -= decrement;
    }

    public void speedUp(int increment) {
        speed += increment;
    }

    public String toString() {
        return "Bicycle [gear = " + gear + ", speed = " + speed + ", name = " + name + "]";
    }
}

class MountainBike extends InheritExample {

    public int seatHeight;
    public String message;
    public List<Object> list;

    public MountainBike(int gear, int speed,
                        int startHeight,List<Object> list ,String name ,JSONObject json, String message) {
        super(gear, speed, name ,json);
        this.list = list;
        this.seatHeight = startHeight;
        this.message = message;
    }

    public void setHeight(int newValue) {
        seatHeight = newValue;
    }

    @Override
    public String toString() {
        return "Mountain Bicycle [gear = " + gear + ", speed = " + speed + ", name = " + name
                + ", message = " + message  + ", json = " + json  +", list = " + list + "]";
    }
}
