package edu.oregonstate.mist.coursesapi

import edu.oregonstate.mist.api.Configuration
import io.dropwizard.client.HttpClientConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull

class ClassSearchConfiguration extends Configuration {
    @Valid
    @NotNull
    Map<String, String> httpDataSource

    @Valid
    @NotNull
    HttpClientConfiguration httpClient
}
