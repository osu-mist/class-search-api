package edu.oregonstate.mist.coursesapi.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.MetaObject
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject

import edu.oregonstate.mist.coursesapi.core.Term
import edu.oregonstate.mist.coursesapi.core.Terms
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAO
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAONotFoundException
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriBuilder

@Path('classes')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class ClassSearchResource extends Resource {
    Logger logger = LoggerFactory.getLogger(ClassSearchResource.class)

    private final ClassSearchDAO classSearchDAO

    private String baseEndpoint = "classes"
    private String termsEndpoint = "$baseEndpoint/terms"

    ClassSearchResource(ClassSearchDAO classSearchDAO, URI endpointUri) {
        this.classSearchDAO = classSearchDAO
        this.endpointUri = endpointUri
    }

    @GET
    @Timed
    @Path('terms')
    Response getTerms() {
        Terms termsResponse = classSearchDAO.getTerms(getPageSize(), getPageNumber())
        ok(new ResultObject(
                links: getPaginationLinks([:], termsResponse.metaObject, termsEndpoint),
                data: termsResponse.terms.collect { getTermResourceObject(it) },
                meta: termsResponse.metaObject
        )).build()
    }

    @GET
    @Timed
    @Path('terms/{termCode: [0-9a-zA-Z-]+}')
    Response getTermByTermCode(@PathParam('termCode') String termCode) {
        Term term

        try {
            term = classSearchDAO.getTermByTermCode(termCode)
        } catch (ClassSearchDAONotFoundException e) {
            return notFound("Term code not found.").build()
        }

        ok(new ResultObject(
                data: getTermResourceObject(term)
        )).build()
    }

    private ResourceObject getTermResourceObject(Term term) {
        String id = term.code
        new ResourceObject(
                id: id,
                type: "terms",
                attributes: term,
                links: getSelfLink("$termsEndpoint/$id")
        )
    }

    @GET
    @Timed
    @Path('subjects')
    Response getSubjects() {

    }

    private Map<String,String> getPaginationLinks(Map<String, String> params,
                                                  MetaObject metaObject,
                                                  String endpoint) {
        metaObject.with {
            [
                    self: getPaginationUrl(
                            getParamsMap(params, currentPageSize, currentPageNumber), endpoint),
                    first: getPaginationUrl(getParamsMap(params, currentPageSize, 1), endpoint),
                    last: getPaginationUrl(
                            getParamsMap(params, currentPageSize, totalPages), endpoint),
                    prev: currentPageNumber <= 1 ? null : getPaginationUrl(
                            getParamsMap(params, currentPageSize, currentPageNumber - 1), endpoint),
                    next: currentPageNumber >= totalPages ? null : getPaginationUrl(
                            getParamsMap(params, currentPageSize, currentPageNumber + 1), endpoint)
            ]
        }
    }

    private static Map<String,String> getParamsMap(Map<String, String> params,
                                            Integer pageSize,
                                            Integer pageNumber) {
        params['pageSize'] = pageSize.toString()
        params['pageNumber'] = pageNumber.toString()

        params
    }

    private Map<String,String> getSelfLink(String endpoint) {
        UriBuilder builder = UriBuilder.fromUri(this.endpointUri).path(endpoint)

        [self: builder.build().toString()]
    }
}