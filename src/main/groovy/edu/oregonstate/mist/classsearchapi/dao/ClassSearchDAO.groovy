package edu.oregonstate.mist.classsearchapi.dao

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.JSON

class ClassSearchDAO {
    private final Map<String, String> classSearchConfiguration
    ObjectMapper mapper = new ObjectMapper();

    ClassSearchDAO(Map<String, String> classSearchConfiguration) {
        this.classSearchConfiguration = classSearchConfiguration
    }

    public def getData(String term, String subject, String courseNumber, String q) {
        def query = [:]
        def data

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

        println query

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
            println 'Response data: -----'
            System.out << reader
            println '\n--------------------'
            data = reader
        }

        data
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
