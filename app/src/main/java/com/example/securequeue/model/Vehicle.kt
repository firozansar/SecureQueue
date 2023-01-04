package com.example.securequeue.model

enum class VehicleType {
    TRUCK,
    PLANE,
    UNKNOWN
}

// Parent
sealed class Vehicle(open val vehicleType: VehicleType)

// Child 1
data class Truck(override val vehicleType: VehicleType = VehicleType.TRUCK, val waterCannon: Boolean) :
    Vehicle(vehicleType)

// Child 2
data class Plane(override val vehicleType: VehicleType = VehicleType.PLANE, val wingsSpanInMeters: Int) :
    Vehicle(vehicleType)

// Child 3
data class DefaultVehicle(override val vehicleType: VehicleType = VehicleType.UNKNOWN) :
    Vehicle(vehicleType)