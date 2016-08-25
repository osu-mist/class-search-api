package edu.oregonstate.mist.coursesapi

import com.fasterxml.jackson.annotation.JsonProperty
import edu.oregonstate.mist.api.Configuration
import io.dropwizard.client.HttpClientConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull

class ClassSearchConfiguration extends Configuration {
    @JsonProperty('courses')
    @NotNull
    @Valid
    Map<String, String> classSearch

    @Valid
    @NotNull
    private HttpClientConfiguration httpClient = new HttpClientConfiguration()

    @JsonProperty("httpClient")
    public HttpClientConfiguration getHttpClientConfiguration() {
        httpClient
    }

    @JsonProperty("httpClient")
    public void setHttpClientConfiguration(HttpClientConfiguration httpClient) {
        this.httpClient = httpClient
    }
}
