package com.duoc.corpaliv.api

// punto de entrada del microservicio rest de solicitudes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CorpalivApiApplication

fun main(args: Array<String>) {
	runApplication<CorpalivApiApplication>(*args)
}
