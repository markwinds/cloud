package com.bangtong.cloud.controller

import com.bangtong.cloud.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.swing.Box

@Controller
@RequestMapping(path = ["/cloud"])
class BindController {

	@Autowired
	private val orderRepository: OrderRepository? = null
	@Autowired
	private val boxStatusRepository: BoxStatusRepository? = null
	@Autowired
	private val boxIfoRepository: BoxIfoRepository? = null

	@GetMapping(path = ["/bind"])
	@ResponseBody
	fun bindBoxOrder(@RequestParam("boxId") boxId:Long,@RequestParam("orderId") orderId:Long):Boolean{
		val result = boxStatusRepository!!.findById(boxId)
		if (result.isEmpty){
			return false
		}
		val boxStatus = result.get()
		if (boxStatus.lockt && !boxStatus.status){
			boxStatus.lockt = false
			boxStatus.status = true
			boxStatus.orderId = orderId
			boxStatusRepository.save(boxStatus)
			val order = orderRepository!!.findById(orderId).get()
			order.boxId = boxId
			orderRepository.save(order)
			return true
		}
		return false
	}

	@GetMapping(path = ["/unbind"])
	@ResponseBody
	fun unbindBoxOrder(@RequestParam("boxId") boxId:Long):Boolean{
		val result = boxStatusRepository!!.findById(boxId)
		if (result.isEmpty){
			return false
		}
		val boxStatus = result.get()
		if (boxStatus.unlockt && boxStatus.status){
			boxStatus.unlockt = false
			boxStatus.status = false
			boxStatusRepository.save(boxStatus)
			val order = orderRepository!!.findById(boxStatus.orderId).get()
			order.boxId = (-1).toLong()
			order.timeReceive = System.currentTimeMillis()
			orderRepository.save(order)
			deleteBoxIfo(boxId)
			return true
		}
		return false
	}

	@GetMapping(path = ["/lock"])
	@ResponseBody
	fun lock(@RequestParam("boxId") boxId:Long,@RequestParam("enable") enable:Boolean):Boolean{
		val boxStatus = getBoxStatusByBoxId(boxId)
		return if (enable){
			if (!boxStatus.status){
				boxStatus.lockt = true
				boxStatusRepository!!.save(boxStatus)
				true
			}else false
		}else{
			boxStatus.lockt = false
			boxStatusRepository!!.save(boxStatus)
			boxStatus.status
		}
	}

	@GetMapping(path = ["/unlock"])
	@ResponseBody
	fun unlock(@RequestParam("boxId") boxId:Long,@RequestParam("enable") enable:Boolean):Boolean{
		val boxStatus = getBoxStatusByBoxId(boxId)
		return if (enable){
			if (boxStatus.status){
				boxStatus.unlockt = true
				boxStatusRepository!!.save(boxStatus)
				true
			}else false
		}else{
			boxStatus.unlockt = false
			boxStatusRepository!!.save(boxStatus)
			!boxStatus.status
		}
	}

	fun getBoxStatusByBoxId(boxId:Long):BoxStatus{
		val result = boxStatusRepository!!.findById(boxId)
		val boxStatus:BoxStatus?
		if (result.isEmpty){
			boxStatus = BoxStatus(boxId,0,false, unlockt = false, status = false)
			boxStatusRepository.save(boxStatus)
		}else{
			boxStatus = result.get()
		}
		return boxStatus
	}

	fun deleteBoxIfo(boxId: Long){
		val list = boxIfoRepository!!.getBoxIfo(boxId)
		for (b in list){
			boxIfoRepository.delete(b)
		}
	}

}