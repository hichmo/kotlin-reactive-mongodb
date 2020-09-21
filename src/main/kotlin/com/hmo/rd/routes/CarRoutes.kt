package com.hmo.rd.routes

import com.hmo.rd.entity.Car
import com.hmo.rd.handler.CarHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router


@Configuration
class CarRoutes(@Autowired val handler: CarHandler) {


    @Bean
    @RouterOperation(operation = Operation(operationId = "getAll", summary = "Get all cars", tags = ["Cars"],
            responses = [
                ApiResponse(responseCode = "200", description = "Successful operation",
                        content = arrayOf(Content(schema = Schema(implementation = Car::class))))
            ]))
    fun getAll() = router {
        GET("/cars", accept(MediaType.TEXT_EVENT_STREAM), handler::handleFindAll)
    }

    @Bean
    @RouterOperation(operation = Operation(operationId = "getCarById", summary = "Get car by id", tags = ["Cars"],
            parameters = [Parameter(`in` = ParameterIn.PATH, name = "id", description = "Car id")],
            responses = [ApiResponse(responseCode = "200", description = "successful operation",
                    content = [Content(schema = Schema(implementation = Car::class))]),
                ApiResponse(responseCode = "404", description = "Car not found")]))
    fun getById() = router {
        GET("/cars/{id}", handler::handleFindById)
    }

    @Bean
    @RouterOperation(operation = Operation(operationId = "create", summary = "Create a car", tags = ["Cars"],
            requestBody = RequestBody(description = "Car to create", required = true,
                    content = [Content(schema = Schema(implementation = Car::class))]),
            responses = [ApiResponse(responseCode = "201", description = "Successful operation",
                    content = arrayOf(Content(schema = Schema(implementation = Car::class)))),
                ApiResponse(responseCode = "404", description = "Car not found")]))
    fun create() = router {
        POST("/cars", accept(MediaType.APPLICATION_JSON), handler::handleCreate)
    }

    @Bean
    @RouterOperation(operation = Operation(operationId = "update", summary = "Update a car", tags = ["Cars"],
            parameters = [Parameter(`in` = ParameterIn.PATH, name = "id", description = "Car id")],
            requestBody = RequestBody(description = "Car to update", required = true,
                    content = [Content(schema = Schema(implementation = Car::class))]),
            responses = [ApiResponse(responseCode = "200", description = "Successful operation",
                    content = arrayOf(Content(schema = Schema(implementation = Car::class)))),
                ApiResponse(responseCode = "404", description = "Car not found")]))
    fun update() = router {
        PUT("/cars/{id}", accept(MediaType.APPLICATION_JSON), handler::handleUpdate)
    }

    @Bean
    @RouterOperation(operation = Operation(operationId = "deleteCarById", summary = "Delete car by id", tags = ["Cars"],
            parameters = [Parameter(`in` = ParameterIn.PATH, name = "id", description = "Car id")],
            responses = [ApiResponse(responseCode = "204", description = "successful operation",
                    content = [Content(schema = Schema(implementation = Car::class))]),
                ApiResponse(responseCode = "404", description = "Car not found")]))
    fun deleteById() = router {
        DELETE("/cars/{id}", handler::handleDelete)
    }
}