package com.example.core.usecase.base

import kotlinx.coroutines.CoroutineDispatcher

data class AppCoroutinesDispachers(
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val main: CoroutineDispatcher
)
