package com.syntax_institut.whatssyntax.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Database DI module — wired in Phase 2.
 *
 * Will provide:
 *   - AppDatabase (Room, singleton)
 *   - ChatDao
 *   - MessageDao
 *   - ContactDao
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
