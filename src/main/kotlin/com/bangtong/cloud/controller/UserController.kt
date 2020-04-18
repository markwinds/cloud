package com.bangtong.cloud.controller

import com.bangtong.cloud.model.User
import com.bangtong.cloud.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller // This means that this class is a Controller
@RequestMapping(path = ["/cloud"]) // This means URL's start with /demo (after Application path)
class UserController {
    @Autowired
    private val userRepository: UserRepository? = null

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
        if (userRepository!!.existsById(id)){
            val user = userRepository.findById(id).get()
            if (user.password == password){
                return "OK"
            }
        }
        return "NO"
    }
}