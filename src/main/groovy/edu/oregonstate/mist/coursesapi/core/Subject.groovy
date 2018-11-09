package edu.oregonstate.mist.coursesapi.core

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.oregonstate.mist.api.jsonapi.MetaObject
import edu.oregonstate.mist.coursesapi.dao.BackendSubject

class Subject {
    @JsonIgnore
    String id

    String abbreviation
    String title

    static Subject fromBackendSubject(BackendSubject backendSubject) {
        backendSubject.with {
            new Subject(id: id, abbreviation: abbreviation, title: title)
        }
    }
}

class Subjects {
    List<Subject> subjects
    MetaObject metaObject
}