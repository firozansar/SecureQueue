package com.example.securequeue.model


interface Runnable {
    fun run()
}

class Cat : Runnable {
    override fun run() { println("cat running") }
}

class Dog : Runnable {
    override fun run() { println("dog running") }
}
