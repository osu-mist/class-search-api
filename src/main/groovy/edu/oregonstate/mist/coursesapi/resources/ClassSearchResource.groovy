package edu.oregonstate.mist.coursesapi.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.coursesapi.ClassSearchUriBuilder
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAO
import groovy.transform.TypeChecked
import io.dropwizard.auth.Auth
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.security.PermitAll
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType

@Path('/catalog/')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class ClassSearchResource extends Resource {
    Logger logger = LoggerFactory.getLogger(ClassSearchResource.class)

    public static final int TERM_LENGTH = 6

    private final ClassSearchDAO classSearchDAO
    private ClassSearchUriBuilder uriBuilder

    ClassSearchResource(ClassSearchDAO classSearchDAO, URI endpointUri) {
        this.classSearchDAO = classSearchDAO
        this.uriBuilder = new ClassSearchUriBuilder(endpointUri)
    }



//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Timed
//    public Response classSearch(@QueryParam('term') String term,
//                                @QueryParam('subject') String subject,
//                                @QueryParam('courseNumber') String courseNumber,
//                                @QueryParam('q') String q) {
//        try {
//            // validate parameters
//            if (!term || !term.trim()) {
//                return badRequest("term is a required parameter.").build()
//            }
//
//            if (!term.isNumber() || term.length() != TERM_LENGTH) {
//                return badRequest("term should be a ${TERM_LENGTH} digit code").build()
//            }
//
//            if (!subject || !subject.trim()) {
//                return badRequest("subject is a required parameter").build()
//            }
//
//            def response = classSearchDAO.getData(term, subject, courseNumber, q, pageNumber,
//                    getPageSize())
//            //@todo: these params are calcualted twice :(
//            ResultObject resultObject = new ResultObject(data: response.data)
//            resultObject.links = getPaginationLinks(response.sourcePagination, term, subject,
//                    courseNumber, q)
//
//            ok(resultObject).build()
//        } catch (Exception e) {
//            logger.error("Exception while getting all terms", e)
//            internalServerError("Woot you found a bug for us to fix!").build()
//        }
//    }
//
//    private HashMap getPaginationLinks(def sourcePagination, String term, String subject,
//                                       String courseNumber, String q) {
//        def links = [:]
//        // If no results were found, no need to add links
//        if (!sourcePagination?.totalCount) {
//            return
//        }
//
//        Integer pageNumber = getPageNumber()
//        Integer pageSize = getPageSize()
//        def urlParams = [
//                "term": term,
//                "subject": subject,
//                "courseNumber": courseNumber,
//                "q": q,
//                "pageSize": pageSize,
//                "pageNumber": pageNumber
//        ]
//
//        int lastPage = Math.ceil(sourcePagination.totalCount / pageSize)
//
//        links["self"] = getPaginationUrl(urlParams)
//        urlParams.pageNumber = 1
//        links["first"] = getPaginationUrl(urlParams)
//        urlParams.pageNumber = lastPage
//        links["last"] = getPaginationUrl(urlParams)
//
//        if (pageNumber > DEFAULT_PAGE_NUMBER) {
//            urlParams.pageNumber = pageNumber - 1
//            links["prev"] = getPaginationUrl(urlParams)
//        } else {
//            links["prev"] = null
//        }
//
//        if (sourcePagination?.totalCount > (pageNumber * pageSize)) {
//            urlParams.pageNumber = pageNumber + 1
//            links["next"] = getPaginationUrl(urlParams)
//        } else {
//            links["next"] = null
//        }
//
//        links
//    }
}