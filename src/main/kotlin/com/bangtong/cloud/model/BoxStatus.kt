package com.bangtong.cloud.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BoxStatus (
	@Id
	val boxId:Long,
	var orderId:Long,
	var lockt:Boolean,
	var unlockt:Boolean,
	var status:Boolean
)

interface BoxStatusRepository: CrudRepository<BoxStatus, Long> {

//	@Query("SELECT * FROM box_ifo WHERE box_id=:boxId", nativeQuery = true)
//	fun getBoxIfo(@Param("boxId")boxId: Long):List<BoxIfo>
}