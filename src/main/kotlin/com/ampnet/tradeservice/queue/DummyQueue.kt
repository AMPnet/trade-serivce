package com.ampnet.tradeservice.queue

import org.jobrunr.jobs.annotations.Job
import org.jobrunr.spring.annotations.Recurring
import org.springframework.stereotype.Service

@Service
class DummyQueue {

    @Recurring(id = "5s-recurring-job", cron = "*/5 * * * * *")
    @Job(name = "5s recurring job")
    fun executeSampleJob() {
        println("Print every 5 second job")
    }
}
