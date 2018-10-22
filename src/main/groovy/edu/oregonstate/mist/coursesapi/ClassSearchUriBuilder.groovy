package edu.oregonstate.mist.coursesapi

import javax.ws.rs.core.UriBuilder

class ClassSearchUriBuilder {
    URI endpointUri

    ClassSearchUriBuilder(URI endpointUri) {
        this.endpointUri = endpointUri
    }

    URI genericUri(String endpoint, String term = null) {
        UriBuilder builder = UriBuilder.fromUri(this.endpointUri).path("students/$endpoint")

        if (term) {
            builder.queryParam("term", term)
        }

        builder.build()
    }
}
