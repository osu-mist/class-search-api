package edu.oregonstate.mist.coursesapi.dao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.coursesapi.core.Attributes
import edu.oregonstate.mist.coursesapi.core.Faculty
import edu.oregonstate.mist.coursesapi.core.MeetingTime
import org.apache.http.HttpEntity
import org.apache.http.HttpHeaders
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.UriBuilder

class ClassSearchDAO {
    private static final String STATUS_CLOSED = 'Closed'
    private static final String STATUS_OPEN = 'Open'
    private static final String STATUS_WAITLISTED = 'Waitlisted'

    private HttpClient httpClient
    private final URI baseURI

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    private static Logger logger = LoggerFactory.getLogger(this)

    ClassSearchDAO(HttpClient httpClient, String endpoint) {
        this.httpClient = httpClient
        this.baseURI = UriBuilder.fromUri(endpoint).path("/api").build()
    }

    private String getResponse(String endpoint, String term = null) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURI)
        uriBuilder.path(endpoint)

        if (term) {
            uriBuilder.queryParam("term", term)
        }

        URI requestURI = uriBuilder.build()

        HttpGet request = new HttpGet(requestURI)
        request.setHeader(HttpHeaders.ACCEPT, "application/json")

        logger.info("Making a request to ${requestURI}")

        HttpResponse response = httpClient.execute(request)

        Integer statusCode = response.getStatusLine().getStatusCode()

        if (statusCode == HttpStatus.SC_OK) {
            logger.info("Successful response from backend data source.")
            EntityUtils.toString(response.entity)
        } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
            List<String> errorMessages = getBackendErrorMessages(
                    EntityUtils.toString(response.entity))

            logger.info("400 response from backend data source. Error messages: $errorMessages")

            if (errorMessages.contains("Term not found")) {
                String message = "Term: $term is invalid."
                logger.info(message)

                throw new InvalidTermException(message)
            } else {
                String message = "Uncaught error(s) in bad request."
                logger.error(message)

                throw new Exception(message)
            }
        } else {
            String message = "Unexpected response from backend data source. " +
                    "Status code: $statusCode"
            logger.error(message)

            throw new Exception(message)
        }
    }

    /**
     * Calculates the status of a section based on availability and wait count.
     *
     * @param it
     * @return
     */
    private static String getStatus(it) {
        String status = STATUS_CLOSED
        if (it.status.sectionOpen == STATUS_OPEN) {
            status = STATUS_OPEN
        }
        if (it.waitCount > 0) {
            status = STATUS_WAITLISTED //@todo: verify logic
        }
        status
    }

    /**
     * Parses out the parameters and adds them to a map if they are not empty
     *
     * @param term
     * @param subject
     * @param courseNumber
     * @param q
     * @param pageNumber
     * @param pageSize
     * @return
     */
    private LinkedHashMap getQueryMap(String term, String subject, String courseNumber, String q,
                                      Integer pageNumber, Integer pageSize) {
        def query = [offset:0]

        if (term) {
            query['term'] = term.trim()
        }
        if (subject) {
            query['subject'] = subject.trim()
        }
        if (courseNumber) {
            query['courseNumber'] = courseNumber.trim()
        }
        if (q) {
            query['keyword'] = q.trim()
        }
        if (pageNumber && pageNumber > 1) {
            query['offset'] = pageSize * (pageNumber - 1)
        }
        if (pageSize) {
            query['max'] = pageSize
        }
        query
    }
}
