package com.bangtong.cloud.controller

import com.bangtong.cloud.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(path = ["/cloud"])
class OrderController {

	@Autowired
	private val orderRepository: OrderRepository? = null

	@PostMapping(path = ["/order/insert"])
	@ResponseBody
	fun insertOrder(@RequestBody order: OrderForm): Long {
		return orderRepository!!.save(order).id
	}

	@GetMapping(path = ["/order"])
	@ResponseBody
	fun syncOrder(@RequestParam("user") user:String):List<OrderForm>{
		return orderRepository!!.getUserOrder(user)
	}

	@PostMapping(path = ["/order/delete"])
	@ResponseBody
	fun deleteOrder(@RequestBody order: OrderForm): Boolean {
		orderRepository!!.delete(order)
		return true
	}
}