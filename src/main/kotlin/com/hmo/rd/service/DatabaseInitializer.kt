package com.hmo.rd.service

import com.hmo.rd.entity.Car
import com.hmo.rd.repository.CarRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DatabaseInitializer(@Autowired val repository: CarRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {
        repository.deleteAll().thenMany(repository.saveAll(mutableListOf(Car(null,"Renault Scenic"),
                Car(null,"Renault Captur"),
                Car(null,"Renault Kadjar")))).subscribe()
    }
}