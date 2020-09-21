package com.hmo.rd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class KotlinReactiveMongodbApplication

fun main(args: Array<String>) {
    runApplication<KotlinReactiveMongodbApplication>(*args)
}