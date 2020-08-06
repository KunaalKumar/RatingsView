package dev.kunaal.ratings_view

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class SavedState : View.BaseSavedState {
    var rating = 0
    var arcColor = 0
    var arcWidthScale = 0F
    var bgColor = 0
    var textColor = 0
    var textScale = 0F

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState= SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    internal constructor(superState: Parcelable?) : super(superState)

    private constructor(parcel: Parcel) : super(parcel) {
        rating = parcel.readInt()
        arcColor = parcel.readInt()
        arcWidthScale = parcel.readFloat()
        bgColor = parcel.readInt()
        textColor = parcel.readInt()
        textScale = parcel.readFloat()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(rating)
        out.writeInt(arcColor)
        out.writeFloat(arcWidthScale)
        out.writeInt(bgColor)
        out.writeInt(textColor)
        out.writeFloat(textScale)
    }
}