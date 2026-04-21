package com.batuhan.smartcampus.resource;

import com.batuhan.smartcampus.exception.DuplicateResourceException;
import com.batuhan.smartcampus.exception.InvalidInputException;
import com.batuhan.smartcampus.exception.LinkedResourceNotFoundException;
import com.batuhan.smartcampus.model.Room;
import com.batuhan.smartcampus.model.Sensor;
import com.batuhan.smartcampus.store.InMemoryStore;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<Sensor>(InMemoryStore.SENSORS.values());

        if (type != null && !type.trim().isEmpty()) {
            sensors.removeIf(sensor -> !sensor.getType().equalsIgnoreCase(type));
        }

        return sensors;
    }

    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null) {
            throw new InvalidInputException("Sensor payload must not be null.");
        }

        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            throw new InvalidInputException("Sensor ID is required.");
        }

        if (sensor.getType() == null || sensor.getType().trim().isEmpty()) {
            throw new InvalidInputException("Sensor type is required.");
        }

        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            throw new InvalidInputException("Sensor status is required.");
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            throw new InvalidInputException("Sensor roomId is required.");
        }

        if (InMemoryStore.SENSORS.containsKey(sensor.getId())) {
            throw new DuplicateResourceException("Sensor with ID " + sensor.getId() + " already exists.");
        }

        Room room = InMemoryStore.ROOMS.get(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "Room with ID " + sensor.getRoomId() + " does not exist."
            );
        }

        InMemoryStore.SENSORS.put(sensor.getId(), sensor);

        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }

        if (!InMemoryStore.SENSOR_READINGS.containsKey(sensor.getId())) {
            InMemoryStore.SENSOR_READINGS.put(sensor.getId(), new ArrayList<>());
        }

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}