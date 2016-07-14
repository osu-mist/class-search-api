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
        try {
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

            def response = classSearchDAO.getData(term, subject, courseNumber, q, getPageNumber(), getPageSize())
            //@todo: these params are calcualted twice :(
            ResultObject resultObject = new ResultObject(data: response.data)
            setPaginationLinks(response.sourcePagination, term, subject, courseNumber, q, resultObject)

            ResponseBuilder responseBuilder = ok(resultObject)
            responseBuilder.build()
        } catch (Exception e) {
            internalServerError("Woot you found a bug for us to fix!").build()
        }
    }

    private void setPaginationLinks(def sourcePagination, String term, String subject, String courseNumber, String q,
                                    ResultObject resultObject) {
        // If no results were found, no need to add links
        if (!sourcePagination?.totalCount) {
            return
        }

        Integer pageNumber = getPageNumber()
        Integer pageSize = getPageSize()
        def urlParams = [
                "term": term,
                "subject": subject,
                "courseNumber": courseNumber,
                "q": q,
                "pageSize": pageSize,
                "pageNumber": pageNumber
        ]

        int lastPage = Math.ceil(sourcePagination.totalCount / pageSize)

        resultObject.links["self"] = getPaginationUrl(urlParams)
        urlParams.pageNumber = 1
        resultObject.links["first"] = getPaginationUrl(urlParams)
        urlParams.pageNumber = lastPage
        resultObject.links["last"] = getPaginationUrl(urlParams)

        if (pageNumber > DEFAULT_PAGE_NUMBER) {
            urlParams.pageNumber = pageNumber - 1
            resultObject.links["prev"] = getPaginationUrl(urlParams)
        } else {
            resultObject.links["prev"] = null
        }

        if (sourcePagination?.totalCount > (pageNumber * pageSize)) {
            urlParams.pageNumber = pageNumber + 1
            resultObject.links["next"] = getPaginationUrl(urlParams)
        } else {
            resultObject.links["next"] = null
        }
    }
}
