package com.shotmaniacs.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.shotmaniacs.config.MockAuthenticationFilter;

import com.shotmaniacs.dao.CrewDao;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.user.*;
import com.shotmaniacs.rest.exceptions.ServerExceptionMapper;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.util.List;

public class SimpleJerseyTest extends JerseyTest {

    protected static final int BAD_REQUEST = Response.Status.BAD_REQUEST.getStatusCode();
    protected static final int FORBIDDEN = Response.Status.FORBIDDEN.getStatusCode();
    protected static final int OK = Response.Status.OK.getStatusCode();
    private static final CrewDao crewDao = new CrewDao();
    private Person person1;
    private Person person2;
    private Person client1;
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    protected ResourceConfig configureBasic() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(
                MockAuthenticationFilter.class,
                RolesAllowedDynamicFeature.class,
                ServerExceptionMapper.class,
                MultiPartFeature.class
        );
    }

    protected Invocation.Builder to(String url) {
        return toWebTarget(url).request();
    }

    protected Invocation.Builder to(String url, ServerRole as) {
        return to(url).header("auth", as);
    }

    protected <T> List<T> get(Class<T> entityClass, Invocation.Builder request) {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, entityClass);
        List<T> entities = request.get(new GenericType<>() {});
        return mapper.convertValue(entities, type);
    }

    protected <T> T getSingle(Class<T> entityClass, Invocation.Builder request) {
        return request.get(entityClass);
    }

    protected <T> Response postFile(T entity, Invocation.Builder request) {
        return request.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA));
    }

    protected <T> Response post(T entity, Invocation.Builder request) {
        return request.post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
    }

    protected <T> Response put(T entity, Invocation.Builder request) {
        return request.put(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
    }

    protected void addMockCrewMember(Department department, ProjectRole... roles) {
        person1 = new Person(123, "testPerson1", "06-23434565", "unique@email.com", null, ServerRole.CREW);
        this.crewDao.save(new CrewMember(person1, List.of(roles), Availability.AVAILABLE, department));
        person2 = new Person(1, "testPerson2", "06-23434565", "uni@eml.com", null, ServerRole.CREW);
        this.crewDao.save(new CrewMember(person2, List.of(roles), Availability.AVAILABLE, department));
    }
    protected void deleteMockCrewMember(){
        this.crewDao.deleteByPerson(person1);
        this.crewDao.deleteByPerson(person2);

    }
    protected void addMockClient() {
        client1 = new Person(MockAuthenticationFilter.USER_ID, "testCLient1", "06-2334565", "unique@ail.com", null, ServerRole.CLIENT);
        this.crewDao.save(client1);
    }
    protected void deleteMockClient(){
        this.crewDao.deleteByPerson(client1);
    }

    private WebTarget toWebTarget(String url) {
        if (url.contains("?")) {
            String[] parts = url.split("\\?");
            String[] params = parts[1].split("=");
            WebTarget target = target(parts[0]);
            for (int i = 0; i < params.length / 2; i++) {
                target = target.queryParam(params[i], params[i + 1]);
            }
            return target;
        }
        return target(url);
    }
}
