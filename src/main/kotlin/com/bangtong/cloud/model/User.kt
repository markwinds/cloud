package com.bangtong.cloud.model

import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class User(
    @Id val id:String,
    val password:String
)

interface UserRepository: CrudRepository<User, String>