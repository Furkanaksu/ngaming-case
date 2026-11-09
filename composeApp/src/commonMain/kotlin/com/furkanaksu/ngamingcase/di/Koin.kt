package com.furkanaksu.ngamingcase.di

import com.furkanaksu.ngamingcase.data.remote.PostApi
import com.furkanaksu.ngamingcase.data.remote.createHttpClient
import com.furkanaksu.ngamingcase.data.repository.PostRepository
import com.furkanaksu.ngamingcase.data.repository.PostRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
    single { PostApi(get()) }
}

val dataModule = module {
    single<PostRepository> { PostRepositoryImpl(get()) }
}

val appModules = listOf(networkModule, dataModule)

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}
