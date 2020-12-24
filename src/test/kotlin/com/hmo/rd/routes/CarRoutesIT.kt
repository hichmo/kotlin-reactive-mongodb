package com.hmo.rd.routes

import com.hmo.rd.entity.Car
import com.hmo.rd.repository.CarRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class CarRoutesIT(@Autowired val webTestClient: WebTestClient,
                  @Autowired val repository: CarRepository) {

    companion object {

        @Container
        @JvmField
        val mongoContainer: MongoDBContainer = MongoDBContainer("mongo:latest")

        init {
            mongoContainer.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
        }

    }

    @BeforeEach
    fun setup() {
        repository.deleteAll().subscribe()
    }

    @Test
    fun `When findAll then return Cars and Status 200`() {

        repository.saveAll(mutableListOf(Car("", "Renault Scenic"),
                Car("", "Renault Captur"),
                Car("", "Renault Kadjar"))).subscribe()

        webTestClient.get().uri("/cars")
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Car::class.java)
                .hasSize(3)
    }

    @Test
    fun `When findByWrongId then return status 404 `() {

        webTestClient.get().uri("/cars/ssss")
                .exchange()
                .expectStatus().isNotFound
    }

    @Test
    fun `When findById then return Car with status 200`() {

        val id = saveCar().block()!!.id
        webTestClient.get().uri("/cars/$id")
                .exchange()
                .expectStatus().isOk
                .expectBody().jsonPath("$.model").isEqualTo("Renault Clio")
    }

    @Test
    fun `When saveCar then return Car with status 201`() {

        webTestClient.post().uri("/cars")
                .body(Mono.just(Car(null, "Renault Espace")), Car::class.java)
                .exchange()
                .expectHeader().exists("Location")
                .expectStatus().isCreated
                .expectBody().jsonPath("$.id").isNotEmpty
                .jsonPath("$.model").isEqualTo("Renault Espace")
    }

    @Test
    fun `When update car then return Car correctly updated with status 200`() {

        val car = saveCar().block()
        car!!.model = "Renault Clio Restyled"

        webTestClient.put().uri("/cars/" + car.id)
                .body(Mono.just(car), Car::class.java)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo(car.id!!)
                .jsonPath("$.model").isEqualTo("Renault Clio Restyled")
    }

    @Test
    fun `When delete car then return status 204`() {

        val id = saveCar().block()!!.id

        webTestClient.delete().uri("/cars/$id")
                .exchange()
                .expectStatus().isNoContent
    }

    fun saveCar(): Mono<Car> {
        return repository.save(Car(null, "Renault Clio"))
    }
}