package com.coded.capstone.formstates.kyc


data class KycFormState(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val salary: String = "",
    val nationality: String = "",
    val mobileNumber: String="",
    val civilId: String ="",

    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val dateOfBirthError: String? = null,
    val salaryError: String? = null,
    val nationalityError: String? = null,
    val mobileNumberError: String? = null,
    val civilIdError: String? = null,
) {
    val formIsValid: Boolean
        get() = listOfNotNull(
            firstNameError, lastNameError, dateOfBirthError,
            salaryError, nationalityError, mobileNumberError, civilIdError
        ).isEmpty()

    fun validate(): KycFormState {
        return this.copy(
            firstNameError = if (firstName.isBlank()) "First name is required" else null,
            lastNameError = if (lastName.isBlank()) "Last name is required" else null,
            dateOfBirthError = if (dateOfBirth.isBlank()) "Date of birth is required" else null,
            salaryError = if (salary.toBigDecimalOrNull() == null) "Enter a valid salary" else null,
            nationalityError = if (nationality.isBlank()) "Nationality is required" else null,
            mobileNumberError = if(mobileNumber.isBlank())"Mobile Number is required" else null,
            civilIdError = if(civilId.isBlank()) "Please Enter a valid civil Id" else null
        )
    }
}
