package com.shotmaniacs;

import com.shotmaniacs.dao.CrewDao;
import com.shotmaniacs.dao.EventDao;
import com.shotmaniacs.dao.PhotoDao;
import com.shotmaniacs.rest.security.AuthenticationFilter;
import com.shotmaniacs.rest.exceptions.ServerExceptionMapper;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/rest/*")
public class ServerConfig extends ResourceConfig {
    private static final String DB_DRIVER = "org.postgresql.Driver";

    public ServerConfig() {
        packages("com.shotmaniacs.rest.resources");     // Define a path to servlet resources
        register(AuthenticationFilter.class);           // Register a filter to verify the user by their token
        register(RolesAllowedDynamicFeature.class);     // Allow using @RolesAllowed annotation
        register(ServerExceptionMapper.class);          // Register custom exceptions
        register(MultiPartFeature.class);               // Allow file transfer

        // Register dao services for dependency injection
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(EventDao.class);
                bindAsContract(CrewDao.class);
                bindAsContract(PhotoDao.class);
            }
        });

        try {
            // Register a database driver
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to find database driver");
        }
    }
}