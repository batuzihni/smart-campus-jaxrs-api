package com.batuhan.smartcampus.resource;

import com.batuhan.smartcampus.exception.DuplicateResourceException;
import com.batuhan.smartcampus.exception.InvalidInputException;
import com.batuhan.smartcampus.exception.ResourceNotFoundException;
import com.batuhan.smartcampus.exception.RoomNotEmptyException;
import com.batuhan.smartcampus.model.Room;
import com.batuhan.smartcampus.store.InMemoryStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<Room>(InMemoryStore.ROOMS.values());
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null) {
            throw new InvalidInputException("Room payload must not be null.");
        }

        if (room.getId() == null || room.getId().trim().isEmpty()) {
            throw new InvalidInputException("Room ID is required.");
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            throw new InvalidInputException("Room name is required.");
        }

        if (room.getCapacity() <= 0) {
            throw new InvalidInputException("Room capacity must be greater than zero.");
        }

        if (InMemoryStore.ROOMS.containsKey(room.getId())) {
            throw new DuplicateResourceException("Room with ID " + room.getId() + " already exists.");
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }

        InMemoryStore.ROOMS.put(room.getId(), room);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(room.getId())
                .build();

        return Response.created(location)
                .entity(room)
                .build();
    }

    @GET
    @Path("/{roomId}")
    public Room getRoomById(@PathParam("roomId") String roomId) {
        Room room = InMemoryStore.ROOMS.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room with ID " + roomId + " was not found.");
        }

        return room;
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = InMemoryStore.ROOMS.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room with ID " + roomId + " was not found.");
        }

        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted because it has assigned sensors.");
        }

        InMemoryStore.ROOMS.remove(roomId);

        return Response.noContent().build();
    }
}