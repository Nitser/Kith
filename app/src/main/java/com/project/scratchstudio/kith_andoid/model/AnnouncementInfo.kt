package com.project.scratchstudio.kith_andoid.model

import java.io.Serializable

class AnnouncementInfo : Serializable {
    var id: Int = 0
    lateinit var title: String

    var enabled: Int = 0
    var boardSubscription: Int = 0
    var subscriptionOnBoard: Int = 0

    lateinit var startDate: String
    lateinit var endDate: String

    lateinit var participants: String
    lateinit var needParticipants: String
    lateinit var url: String
    lateinit var description: String

    var organizerId: Int = 0
    lateinit var organizerName: String
    lateinit var organizerLastName: String
    lateinit var organizerMiddleName: String
    lateinit var organizerLogin: String
    lateinit var organizerPhoto: String
    lateinit var organizerPhone: String

}
