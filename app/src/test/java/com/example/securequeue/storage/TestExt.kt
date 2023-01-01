package com.comparethemarket.library.usersession

fun Any.getFileAsString(fileName: String): String {
    return requireNotNull(
        javaClass.classLoader
            ?.getResourceAsStream(fileName)
            ?.bufferedReader()
            ?.use { it.readText() }
    )
}
