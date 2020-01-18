package org.inema.emmatel

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableProcessApplication("org.emmatel.purchase")
class EmmatelApplication

fun main(args: Array<String>) {
    runApplication<EmmatelApplication>(*args)
}


data class PhoneModel(
        val brand: String,
        val model: String,
        val color: String
)


@RestController
@RequestMapping("api/models")
class ModelsController {
    val models = listOf(
            PhoneModel(brand = "IPhone", model = "X", color = "Black"),
            PhoneModel(brand = "IPhone", model = "X", color = "White"),
            PhoneModel(brand = "Samsunge", model = "Galaxy S10", color = "Gold"),
            PhoneModel(brand = "Samsunge", model = "Galaxy S10", color = "Blue"),
            PhoneModel(brand = "Samsunge", model = "Galaxy S10", color = "White"),
            PhoneModel(brand = "Xiami", model = "Note 10", color = "Gold"),
            PhoneModel(brand = "Xiami", model = "Note 10", color = "Cyan"),
            PhoneModel(brand = "Xiami", model = "Note 10", color = "Red"),
            PhoneModel(brand = "Xiami", model = "Redmi 7", color = "Cyan"),
            PhoneModel(brand = "Xiami", model = "Redmi 7", color = "Red"),
            PhoneModel(brand = "Xiami", model = "Redmi 7", color = "Blue")
    )

    @GetMapping
    fun findModel(@RequestParam("q") q: String) =
            models.filter {
                (it.brand + " " + it.model + " " + it.color).toLowerCase().contains(
                        q.toLowerCase()
                )
            }
}