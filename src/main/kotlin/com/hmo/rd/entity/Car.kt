package com.hmo.rd.entity

import org.springframework.data.mongodb.core.mapping.Document

@Document("cars")
data class Car(var id: String? = null, var model: String)