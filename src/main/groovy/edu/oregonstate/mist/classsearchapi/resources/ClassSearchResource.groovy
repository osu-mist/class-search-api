package edu.oregonstate.mist.classsearchapi.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.classsearchapi.dao.ClassSearchDAO
import io.dropwizard.auth.Auth

import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ResponseBuilder
import javax.ws.rs.core.MediaType

import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.HTTPBuilder

/**
 * Sample resource class.
 */
@Path('/class-search/')
class ClassSearchResource extends Resource {

    public static final int TERM_LENGTH = 6
    private final ClassSearchDAO classSearchDAO

    ClassSearchResource(ClassSearchDAO classSearchDAO) {
        this.classSearchDAO = classSearchDAO
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response classSearch(@Auth AuthenticatedUser _, @NotNull @QueryParam('term') String term,
                               @QueryParam('subject') String subject,
                               @QueryParam('courseNumber') String courseNumber, @QueryParam('q') String q) {
        // validate parameters
        if (!term || !term.trim()) {
            return badRequest("term is a required parameter.").build()
        }

        if (!term.isNumber() || term.length() != TERM_LENGTH) {
            return badRequest("term should be a ${TERM_LENGTH} digit code").build()
        }

        if (!subject || !subject.trim()) {
            return badRequest("subject is a required parameter").build()
        }

        //@todo: pagination
        def data = classSearchDAO.getData(term, subject, courseNumber, q)
        ResultObject resultObject = new ResultObject(data: data)

        ResponseBuilder responseBuilder = ok(resultObject)
        responseBuilder.build()
    }


}
