package com.hmo.rd.handler

import com.hmo.rd.entity.Car
import com.hmo.rd.repository.CarRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.util.UriComponentsBuilder.fromUriString
import reactor.core.publisher.Mono

@Service
class CarHandler(@Autowired val repository: CarRepository, val reactiveMongoOperations: ReactiveMongoOperations) {

    fun handleFindAll(request: ServerRequest): Mono<ServerResponse> = ok()
            .body(repository.findAll(), Car::class.java)

    fun handleFindById(request: ServerRequest): Mono<ServerResponse> {
        return repository.findById(request.pathVariable("id"))
                .flatMap { car -> ok().bodyValue(car) }
                .switchIfEmpty(Mono.defer { notFound().build() })
    }

    fun handleCreate(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(Car::class.java)
                .flatMap { car ->
                    repository.save(car)
                }
                .flatMap { carAdded ->
                    created(fromUriString(request.uri().toString()).pathSegment(carAdded.id).build().toUri()).bodyValue(carAdded)
                }
    }

    fun handleUpdate(request: ServerRequest): Mono<ServerResponse> {

        return request.bodyToMono(Car::class.java)
                .flatMap { car ->
                    reactiveMongoOperations.findAndModify(
                            Query.query(Criteria.where("_id").`is`(request.pathVariable("id"))),
                            Update().set("model", car.model), FindAndModifyOptions().returnNew(true), Car::class.java)
                }
                .flatMap { carUpdated -> ok().bodyValue(carUpdated) }
                .switchIfEmpty(Mono.defer { badRequest().build() })

    }

    fun handleDelete(request: ServerRequest): Mono<ServerResponse> {
        return repository.deleteById(request.pathVariable("id")).then(noContent().build())
    }
}