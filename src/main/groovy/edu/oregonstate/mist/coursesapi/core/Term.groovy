package edu.oregonstate.mist.coursesapi.core

import com.fasterxml.jackson.annotation.JsonFormat
import edu.oregonstate.mist.api.jsonapi.MetaObject
import edu.oregonstate.mist.coursesapi.dao.BackendTerm

import java.time.LocalDate

class Term {
    String code
    String description
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate startDate
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate endDate
    String financialAidYear
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate housingStartDate
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    LocalDate housingEndDate

    static Term fromBackendTerm(BackendTerm backendTerm) {
        backendTerm.with {
            new Term(
                    code: code,
                    description: description,
                    startDate: startDate,
                    endDate: endDate,
                    financialAidYear: financialAidProcessingYear,
                    housingStartDate: housingStartDate,
                    housingEndDate: housingEndDate
            )
        }
    }
}

class Terms {
    List<Term> terms
    MetaObject metaObject
}
