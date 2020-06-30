package com.project.scratchstudio.kith_andoid.model

import androidx.annotation.Keep
import com.project.scratchstudio.kith_andoid.network.model.board.Image
import com.project.scratchstudio.kith_andoid.network.model.category.Category
import com.project.scratchstudio.kith_andoid.network.model.city.City
import com.project.scratchstudio.kith_andoid.network.model.country.Country
import com.project.scratchstudio.kith_andoid.network.model.region.Region
import java.io.Serializable

@Keep
class BoardModelView : Serializable {

    var id: Int = 0

    var title = ""

    var description = ""

    var boardPhotoUrls: ArrayList<Image> = ArrayList()

    var newPhotos = ArrayList<PhotoModelView>()

    var enabled: Boolean = false

    var startDate = ""

    var organizerId: Int = 0

    var cost: Int = 0

    var organizerFirstName = ""

    var organizerLastName = ""

    var organizerMiddleName = ""

    var organizerPhone = ""

    var organizerPhotoUrl: String? = ""

    var subscriptionOnBoard: Boolean = false

    var country: Country? = null

    var region: Region? = null

    var city: City? = null

    var category: Category? = null

    var chatCount: Int = 0

    var isPaid: Boolean = false
}