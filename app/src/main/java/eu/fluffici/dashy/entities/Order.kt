package eu.fluffici.dashy.entities

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
@Parcelize
@Serializable
data class Order(
    val id: Int,
    val order_id: String?,
    val first_name: String?,
    val last_name: String?,
    val first_address: String?,
    val second_address: String?,
    val postal_code: String?,
    val country: String?,
    val email: String?,
    val phone_number: String?,
    val status: String?,
    val customer_id: String?,
    val created_at: String?,
    val updated_at: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(this.id)
        parcel.writeString(this.order_id)
        parcel.writeString(this.first_name)
        parcel.writeString(this.last_name)
        parcel.writeString(this.first_address)
        parcel.writeString(this.second_address)
        parcel.writeString(this.postal_code)
        parcel.writeString(this.country)
        parcel.writeString(this.email)
        parcel.writeString(this.phone_number)
        parcel.writeString(this.status)
        parcel.writeString(this.customer_id)
        parcel.writeString(this.created_at)
        parcel.writeString(this.updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}