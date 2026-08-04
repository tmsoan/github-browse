package com.anos.data.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

/**
 * Scans the `repository` package only: a broader `com.anos.data` scan also prefix-matches
 * `com.anos.database`, which would pull database definitions in a second time.
 */
@Module
@Configuration
@ComponentScan("com.anos.data.repository")
class DataModule
