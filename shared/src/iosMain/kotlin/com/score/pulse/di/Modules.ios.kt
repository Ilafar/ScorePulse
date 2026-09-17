package com.score.pulse.di

import com.score.pulse.core.data.local.DatabaseFactory
import org.koin.core.module.Module
import com.score.pulse.core.data.local.AppDatabase
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory() }
    }
