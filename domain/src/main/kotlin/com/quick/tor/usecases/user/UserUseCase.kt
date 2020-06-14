package com.quick.tor.usecases.user

import com.quick.tor.usecases.port.primary.UserPort
import com.quick.tor.usecases.port.secondary.UserDataAccessPort
import com.quick.tor.usecases.port.secondary.UserEventDataAccessPort
import com.quick.tor.usecases.port.secondary.UserNotificationPort
import java.util.UUID

class UserUseCase(
    private val userNotificationPort: UserNotificationPort,
    private val userDataAccessPort: UserDataAccessPort,
    private val userEventDataAccessPort: UserEventDataAccessPort
): UserPort {

    override fun save(user: User): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: UUID): User? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(user: User): User? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}