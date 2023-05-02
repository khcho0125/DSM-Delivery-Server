package com.dsm.domain.mission.usecase

import com.dsm.exception.MissionException
import com.dsm.persistence.entity.Mission
import com.dsm.persistence.repository.MissionRepository
import com.dsm.plugins.database.dbQuery
import java.time.LocalDateTime

/**
 *
 * 배달 미션 게시를 담당하는 PostMission
 *
 * @author Chokyunghyeon
 * @date 2023/04/26
 **/
class PostMission(
    private val missionRepository: MissionRepository
) {

    suspend operator fun invoke(request: Request, studentId: Int) : Unit = dbQuery {
        if (missionRepository.existsByStudentId(studentId)) {
            throw MissionException.AlreadyPosted()
        }

        missionRepository.insert(Mission.doPost(
            orderId = studentId,
            stuff = request.stuff,
            deadline = request.deadline,
            price = request.price
        ))
    }

    data class Request(
        val stuff: String,
        val deadline: LocalDateTime,
        val price: Long
    )
}