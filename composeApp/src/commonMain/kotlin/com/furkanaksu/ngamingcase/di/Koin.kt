package com.furkanaksu.ngamingcase.di

import com.furkanaksu.ngamingcase.data.remote.PostApi
import com.furkanaksu.ngamingcase.data.remote.createHttpClient
import com.furkanaksu.ngamingcase.data.repository.PostRepository
import com.furkanaksu.ngamingcase.data.repository.PostRepositoryImpl
import com.furkanaksu.ngamingcase.ui.list.PostListViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
    single { PostApi(get()) }
}

val dataModule = module {
    single<PostRepository> { PostRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { PostListViewModel(get()) }
}

val appModules = listOf(networkModule, dataModule, viewModelModule)

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}
