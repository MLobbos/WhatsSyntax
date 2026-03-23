package com.syntax_institut.whatssyntax.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Network DI module — wired in Phase 1.
 *
 * Will provide:
 *   - OkHttpClient (with JWT auth interceptor)
 *   - Retrofit instance pointed at BASE_URL
 *   - ApiService (Retrofit interface)
 *   - OkHttp WebSocket factory
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule
