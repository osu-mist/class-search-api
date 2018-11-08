package edu.oregonstate.mist.coursesapi.dao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.coursesapi.core.Attributes
import edu.oregonstate.mist.coursesapi.core.Faculty
import edu.oregonstate.mist.coursesapi.core.MeetingTime
import edu.oregonstate.mist.coursesapi.core.Term
import groovy.transform.InheritConstructors
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
import java.time.LocalDate

class ClassSearchDAO {
    private HttpClient httpClient
    private final URI baseURI

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    private static Logger logger = LoggerFactory.getLogger(this)

    ClassSearchDAO(HttpClient httpClient, String endpoint) {
        this.httpClient = httpClient
        this.baseURI = UriBuilder.fromUri(endpoint).path("/api").build()
    }

    public List<Term> getTerms(String term = null) {
        BackendResponse termsResponse = getResponse("terms")

        List<BackendTerm> terms = objectMapper.readValue(
                termsResponse.response, new TypeReference<List<BackendTerm>>() {})

        terms.collect { Term.fromBackendTerm(it) }
    }

    private BackendResponse getResponse(String endpoint, String term = null) {
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
            new BackendResponse(
                    response: EntityUtils.toString(response.entity),
                    total: Integer.parseInt(response.getFirstHeader("X-Total-Count").value)
            )
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
}

class BackendResponse {
    String response // plain text response, meant to be deserialized.
    Integer total // total objects in resource, regardless of the amount in the current response
}

@InheritConstructors
class InvalidTermException extends Exception {}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendTerm {
    String code
    String description
    LocalDate startDate
    LocalDate endDate
    String financialAidProcessingYear
    LocalDate housingStartDate
    LocalDate housingEndDate
}