package api;

// Injection
// import javax.inject.Inject; // Need to understand what injection is
import javax.ws.rs.*;

import javax.ws.rs.core.Response;
// MediaType
import javax.ws.rs.core.MediaType;
import javax.enterprise.context.ApplicationScoped; // ApplicationScoped ~singleton

//  import classes of domain
import domain.model.Group;

// service
import domain.service.GroupService;

// will be removed later
import domain.service.GroupServiceImpl;


// https://www.restapitutorial.com/lessons/httpmethods.html

@ApplicationScoped // singleton
@Path("/groups")
public class GroupRestService {
    // Endpoint

    /*
    @Inject
    private GroupService groupService;
    */

    private final String current_link="http://localhost:10080/groups/"; // for link informations
    private final GroupService groupService;

    public GroupRestService() {
        this.groupService=new GroupServiceImpl();
    }


    // http://localhost:10080/groups
    // GET a list of all groups
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGroups() {
        return Response.ok(groupService.getAllGroups()).build();
    }

    // http://localhost:10080/groups/{id}
    // GET a particular group
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroup(@PathParam("id") int id) { // TODO: check input

        Group group=groupService.getGroup(id);
        if (group == null) { // group not found
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // group exists
        return Response.ok(group).build();

    }
    /*
    https://stackoverflow.com/questions/4687271/jax-rs-how-to-return-json-and-http-status-code-together
    https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Response.Status.html
    https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Response.html

    Just to try something : Response.ok(groupService.getGroup(id)).header("hello",42).build();
    */

    // TODO: restriction on name ? or not
    // Create, add a group to the existing groups
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(Group group){ // TODO: check input, for instance if we also enter id, not only name or only id
        /*
        Create a group and returns HTTP status code and the location of the newly created object

        Example with curl:
        - curl --verbose -H "Content-Type: application/json" -X POST http://localhost:10080/groups -d '{"name":"test"}'

         Then you use GET to see the created object
        */

        Group returnedGroup=groupService.createGroup(group); // get all groups and check if group inside groups
        // will add the Group if does not exist, otherwise return null
        if (returnedGroup == null){
            // group exists already
            return Response.status(Response.Status.CONFLICT).build(); // 409
        }

        return Response.status(Response.Status.CREATED).header("Location", current_link.concat(String.valueOf(returnedGroup.getId()))).build(); // 201
    }

    // Update existing group
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGroup(Group group){ // TODO: Need to check the JSON, and add 204 (No Content), seems still have 404 if we use only the name without id.
        /*
        Update existing group. Need to know the id to update. Return modified object.

        Example:
        - curl --verbose -H "Content-Type: application/json" -X PUT http://localhost:10080/groups -d '{"id":3,"name":"fabrice"}'

         */

        Group returnedGroup=groupService.updateGroup(group); // get all groups and check if group inside list of groups
        // will update the Group if exists, otherwise return null
        if (returnedGroup == null){
            // group does not exist already
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }

        return  Response.ok(returnedGroup).header("Location", current_link.concat(String.valueOf(group.getId()))).build(); // 200
    }

    // Delete existing group
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGroup(int id){ // TODO: check input
        /*
        Delete existing group and return the deleted group.

        Example:
        - curl --verbose -H "Content-Type: application/json" -X DELETE http://localhost:10080/groups -d 4

        This does not work : '{"id":youridhere}'
         */

        Group returnedGroup=groupService.deleteGroup(id); // get all groups and check if group inside groups
        // will delete the group if exists, otherwise return null
        if (returnedGroup == null){
            // group does not exist already
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }

        return  Response.ok(returnedGroup).build(); // 200
    }
}
