package com.batuhan.smartcampus.store;

import com.batuhan.smartcampus.model.Room;
import com.batuhan.smartcampus.model.Sensor;
import com.batuhan.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore {

    public static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> SENSOR_READINGS = new ConcurrentHashMap<>();

    static {
        Room room1 = new Room("LIB-301", "Library Quiet Study", 40);
        Room room2 = new Room("ENG-101", "Engineering Lab", 25);

        ROOMS.put(room1.getId(), room1);
        ROOMS.put(room2.getId(), room2);

        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 21.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "ACTIVE", 420.0, "ENG-101");
        Sensor sensor3 = new Sensor("TEMP-999", "Temperature", "MAINTENANCE", 19.0, "LIB-301");

        SENSORS.put(sensor1.getId(), sensor1);
        SENSORS.put(sensor2.getId(), sensor2);
        SENSORS.put(sensor3.getId(), sensor3);

        ROOMS.get("LIB-301").getSensorIds().add("TEMP-001");
        ROOMS.get("ENG-101").getSensorIds().add("CO2-001");
        ROOMS.get("LIB-301").getSensorIds().add("TEMP-999");

        SENSOR_READINGS.put("TEMP-001", new ArrayList<SensorReading>());
        SENSOR_READINGS.put("CO2-001", new ArrayList<SensorReading>());
        SENSOR_READINGS.put("TEMP-999", new ArrayList<SensorReading>());
    }

    private InMemoryStore() {
    }
}