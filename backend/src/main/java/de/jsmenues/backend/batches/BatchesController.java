package de.jsmenues.backend.batches;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;



@Path("batches")
public class BatchesController {
	Gson gson = new Gson();
	 
    /**
     * Method handling HTTP GET requests. The returned object will be sent to the
     * client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @PermitAll
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {

        return "[{ id: 'a220', duration: 6 },"
        		+ "{ id: 'a610', duration: 28 },"
        		+ "{ id: 'a620', duration: 496 },"
        		+ "{ id: 'a999', duration: 8128 },];";
    }
        
    @PermitAll
    @GET
    @Path("/column")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getList() {
    	List<String> batches = new ArrayList<String>();
    	//batches.add("name");
    	batches.add("{name: 'a220', duration: 6 }");
    	//String re = gson.toJson(batches);
    	//return batches;
    	return Response.ok(batches).build(); 
    }
    
    @PermitAll
    @GET
    @Path("/data")
    public List<String> getAlldata(){
    	List<String> result = new ArrayList<String>();
    	String j = "{ name: 'a220', duration: 6 }";
    	result.add(j);
    	return result;
    }
    
    @PermitAll
    @PUT
    @Path("/data")
    public Response putData(String value){
    	//ToDO
    	
    	return Response.ok("OK").build();
    }
    
    @PermitAll
    @PATCH
    @Path("/data/{name}")
    public Response patchData(@QueryParam("name") String name, String value){
    	//ToDO
    	
    	return Response.ok("OK").build();
    }
       
    /*
     * fk_doku: string;
    	  periodizitaet: string;
    	  ersterlauf: string;
    	  letzterlauf: string
    REST- CRUD
    - get
    - post
    - put
    - delete
    - (patch)
    
    */
}
