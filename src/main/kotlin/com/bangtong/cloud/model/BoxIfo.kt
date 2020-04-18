package com.bangtong.cloud.model

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BoxIfo(
	@Id @GeneratedValue(strategy= GenerationType.AUTO) val id:Long,
	val boxId:Long,
	val time:Long,
	val x:Double,
	val y:Double,
	val temperature:Int
)

interface BoxIfoRepository: CrudRepository<BoxIfo, Long> {

	@Query("SELECT * FROM box_ifo WHERE box_id=:boxId", nativeQuery = true)
	fun getBoxIfo(@Param("boxId")boxId: Long):List<BoxIfo>

//	@Query("DELETE FROM box_ifo WHERE box_id=:boxId", nativeQuery = true)
//	fun deleteBoxIfo(@Param("boxId")boxId: Long)
}