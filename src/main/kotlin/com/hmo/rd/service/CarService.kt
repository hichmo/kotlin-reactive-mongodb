package com.hmo.rd.service

import com.hmo.rd.entity.Car
import com.hmo.rd.model.CarDTO
import com.hmo.rd.repository.CarRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CarService(@Autowired val repository: CarRepository, val reactiveMongoOperations: ReactiveMongoOperations) {

    fun findCars(): Flux<CarDTO> = repository.findAll().map { item -> CarDTO().id(item.id).model(item.model) }

    fun getCarById(id: String): Mono<CarDTO> = repository.findById(id).map { item -> CarDTO().id(item.id).model(item.model) }

    fun addCar(car: CarDTO): Mono<CarDTO> = repository.save(Car(car.id, car.model)).map { item -> CarDTO().id(item.id).model(item.model) }

    fun updateCar(id: String, carDTO: CarDTO): Mono<CarDTO> =
            reactiveMongoOperations.findAndModify(Query.query(Criteria.where("_id").`is`(id)), Update().set("model", carDTO.model), FindAndModifyOptions().returnNew(true), Car::class.java)
                    .map { car -> CarDTO().id(car.id).model(car.model) }

    fun deleteById(id: String): Mono<Void> = repository.deleteById(id)

}