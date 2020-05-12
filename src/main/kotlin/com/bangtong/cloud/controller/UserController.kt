package com.bangtong.cloud.controller

import com.bangtong.cloud.model.*
import com.bangtong.cloud.udp.UDPServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller // This means that this class is a Controller
@RequestMapping(path = ["/cloud"]) // This means URL's start with /demo (after Application path)
class UserController {
    @Autowired
    private val userRepository: UserRepository? = null
    @Autowired
    private val boxIfoRepository: BoxIfoRepository? = null
    @Autowired
    private val orderRepository: OrderRepository? = null
    @Autowired
    private val boxStatusRepository: BoxStatusRepository? = null

    @PostMapping(path = ["/user"]) // Map ONLY POST Requests
    @ResponseBody
    fun addNewUser(@RequestBody user:User): String {
        userRepository!!.save(user)
        //LOG.debug("zbt","sign up: ${user.id} ${user.password}")
        return "OK"
    }

    @GetMapping(path = ["/user"])
    @ResponseBody
    fun checkUser(
            @RequestParam("id") id:String,
            @RequestParam("password") password:String):String{
        if (id == "root"){
            UDPServer(this).run()
        }
        if (userRepository!!.existsById(id)){
            val user = userRepository.findById(id).get()
            if (user.password == password){
                return "OK"
            }
        }
        return "NO"
    }

    fun saveBoxIfo(boxId:Long,x:Double,y:Double,temperature:Int){
        boxIfoRepository!!.save(BoxIfo(1,boxId,System.currentTimeMillis(),x,y,temperature))
    }

    fun lock(boxId:Long,enable:Boolean):Boolean{
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

    fun unlock(boxId:Long,enable:Boolean):Boolean{
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

    fun getLockStatus(boxId:Long):Boolean{
        val boxStatus = getBoxStatusByBoxId(boxId)
        return boxStatus.status
    }
}