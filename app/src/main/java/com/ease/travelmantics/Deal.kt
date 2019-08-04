package com.ease.travelmantics

import java.io.Serializable

class Deal : Serializable {
    var id: String? = null
    var title: String? = null
    var description: String? = null
    var price: String? = null
    var imageUrl: String? = null
    var imageName: String? = null

    constructor() {}
}
