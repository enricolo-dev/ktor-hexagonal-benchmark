package com.quick.tor.usecases.user

import com.quick.tor.RequiresTransactionContext
import com.quick.tor.StartsNewTransaction
import com.quick.tor.TransactionService
import com.quick.tor.log.Logger
import com.quick.tor.usecases.user.port.primary.UserPort
import com.quick.tor.usecases.user.port.secondary.UserDataAccessPort
import com.quick.tor.usecases.user.model.User
import com.quick.tor.usecases.user.model.UserEvent
import com.quick.tor.usecases.user.port.secondary.UserEventDataAccessPort
import com.quick.tor.usecases.user.port.secondary.UserNotificationPort
import java.util.UUID

class UserUseCase(
    private val userNotificationPort: UserNotificationPort,
    private val userDataAccessPort: UserDataAccessPort,
    private val userEventDataAccessPort: UserEventDataAccessPort,
    private val transactionService: TransactionService,
    private val log: Logger
): UserPort {

    @OptIn(RequiresTransactionContext::class)
    override suspend fun save(user: User): User? = transactionService.transaction {

        val existsUser = userDataAccessPort.findByIdempotency(user.idempotencyId)
        if (existsUser !== null) return@transaction existsUser

        val savedUser = userDataAccessPort.save(user)
        val event = UserEvent(user = savedUser)
        val savedEvent = userEventDataAccessPort.save(event)

        return@transaction try {
            userNotificationPort.notify(savedEvent)
            userEventDataAccessPort.delete(savedEvent)
            userDataAccessPort.update(savedUser.withSendNotificationValidated())
        } catch (exception: Exception) {
            // TODO retry
            log.error("Error in event", exception)
            savedUser
        }

    }

    @OptIn(RequiresTransactionContext::class)
    override suspend fun findById(id: UUID): User? = transactionService.transaction {

        log.info("trying to find user with id: $id")
        val userFound = userDataAccessPort.findById(id)
        log.info("found user: $userFound")

        return@transaction userFound
    }

    @OptIn(RequiresTransactionContext::class)
    override suspend fun update(user: User): User? = transactionService.transaction {

        val updated = userDataAccessPort.update(user)
        val eventUpdated = userEventDataAccessPort.save(UserEvent(user = updated))

        log.info("Updated user: ${user.id}")

        try {
            userNotificationPort.notify(eventUpdated)
            userEventDataAccessPort.delete(eventUpdated)
        } catch (exception: Exception) {
            log.error("Error in event", exception)
            // TODO retry
        }
        return@transaction updated
    }

}