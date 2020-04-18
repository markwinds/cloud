package com.bangtong.cloud.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Address(
    @Id @GeneratedValue(strategy= GenerationType.AUTO) val id:Long,
    val user:String,
    val name:String,
    val phone:String,
    val province:String,
    val city:String,
    val area:String,
    val location:String,
    val live:Boolean
)

interface AddressRepository: CrudRepository<Address, Long>{

	fun findByUser(user: String):List<Address>
	@Query("SELECT * FROM address WHERE user=:user AND live=TRUE", nativeQuery = true)
	fun getAllLiveAddress(@Param("user")user: String):List<Address>

}