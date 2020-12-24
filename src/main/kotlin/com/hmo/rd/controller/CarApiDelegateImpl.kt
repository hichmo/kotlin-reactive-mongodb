package com.hmo.rd.controller

import com.hmo.rd.model.CarDTO
import com.hmo.rd.service.CarService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class CarApiDelegateImpl(@Autowired val service: CarService) : CarsApiDelegate {

    override fun findCars(exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<CarDTO>>> {
        return Mono.just(ResponseEntity.ok(service.findCars()))
    }


    override fun getCarById(id: String?, exchange: ServerWebExchange?): Mono<ResponseEntity<CarDTO>> {
        return service.getCarById(id!!)
                .map { car -> ResponseEntity.ok(car) }
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))

    }

    override fun addCar(carDTO: Mono<CarDTO>?, exchange: ServerWebExchange?): Mono<ResponseEntity<CarDTO>> {
        return carDTO!!.flatMap { car -> service.addCar(car) }.map { carAdded ->
            ResponseEntity.created(UriComponentsBuilder.fromUriString(exchange!!.request.uri.toString()).pathSegment(carAdded.id).build().toUri())
                    .body(carAdded)
        }
    }

    override fun updateCar(id: String?, carDTO: Mono<CarDTO>?, exchange: ServerWebExchange?): Mono<ResponseEntity<CarDTO>> {
        return carDTO!!.flatMap { car -> service.updateCar(id!!, car) }
                .map { car -> ResponseEntity.ok(car) }
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
    }

    override fun deleteCar(id: String?, exchange: ServerWebExchange?): Mono<ResponseEntity<Void>> {
        return service.deleteById(id!!).then(Mono.just(ResponseEntity.noContent().build()))
    }
}