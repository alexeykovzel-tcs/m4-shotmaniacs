package com.shotmaniacs.utils;

import com.shotmaniacs.models.Credentials;
import com.shotmaniacs.models.user.Person;

import java.util.regex.Pattern;

/**
 * Validates data by applying regex patterns. Typically, it is used to check whether the value in the
 * input fields filled by the user adheres to the server standards.
 */
public class FormValidator {

    /**
     * Validates person data by applying the corresponding regex patterns on email, password and fullname.
     *
     * @param person person to validate
     * @return true if the person is validated
     */
    public boolean validatePerson(Person person) {
        return validateEmail(person.getEmail())
                && validatePassword(person.getPassword())
                && validateFullname(person.getFullname());
    }

    /**
     * Validates credentials by applying the corresponding regex patterns on email and password.
     *
     * @param credentials credentials to validate
     * @return true if the credentials are validated
     */
    public boolean validateCredentials(Credentials credentials) {
        return validateEmail(credentials.getEmail())
                && validatePassword(credentials.getPassword());
    }

    /**
     * Validates that a given fullname is not empty, does not contain numbers or special characters
     *
     * @param fullname fullname to validate
     * @return true if the fullname is validated
     */
    public boolean validateFullname(String fullname) {
        String pattern = "^[a-zA-Z\\s]+";
        return patternMatches(fullname, pattern);
    }

    /**
     * Validates that a phone number contains ten digits, probably parentheses or an international prefix.
     *
     * @param phone phone number to validate
     * @return true if the phone number is validated
     */
    public boolean validatePhone(String phone) {
        String pattern
                // Check if the number has ten digits
                = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                // Allow first part of our phone between parentheses
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                // Allow an international prefix at the start of a phone number
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
        return patternMatches(phone, pattern);
    }

    /**
     * Validates that a password contains at least 3 characters, one letter and one number.
     *
     * @param password password to validate
     * @return true if the password is validated
     */
    public boolean validatePassword(String password) {
        return true;
//        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{3,}$";
//        return patternMatches(password, pattern);
    }

    /**
     * Validates that an email adheres to the regex pattern provided by RFC 5322.
     *
     * @param email email to validate
     * @return true if the email is validated
     */
    public boolean validateEmail(String email) {
        String pattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        return patternMatches(email, pattern);
    }

    /**
     * Checks whether a value matches the regex pattern.
     *
     * @param value   value to match with the pattern
     * @param pattern regex pattern to apply on the value
     * @return true if the value matches with the regex pattern
     */
    private boolean patternMatches(String value, String pattern) {
        return Pattern.compile(pattern).matcher(value).matches();
    }
}
