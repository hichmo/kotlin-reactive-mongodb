package com.hmo.rd.routes

import com.hmo.rd.entity.Car
import com.hmo.rd.handler.CarHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
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
    @RouterOperation(operation = Operation(
            operationId = "findCars",
            summary = "Find cars", tags = ["Cars"],
            description = "Return the list of cars",
            responses = [
                ApiResponse(responseCode = "200", description = "Successful operation",
                        content = [Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE, array = ArraySchema(schema = Schema(implementation = Car::class)))]
                )]))
    fun findAll() = router {
        GET("/cars", accept(MediaType.TEXT_EVENT_STREAM), handler::handleFindAll)
    }

    @Bean
    @RouterOperation(operation = Operation(
            operationId = "getCarById",
            summary = "Find car by id",
            description = "Returns a single car",
            tags = ["Cars"],
            parameters = [Parameter(`in` = ParameterIn.PATH, name = "id", description = "Id of car to return", schema = Schema(implementation = String::class))],
            responses = [
                ApiResponse(
                        responseCode = "200", description = "Successful operation",
                        content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Car::class))]),
                ApiResponse(responseCode = "404", description = "Car not found")]))
    fun getCarById() = router {
        GET("/cars/{id}", accept(MediaType.APPLICATION_JSON), handler::handleFindById)
    }

    @Bean
    @RouterOperation(operation = Operation(
            operationId = "addCar",
            summary = "Add a new car",
            description = "Car that needs to be added",
            tags = ["Cars"],
            requestBody = RequestBody(
                    description = "Car to add", required = true,
                    content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Car::class))]),
            responses = [ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    headers = arrayOf(Header(name = "location",
                            description = "URI of a newly created car",
                            schema = Schema(implementation = String::class))),
                    content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Car::class))])]))
    fun addCar() = router {
        POST("/cars", accept(MediaType.APPLICATION_JSON), handler::handleCreate)
    }

    @Bean
    @RouterOperation(operation = Operation(operationId = "updateCar", summary = "Update an existing car", tags = ["Cars"],
            parameters = [Parameter(`in` = ParameterIn.PATH, name = "id", description = "car id", schema = Schema(implementation = String::class))],
            requestBody = RequestBody(
                    description = "Car object that needs to be updated", required = true,
                    content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Car::class))]),
            responses = [
                ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Car::class))]),
                ApiResponse(responseCode = "404", description = "Car not found")]))
    fun updateCar() = router {
        PUT("/cars/{id}", accept(MediaType.APPLICATION_JSON), handler::handleUpdate)
    }

    @Bean
    @RouterOperation(operation = Operation(operationId = "deleteCar", summary = "Delete a car", tags = ["Cars"],
            parameters = [Parameter(`in` = ParameterIn.PATH, name = "id", description = "Car id to delete", schema = Schema(implementation = String::class))],
            responses = [
                ApiResponse(
                        responseCode = "204", description = "Successful operation"),
                ApiResponse(
                        responseCode = "404", description = "Car not found")]))
    fun deleteCar() = router {
        DELETE("/cars/{id}", handler::handleDelete)
    }
}