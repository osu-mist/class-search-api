package edu.oregonstate.mist.coursesapi

import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.InfoResource
import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.BasicAuthenticator
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAO
import edu.oregonstate.mist.coursesapi.dao.UtilHttp
import edu.oregonstate.mist.coursesapi.health.BackendHealth
import edu.oregonstate.mist.coursesapi.resources.ClassSearchResource
import io.dropwizard.Application
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.setup.Environment
import io.dropwizard.auth.AuthFactory
import io.dropwizard.auth.basic.BasicAuthFactory
import org.apache.http.client.HttpClient

/**
 * Main application class.
 */
class ClassSearchApplication extends Application<ClassSearchConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(ClassSearchConfiguration configuration, Environment environment) {
        Resource.loadProperties()
        environment.jersey().register(new InfoResource())

        // the httpclient from DW provides with many metrics and config options
        HttpClient httpClient = new HttpClientBuilder(environment)
                .using(configuration.getHttpClientConfiguration())
                .build("backend-http-client")

        // reusable UtilHttp instance for both DAO and healthcheck
        UtilHttp utilHttp = new UtilHttp(configuration.classSearch)

        // setup dao
        ClassSearchDAO classSearchDAO = new ClassSearchDAO(utilHttp, httpClient)

        def classSearchResource = new ClassSearchResource(classSearchDAO)
        classSearchResource.setEndpointUri(configuration.getApi().getEndpointUri())
        environment.jersey().register(classSearchResource)

        environment.jersey().register(
                AuthFactory.binder(
                        new BasicAuthFactory<AuthenticatedUser>(
                                new BasicAuthenticator(configuration.getCredentialsList()),
                                'SkeletonApplication',
                                AuthenticatedUser.class)))

        // healthchecks
        environment.healthChecks().register("backend",
                new BackendHealth(utilHttp, httpClient))
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {
        new ClassSearchApplication().run(arguments)
    }
}