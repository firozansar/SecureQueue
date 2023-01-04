package com.example.securequeue.model

enum class VehicleType {
    TRUCK,
    PLANE;
}

// Parent
sealed interface Vehicle {
    val type: VehicleType
}

// Child 1
data class Truck (val waterCannon: Boolean): Vehicle {
    override val type = VehicleType.TRUCK
}

// Child 2
data class Plane (val wingsSpanInMeters: Int): Vehicle {
    override val type = VehicleType.PLANE
}
