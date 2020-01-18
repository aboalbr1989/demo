package org.inema.emmatel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmmatelApplication

fun main(args: Array<String>) {
    runApplication<EmmatelApplication>(*args)
}
