package edu.oregonstate.mist.coursesapi.health

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAO
import edu.oregonstate.mist.coursesapi.dao.UtilHttp
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils

class BackendHealth extends HealthCheck {
    private ClassSearchDAO classSearchDAO

    BackendHealth(ClassSearchDAO classSearchDAO) {
        this.classSearchDAO = classSearchDAO
    }

    protected Result check() {
        try {
            String status = classSearchDAO.status()
            status == "available" ? Result.healthy() : Result.unhealthy("DAO status: $status")
        } catch(Exception e) {
            Result.unhealthy(e.message)
        }
    }
}
