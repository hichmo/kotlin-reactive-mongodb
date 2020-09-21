package com.hmo.rd.handler

import com.hmo.rd.entity.Car
import com.hmo.rd.repository.CarRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Service
class CarHandler(@Autowired val repository: CarRepository) {

    fun handleFindAll(request: ServerRequest): Mono<ServerResponse> = ok()
            .body(repository.findAll(), Car::class.java)

    fun handleFindById(request: ServerRequest): Mono<ServerResponse> {
        return repository.findById(request.pathVariable("id"))
                .flatMap { item -> ok().bodyValue(item) }
                .switchIfEmpty(Mono.defer { notFound().build() })
    }

    fun handleCreate(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(Car::class.java)
                .flatMap { item ->
                    repository.save(item) }
                .flatMap { itemSaved ->
                    print(itemSaved.toString())
                    created(UriComponentsBuilder.fromUriString(request.path())
                            .pathSegment(itemSaved.id).build().toUri())
                            .bodyValue(itemSaved)
                }
    }

    fun handleUpdate(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(Car::class.java)
                .flatMap { carToUpdate ->
                    repository.findById(request.pathVariable("id"))
                            .flatMap { existingCar ->
                                existingCar.model = carToUpdate.model
                                ok().body(repository.save(existingCar), Car::class.java)
                            }
                            .switchIfEmpty(Mono.defer { badRequest().build() })
                }
    }

    fun handleDelete(request: ServerRequest): Mono<ServerResponse> {
        return repository.deleteById(request.pathVariable("id")).then(noContent().build())
    }
}