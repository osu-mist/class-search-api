package edu.oregonstate.mist.classsearchapi.dao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.classsearchapi.core.Attributes
import edu.oregonstate.mist.classsearchapi.core.Faculty
import edu.oregonstate.mist.classsearchapi.core.MeetingTime
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils

class ClassSearchDAO {
    private static final String STATUS_CLOSED = 'Closed'
    private static final String STATUS_OPEN = 'Open'
    private static final String STATUS_WAITLISTED = 'Waitlisted'

    private UtilHttp utilHttp
    private HttpClient httpClient
    private ObjectMapper mapper = new ObjectMapper()

    ClassSearchDAO(Map<String, String> classSearchConfiguration, HttpClient httpClient) {
        this.httpClient = httpClient
        this.utilHttp = new UtilHttp(classSearchConfiguration)
    }

    /**
     * Performs class search and returns results in jsonapi format
     *
     * @param term
     * @param subject
     * @param courseNumber
     * @param q
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public def getData(String term, String subject, String courseNumber, String q,
                       Integer pageNumber, Integer pageSize) {
        CloseableHttpResponse response
        def data = []
        def sourcePagination = [totalCount: 0, pageOffset: 0, pageMaxSize: 0]

        try {
            def query = getQueryMap(term, subject, courseNumber, q, pageNumber, pageSize)
            response = utilHttp.sendGet(query, httpClient)

            HttpEntity entity = response.getEntity()
            def entityString = EntityUtils.toString(entity)

            data = this.mapper.readValue(entityString,
                    new TypeReference<List<HashMap>>() {
                });

            sourcePagination = getSourcePagination(response.getAllHeaders())
            EntityUtils.consume(entity)
        } finally {
            response?.close()
        }

        [data: getFormattedData(data), sourcePagination: sourcePagination]
    }

    private static getSourcePagination(headers) {
        def headerMap = [:]
        headers.each {
            headerMap[it.name] = it.value
        }

        [
            totalCount: headerMap['X-hedtech-totalCount']?.toInteger(),
            pageOffset: headerMap['X-hedtech-pageOffset']?.toInteger(),
            pageMaxSize: headerMap['X-hedtech-pageMaxSize']?.toInteger()
        ]
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
            List<Faculty> faculty = new ArrayList<Faculty>()
            List<MeetingTime> meetingTimes = new ArrayList<MeetingTime>()
            String status = getStatus(it)

            it.faculty.each { f ->
                faculty << new Faculty(displayName: f.displayName, primaryFaculty: f.primaryIndicator)
            }
            it.meetingTimes.each { k ->
                meetingTimes << new MeetingTime(
                        startTime:          k.beginTime,
                        endTime:            k.endTime,
                        building:           k.building,
                        buildingName:       k.buildingDescription,
                        room:               k.room,
                        campus:             k.campus,
                        campusDescription:  k.campusDescription,
                        monday:             k.monday,
                        tuesday:            k.tuesday,
                        wednesday:          k.wednesday,
                        thursday:           k.thursday,
                        friday:             k.friday,
                        saturday:           k.saturday,
                        sunday:             k.sunday
                )
            }

            Attributes attributes = new Attributes(
                    campusDescription:          it.campusDescription,
                    courseNumber:               it.courseNumber,
                    crn:                        it.courseReferenceNumber,
                    sectionTitle:               it.sectionTitle,
                    creditHourHigh:             it.creditHourHigh,
                    creditHourLow:              it.creditHourLow,
                    creditHours:                it.creditHours,
                    enrollment :                it.enrollment,
                    maximumEnrollment:          it.maximumEnrollment,
                    openSection:                it.openSection,
                    termStartDate :             it.partOfTermStartDate,
                    termEndDate:                it.partOfTermEndDate,
                    termWeeks:                  it.partOfTermWeeks,
                    scheduleTypeDescription :   it.scheduleTypeDescription,
                    section :                   it.sequenceNumber,
                    status :                    status,
                    subject:                    it.subject,
                    subjectCourse :             it.subjectCourse,
                    subjectDescription:         it.subjectDescription,
                    term:                       it.term,
                    termDescription:            it.termDesc,
                    waitCapacity:               it.waitCapacity,
                    waitCount:                  it.waitCount,
                    faculty:                    faculty,
                    meetingTimes:               meetingTimes
            )

            result << new ResourceObject(id: it.courseReferenceNumber, type: 'course',
                    attributes: attributes)
        }

        result
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
            query['offset'] = pageSize * pageNumber
        }
        if (pageSize) {
            query['max'] = pageSize
        }
        query
    }


}
