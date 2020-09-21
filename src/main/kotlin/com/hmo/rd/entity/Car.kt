package com.hmo.rd.entity

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.mongodb.core.mapping.Document

@Document("cars")
data class Car(@JsonProperty(access = JsonProperty.Access.READ_ONLY) var id: String? = null,
               var model: String)