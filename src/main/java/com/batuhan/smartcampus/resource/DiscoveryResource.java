package com.batuhan.smartcampus.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getApiInfo() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("name", "Smart Campus Sensor & Room Management API");
        response.put("version", "v1");

        Map<String, String> maintainer = new LinkedHashMap<>();
        maintainer.put("name", "Batuhan Amirzehni");
        maintainer.put("email", "w2106852@westminster.ac.uk");
        response.put("maintainer", maintainer);

        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        response.put("resources", resources);

        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", "/api/v1");
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("links", links);

        return response;
    }
}