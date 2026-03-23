package com.syntax_institut.whatssyntax.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Top-level Hilt module.
 * Phase 1: add @Provides for ApiService (Retrofit), AuthRepository.
 * Phase 2: add @Provides for AppDatabase (Room), ChatRepository.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
