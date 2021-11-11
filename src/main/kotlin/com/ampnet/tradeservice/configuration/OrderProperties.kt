package com.ampnet.tradeservice.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Suppress("MagicNumber")
@Configuration
@ConfigurationProperties(prefix = "order")
class OrderProperties {
    var maxPendingDuration: Duration = Duration.ofMinutes(10)
    var maxPreparedDuration: Duration = Duration.ofMinutes(10)
}
