package edu.oregonstate.mist.coursesapi

import edu.oregonstate.mist.api.ErrorResultObject
import edu.oregonstate.mist.api.MockUriInfo
import edu.oregonstate.mist.api.jsonapi.MetaObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.coursesapi.core.Subject
import edu.oregonstate.mist.coursesapi.core.Subjects
import edu.oregonstate.mist.coursesapi.core.Term
import edu.oregonstate.mist.coursesapi.core.Terms
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAO
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAONotFoundException
import edu.oregonstate.mist.coursesapi.resources.ClassSearchResource
import groovy.mock.interceptor.MockFor
import org.junit.Test

import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.Response
import java.time.LocalDate

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class ClassSearchResourceTest {
    private URI exampleURI = new URI("https://example.com")

    private MetaObject metaObject = new MetaObject(
            totalResults: 1,
            totalPages: 1,
            currentPageNumber: 1,
            currentPageSize: 10
    )

    private Term term = new Term(
            code: "201901",
            description: "Fall 2018",
            startDate: LocalDate.of(2018, 9, 22),
            endDate: LocalDate.of(2018, 12, 7),
            financialAidYear: "1819",
            housingStartDate:  LocalDate.of(2018, 9, 22),
            housingEndDate:  LocalDate.of(2018, 12, 7)
    )

    private Subject subject = new Subject(
            id: "1234",
            abbreviation: "CS",
            title: "Computer Science"
    )

    @Test
    void testGetTerms() {
        Terms terms = new Terms(
                terms: [term],
                metaObject: metaObject
        )

        def mockDAO = getMockDAO()

        mockDAO.demand.getTerms() { Integer pageSize, Integer pageNumber ->
            terms
        }

        mockDAO.use {
            ClassSearchResource classSearchResource = getClassSearchResource()
            Response termsResponse = classSearchResource.getTerms()
            responseChecker(termsResponse, term, term.code, "terms")
        }
    }

    @Test
    void testGetTermByTermCode() {
        def mockDAO = getMockDAO()

        mockDAO.demand.getTermByTermCode() { term }

        mockDAO.use {
            ClassSearchResource classSearchResource = getClassSearchResource()
            Response termResponse = classSearchResource.getTermByTermCode("201901")
            responseChecker(termResponse, term, term.code, "terms")
        }
    }

    @Test
    void getTermByTermCodeShouldReturnNotFound() {
        def mockDAO = getMockDAO()

        mockDAO.demand.getTermByTermCode() { throw new ClassSearchDAONotFoundException() }

        mockDAO.use {
            ClassSearchResource classSearchResource = getClassSearchResource()
            Response termResponse = classSearchResource.getTermByTermCode("foo")
            checkErrorResponse(termResponse, 404, "Term code not found.")
        }
    }

    @Test
    void testGetSubjects() {
        Subjects subjects = new Subjects(
                subjects: [subject],
                metaObject: metaObject
        )

        def mockDAO = getMockDAO()

        mockDAO.demand.getSubjects() { Integer pageSize, Integer pageNumber ->
            subjects
        }

        mockDAO.use {
            ClassSearchResource classSearchResource = getClassSearchResource()
            Response subjectsResponse = classSearchResource.getSubjects()
            responseChecker(subjectsResponse, subject, subject.id, "subjects")
        }
    }

    @Test
    void testGetSubjectById() {
        def mockDAO = getMockDAO()

        mockDAO.demand.getSubjectById() { subject }

        mockDAO.use {
            ClassSearchResource classSearchResource = getClassSearchResource()
            Response subjectResponse = classSearchResource.getSubjectById("1234")
            responseChecker(subjectResponse, subject, subject.id, "subjects")
        }
    }

    @Test
    void getSubjectByIdShouldReturnNotFound() {
        def mockDAO = getMockDAO()

        mockDAO.demand.getSubjectById() { throw new ClassSearchDAONotFoundException() }

        mockDAO.use {
            ClassSearchResource classSearchResource = getClassSearchResource()
            Response subjectResponse = classSearchResource.getSubjectById("foo")
            checkErrorResponse(subjectResponse, 404, "Subject ID not found.")
        }
    }

    private ClassSearchResource getClassSearchResource() {
        ClassSearchDAO classSearchDAO = new ClassSearchDAO(null, "https://backendapi.com")

        ClassSearchResource resource = new ClassSearchResource(classSearchDAO, exampleURI)

        resource.uriInfo = new MockUriInfo(exampleURI, new MultivaluedHashMap())

        resource
    }

    /**
     * Helper method to test a response object
     * @param response
     * @param expectedData
     */
    private void responseChecker(Response response,
                                 def expectedData,
                                 String expectedID,
                                 String expectedType) {
        assertNotNull(response)
        assertEquals(response.status, 200)
        assertEquals(response.getEntity().class, ResultObject.class)

        def responseData = response.getEntity()["data"]

        def resourceObject

        if (responseData instanceof List) {
            resourceObject = responseData[0]
        } else {
            resourceObject = responseData
        }

        assertEquals(resourceObject["attributes"], expectedData)
        assertEquals(resourceObject["id"], expectedID)
        assertEquals(resourceObject["type"], expectedType)
    }

    /**
     * Helper method to check an error response.
     * @param response
     * @param expectedResponseCode
     * @param expectedDeveloperMessage
     */
    private void checkErrorResponse(Response response,
                                    Integer expectedResponseCode,
                                    String expectedErrorMessage) {
        assertNotNull(response)
        assertEquals(response.status, expectedResponseCode)
        assertEquals(response.getEntity().class, ErrorResultObject.class)

        if (expectedErrorMessage) {
            List<String> errorMessages = response.getEntity()["errors"].collect { it["detail"] }
            assertTrue(errorMessages.contains(expectedErrorMessage))
        }
    }

    private MockFor getMockDAO() {
        new MockFor(ClassSearchDAO)
    }
}
