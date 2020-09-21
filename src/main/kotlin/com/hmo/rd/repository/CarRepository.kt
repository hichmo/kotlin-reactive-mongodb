package com.hmo.rd.repository

import com.hmo.rd.entity.Car
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CarRepository : ReactiveMongoRepository<Car, String> {
}