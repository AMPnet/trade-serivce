package com.ampnet.tradeservice.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Suppress("MagicNumber")
@Configuration
@ConfigurationProperties(prefix = "interactive-brokers")
class InteractiveBrokersProperties {
    var host: String = "127.0.0.1"
    var port: Int = 7497
}
