package com.quick.tor

import com.quick.tor.config.*
import com.quick.tor.log.Logger
import com.quick.tor.log.LoggerImpl
import mu.KotlinLogging
import org.koin.dsl.module

val sharedModule = module(createdAtStart = true) {
    single<Logger> { LoggerImpl(KotlinLogging) }
    single<EnvironmentVariables> { EnvironmentVariablesImpl() }
    single<ConfigRepository> { ConfigRepositoryImpl(envVars = get()) }
    single { ApplicationConfig(configRepository = get()) }
}