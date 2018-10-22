package edu.oregonstate.mist.coursesapi

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAO
import edu.oregonstate.mist.coursesapi.dao.UtilHttp
import edu.oregonstate.mist.coursesapi.health.BackendHealth
import edu.oregonstate.mist.coursesapi.resources.ClassSearchResource
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.setup.Environment
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.BasicCredentialsProvider

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
        this.setup(configuration, environment)


        ClassSearchDAO classSearchDAO = new ClassSearchDAO(
                getHttpClient(configuration, environment),
                configuration.httpDataSource.endpoint
        )

        environment.jersey().register(new ClassSearchResource(
                classSearchDAO, configuration.api.endpointUri))

    }

    HttpClient getHttpClient(ClassSearchConfiguration configuration, Environment environment) {
        def httpClientBuilder = new HttpClientBuilder(environment)

        if (configuration.httpClient != null) {
            httpClientBuilder.using(configuration.httpClient)
        }

        CredentialsProvider provider = new BasicCredentialsProvider()
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                configuration.httpDataSource.username,
                configuration.httpDataSource.password
        )
        provider.setCredentials(AuthScope.ANY, credentials)

        httpClientBuilder.using(provider)

        httpClientBuilder.build("backend-http-client")
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