package com.bangtong.cloud.controller

import com.bangtong.cloud.model.BoxIfo
import com.bangtong.cloud.model.BoxIfoRepository
import com.bangtong.cloud.model.OrderForm
import com.bangtong.cloud.model.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(path = ["/cloud"])
class BoxIfoController {

	@Autowired
	private val boxIfoRepository: BoxIfoRepository? = null

	@GetMapping(path = ["/boxIfo"])
	@ResponseBody
	fun getBoxIfo(@RequestParam("id") id:Long):List<BoxIfo>{
		return boxIfoRepository!!.getBoxIfo(id)
	}

	@PostMapping(path = ["/boxIfo/insert"])
	@ResponseBody
	fun insertBoxIfo(@RequestBody boxIfo: BoxIfo): Long {
		return boxIfoRepository!!.save(boxIfo).id
	}
}