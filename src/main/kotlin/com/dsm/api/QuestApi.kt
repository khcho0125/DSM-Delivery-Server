package com.dsm.api

import com.dsm.domain.quest.usecase.AcceptQuest
import com.dsm.domain.quest.usecase.GetQuest
import com.dsm.domain.quest.usecase.PublishQuest
import com.dsm.exception.DomainException
import com.dsm.plugins.currentUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 *
 * 배달 퀘스트의 API를 관리하는 QuestApi
 *
 * @author Chokyunghyeon
 * @date 2023/04/26
 **/
class QuestApi(
    publishQuest: PublishQuest,
    getQuest: GetQuest,
    acceptQuest: AcceptQuest
) : Api({
    route("/mission") {
        get {
            call.respond(
                message = getQuest(),
                status = HttpStatusCode.OK
            )
        }
    }

    authenticate("/quest") {
        post {
            val request: PublishQuest.Request = call.receive()
            val studentId: Int = call.currentUserId()

            publishQuest(request, studentId)

            call.response.status(HttpStatusCode.NoContent)
        }

        patch("/{quest-id}") {
            val questId: Int = call.parameters["quest-id"]?.toInt()
                ?: throw DomainException.BadRequest("Require Quest ID")

            val studentId: Int = call.request.queryParameters["student-id"]?.toInt()
                ?: throw DomainException.BadRequest("Require Student ID")

            acceptQuest(questId, studentId)

            call.response.status(HttpStatusCode.NoContent)
        }

        delete("/{quest-id}") {

        }
    }
}) {
    companion object {
        val module: Module = module {
            singleOf(::AcceptQuest)
            singleOf(::PublishQuest)
            singleOf(::GetQuest)
            singleOf(::QuestApi) bind Api:: class
        }
    }
}
