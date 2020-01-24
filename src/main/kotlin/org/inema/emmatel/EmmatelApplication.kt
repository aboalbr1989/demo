package org.inema.emmatel

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.camunda.connect.Connectors
import org.camunda.connect.httpclient.HttpConnector
import org.camunda.spin.Spin.JSON
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@SpringBootApplication
@EnableProcessApplication("org.emmatel.purchase")
class EmmatelApplication

fun main(args: Array<String>) {
    runApplication<EmmatelApplication>(*args)
}

@Entity
data class PhoneModel(
        @field:Id
        @GeneratedValue
        val id: UUID? = null,
        val brand: String = "",
        val model: String = "",
        val color: String = ""
)

interface PhoneModelRepository : JpaRepository<PhoneModel, UUID> {
    @Query("from PhoneModel  p where concat(lower(p.brand),' ',lower(p.model),' ',lower(p.color)) like :q")
    fun searchPhoneModels(q: String, pagination: Pageable): Page<PhoneModel>
}

@RestController
@RequestMapping("api/models")
class ModelsController(val phoneModels: PhoneModelRepository) {
    @GetMapping
    fun findModel(@RequestParam("q") q: String?, pagination: Pageable? = null) =
            q?.let {
                phoneModels.searchPhoneModels(
                        q = q.asQuery(), pagination = pagination ?: Pageable.unpaged())
            } ?: phoneModels.findAll(pagination ?: Pageable.unpaged())

    fun String.asQuery() = "%" + this.toLowerCase().trim().replace(Regex("\\s+"), "%") + "%"
}

@Service
class PhoneModelService {

    fun savePhoneModels() = JavaDelegate {
        JSON(it.getVariable("data")).elements().map { model ->

        }
    }
}

@Service
class PurchaseService(val restTemplate: RestTemplate) {
    fun logItems() = JavaDelegate { e ->
        UUID.randomUUID().toString().let { id ->
            JSON(e["request"]).elements().map { item ->
                item.prop("purchaseId", id)
            }
        }.forEach {
            Connectors.http<HttpConnector>().createRequest().post().url("http://localhost:9200/purchase/_doc").payload(it.toString()).contentType("application/json").execute()
        }
    }

}


operator fun DelegateExecution.get(key: String): Any? = getVariable(key)


@Configuration
class Bootstrap {

    @Bean
    fun startup(phoneModels: PhoneModelRepository) = CommandLineRunner {
        phoneModels.saveAll(mapOf(
                "IPhone" to (
                        listOf("6", "6 plus", "7", "7 plus", "8", "8 plus", "X", "11", "X Max") to
                                listOf("Black", "White", "Gold")
                        ),
                "Samsung" to (
                        (6..10).map {
                            "Galaxy S$it"
                        } to
                                listOf("Black", "White", "Cyan", "Purple")
                        ),
                "Xiaomi" to (
                        (7..9).map {
                            "RedMi $it"
                        } to
                                listOf("Black", "White", "Cyan", "Purple", "Blue")
                        )
        ).flatMap {
            it.value.first.flatMap { model ->
                it.value.second.map { color ->
                    PhoneModel(
                            brand = it.key,
                            model = model,
                            color = color
                    )
                }
            }
        })
    }


    @Bean
    fun restTemplate() = RestTemplate()
}