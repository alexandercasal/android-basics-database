package com.alexander.udacity.udacity_pets_sqlite.data

import android.net.Uri
import android.provider.BaseColumns

class PetContract {

    companion object {
        const val CONTENT_AUTHORITY: String = "com.alexander.udacity.udacity_pets_sqlite"
        val BASE_CONTENT_URI = Uri.parse("content://${CONTENT_AUTHORITY}")

        const val PATH_PETS = "pets"
    }

    class PetEntry {
        companion object {
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)
            const val TABLE_NAME: String = "pets"

            const val _ID = BaseColumns._ID
            const val COLUMN_PET_NAME: String = "name"
            const val COLUMN_PET_BREED: String = "breed"
            const val COLUMN_PET_GENDER: String = "gender"
            const val COLUMN_PET_WEIGHT: String = "weight"

            // Gender constants
            const val GENDER_MALE: Int = 1
            const val GENDER_FEMALE: Int = 2
            const val GENDER_UNKNOWN: Int = 0

            fun isValidName(name: String?):Boolean {
                return name != null && name.isNotEmpty()
            }

            fun isValidBreed(breed: String?): Boolean {
                return breed != null && breed.isNotEmpty()
            }

            fun isValidWeight(weight: Int?): Boolean {
                return weight != null && weight > 0
            }

            fun isValidGender(gender: Int?): Boolean {
                return gender != null && (gender == GENDER_MALE || gender == GENDER_FEMALE
                        || gender == GENDER_UNKNOWN)
            }
        }
    }
}