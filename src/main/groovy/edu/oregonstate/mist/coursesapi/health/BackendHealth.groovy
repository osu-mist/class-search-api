package edu.oregonstate.mist.coursesapi.health

import com.codahale.metrics.health.HealthCheck
import edu.oregonstate.mist.coursesapi.dao.ClassSearchDAO

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
