package edu.oregonstate.mist.classsearchapi.dao

import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.classsearchapi.core.Attributes
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.JSON

class ClassSearchDAO {
    private final Map<String, String> classSearchConfiguration

    ClassSearchDAO(Map<String, String> classSearchConfiguration) {
        this.classSearchConfiguration = classSearchConfiguration
    }

    /**
     * Performs class search and returns results in jsonapi format
     *
     * @param term
     * @param subject
     * @param courseNumber
     * @param q
     * @return
     */
    public def getData(String term, String subject, String courseNumber, String q) {
        def data
        def query = getQueryMap(term, subject, courseNumber, q)

        //@todo: use built-in http library: http://hc.apache.org/httpcomponents-client-ga/quickstart.html
        def remote = new HTTPBuilder(getBackendHost())
        remote.ignoreSSLIssues()
        remote.auth.basic(getBackendUsername(), getBackendPassword())

        remote.get( path : getBackendPath(),
                contentType : JSON,
                query : query) { resp, reader ->

            println "response status: ${resp.statusLine}"
            println 'Headers: -----------'
            resp.headers.each { h ->
                println " ${h.name} : ${h.value}"
            }
//            println 'Response data: -----'
//            System.out << reader
//            println '\n--------------------'
            data = reader
        }

        getFormattedData(data)
    }

    /**
     * Takes the data from the backend and formats it based on the swagger spec.
     *
     * @param data
     * @return
     */
    private static List<ResourceObject> getFormattedData(def data) {
        List<ResourceObject> result = new ArrayList<ResourceObject>()
        data.each {
            Attributes attributes = new Attributes(
                    campusDescription:          it.campusDescription,
                    courseNumber:               it.subject,
                    crn:                        it.courseReferenceNumber,
                    sectionTitle:               it.sectionTitle,
                    creditHourHigh:             it.creditHourHigh,
                    creditHourLow:              it.creditHourLow,
                    creditHours:                it.creditHours,
                    enrollment :                it.enrollment,
                    maximumEnrollment :         it.subject,
                    openSection:                it.openSection,
                    termStartDate :             it.partOfTermStartDate,
                    termEndDate:                it.partOfTermEndDate,
                    termWeeks:                  it.partOfTermWeeks,
                    scheduleTypeDescription :   it.scheduleTypeDescription,
                    section :                   it.sequenceNumber, //@todo: is this the right mapping?
                    status :                    it.status.sectionOpen? 'Open' : 'Closed', //@todo: logic needs more work
                    subject:                    it.subject,
                    subjectCourse :             it.subjectCourse,
                    subjectDescription:         it.subjectDescription,
                    term:                       it.term,
                    termDescription:            it.termDesc, //@todo: update swagger
                    waitCapacity:               it.waitCapacity,
                    waitCount:                  it.waitCount,
            )

            result << new ResourceObject(id: it.courseReferenceNumber, type: 'course', attributes: attributes)
        }

        result
    }

    /**
     * Parses out the parameters and adds them to a map if they are not empty
     *
     * @param term
     * @param subject
     * @param courseNumber
     * @param q
     * @return
     */
    private LinkedHashMap getQueryMap(String term, String subject, String courseNumber, String q) {
        def query = [:]

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
        query
    }

    private String getBackendHost() {
        classSearchConfiguration.get("backendHost")
    }

    private String getBackendPath() {
        classSearchConfiguration.get("backendPath")
    }

    private String getBackendUsername() {
        classSearchConfiguration.get("backendUsername")
    }

    private String getBackendPassword() {
        classSearchConfiguration.get("backendPassword")
    }
}
