package com.bangtong.cloud.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class OrderForm(
		@Id @GeneratedValue(strategy= GenerationType.AUTO) val id:Long,
		val user:String,
		val senderName:String,
		val senderPhone:String,
		val senderProvince:String,
		val senderCity:String,
		val senderArea:String,
		val senderLocation:String,
		val receiverName:String,
		val receiverPhone:String,
		val receiverProvince:String,
		val receiverCity:String,
		val receiverArea:String,
		val receiverLocation:String,
		val remark:String,
		val timeSend: Long,
		var timeReceive: Long,
		var boxId:Long
)

interface OrderRepository: CrudRepository<OrderForm, Long> {

	@Query("SELECT * FROM order_form WHERE user=:user OR receiver_phone=:user", nativeQuery = true)
	fun getUserOrder(@Param("user")user: String):List<OrderForm>
}