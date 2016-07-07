package edu.oregonstate.mist.classsearchapi

import com.fasterxml.jackson.annotation.JsonProperty
import edu.oregonstate.mist.api.Configuration

import javax.validation.Valid
import javax.validation.constraints.NotNull

class ClassSearchConfiguration extends Configuration {
    @JsonProperty('class-search')
    @NotNull
    @Valid
    Map<String, String> classSearch
}
