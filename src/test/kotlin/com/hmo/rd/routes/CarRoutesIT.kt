package com.hmo.rd.routes

import com.hmo.rd.entity.Car
import com.hmo.rd.repository.CarRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarRoutesIT(@Autowired val webTestClient: WebTestClient,
                  @Autowired val repository: CarRepository) {

    @BeforeEach
    fun cleanUp() {
        repository.deleteAll().subscribe()
    }

    @Test
    @DisplayName("Test find all cars, expected Status OK 200 and body size 3")
    fun should_return_status_ok() {

        repository.saveAll(mutableListOf(Car("", "Renault Scenic"),
                Car("", "Renault Captur"),
                Car("", "Renault Kadjar"))).subscribe()

        webTestClient.get().uri("/cars")
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Car::class.java).hasSize(3)
    }


    @Test
    @DisplayName("Test find Car by unknown id, expected Status Not found 404")
    fun should_return_status_not_found() {
        webTestClient.get().uri("/cars/ssss")
                .exchange()
                .expectStatus().isNotFound
    }

    @Test
    @DisplayName("Test find Car by id, expected body with Car")
    fun should_return_car_when_get_by_id() {

        var id = saveCar().block()!!.id
        webTestClient.get().uri("/cars/$id")
                .exchange()
                .expectStatus().isOk
                .expectBody().jsonPath("$.model").isEqualTo("Renault Clio")
    }

    @Test
    @DisplayName("Test post Car, expected Status Created 201 and body with Car")
    fun should_return_car_when_post() {
        webTestClient.post().uri("/cars")
                .body(Mono.just(Car(null,"Renault Espace")), Car::class.java)
                .exchange()
                .expectHeader().exists("Location")
                .expectStatus().isCreated
                .expectBody().jsonPath("$.id").isNotEmpty
                .jsonPath("$.model").isEqualTo("Renault Espace")
    }

    @Test
    @DisplayName("Test update Car, expected Status 200 and body with Car correctly updated")
    fun should_return_car_when_put() {

        var car = saveCar().block()
        car!!.model = "Renault Clio Restyled"

        webTestClient.put().uri("/cars/"+car.id)
                .body(Mono.just(car), Car::class.java)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo(car.id!!)
                .jsonPath("$.model").isEqualTo("Renault Clio Restyled")
    }

    @Test
    @DisplayName("Test delete Car by id, expected Status No Content 204")
    fun should_return_status_no_content_when_delete() {
        val id = saveCar().block()!!.id
        webTestClient.delete().uri("/cars/$id")
                .exchange()
                .expectStatus().isNoContent

        webTestClient.get().uri("/cars/$id")
                .exchange()
                .expectStatus().isNotFound

    }

    fun saveCar(): Mono<Car> {
        return repository.save(Car(null,"Renault Clio"))
    }
}