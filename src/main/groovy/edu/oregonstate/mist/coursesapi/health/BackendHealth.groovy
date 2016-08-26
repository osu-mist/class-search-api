package edu.oregonstate.mist.coursesapi.health

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result
import edu.oregonstate.mist.coursesapi.dao.UtilHttp
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils

class BackendHealth extends HealthCheck {
    private UtilHttp utilHttp
    private HttpClient httpClient

    BackendHealth(UtilHttp utilHttp, HttpClient httpClient) {
        this.utilHttp = utilHttp
        this.httpClient = httpClient
    }

    protected Result check() {
        CloseableHttpResponse response
        try {
            response = utilHttp.sendGet([max: 1, term:201700], httpClient)
            HttpEntity entity = response.getEntity()
            def entityString = EntityUtils.toString(entity)
            EntityUtils.consume(entity)

            if (entityString && response.statusLine.statusCode == 200) {
                Result.healthy()
            } else {
                Result.unhealthy("Content of url: (${url}) was empty or null")
            }
        } catch(Exception e) {
            Result.unhealthy(e.message)
        }
    }
}
