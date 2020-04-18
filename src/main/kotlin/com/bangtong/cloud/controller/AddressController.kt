package com.bangtong.cloud.controller

import com.bangtong.cloud.model.Address
import com.bangtong.cloud.model.AddressRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(path = ["/cloud"])
class AddressController {

	@Autowired
	private val addressRepository: AddressRepository? = null

	@PostMapping(path = ["/address/insert"])
	@ResponseBody
	fun insertAddress(@RequestBody address: Address): Long {
		return addressRepository!!.save(address).id
	}

	@PostMapping(path = ["/address/delete"])
	@ResponseBody
	fun deleteAddress(@RequestBody address: Address): Boolean {
		addressRepository!!.delete(address)
		return true
	}

	@PostMapping(path = ["/address/update"])
	@ResponseBody
	fun updateAddress(@RequestBody address: Address): Boolean {
		addressRepository!!.save(address)
		return true
	}

	@GetMapping(path = ["/address"])
	@ResponseBody
	fun syncAddress(@RequestParam("user") user:String):List<Address>{
		return addressRepository!!.findByUser(user)
	}
}