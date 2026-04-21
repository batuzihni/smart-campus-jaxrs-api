package com.batuhan.smartcampus.resource;

import com.batuhan.smartcampus.exception.DuplicateResourceException;
import com.batuhan.smartcampus.exception.InvalidInputException;
import com.batuhan.smartcampus.exception.ResourceNotFoundException;
import com.batuhan.smartcampus.exception.SensorUnavailableException;
import com.batuhan.smartcampus.model.Sensor;
import com.batuhan.smartcampus.model.SensorReading;
import com.batuhan.smartcampus.store.InMemoryStore;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getSensorReadings() {
        Sensor sensor = InMemoryStore.SENSORS.get(sensorId);

        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with ID " + sensorId + " was not found.");
        }

        List<SensorReading> readings = InMemoryStore.SENSOR_READINGS.get(sensorId);

        if (readings == null) {
            readings = new ArrayList<SensorReading>();
            InMemoryStore.SENSOR_READINGS.put(sensorId, readings);
        }

        return readings;
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = InMemoryStore.SENSORS.get(sensorId);

        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with ID " + sensorId + " was not found.");
        }

        if (reading == null) {
            throw new InvalidInputException("Reading payload must not be null.");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            throw new InvalidInputException("Reading ID is required.");
        }

        if (reading.getTimestamp() <= 0) {
            throw new InvalidInputException("Reading timestamp must be greater than zero.");
        }

        List<SensorReading> readings = InMemoryStore.SENSOR_READINGS.get(sensorId);

        if (readings == null) {
            readings = new ArrayList<SensorReading>();
            InMemoryStore.SENSOR_READINGS.put(sensorId, readings);
        }

        for (SensorReading existing : readings) {
            if (existing.getId().equals(reading.getId())) {
                throw new DuplicateResourceException("Reading with ID " + reading.getId() + " already exists for sensor " + sensorId + ".");
            }
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is currently in MAINTENANCE mode and cannot accept new readings."
            );
        }

        readings.add(reading);
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}