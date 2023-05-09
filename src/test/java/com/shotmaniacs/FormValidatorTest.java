package com.shotmaniacs;

import com.shotmaniacs.models.Credentials;
import com.shotmaniacs.models.user.Person;
import com.shotmaniacs.models.user.ServerRole;
import com.shotmaniacs.utils.FormValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FormValidatorTest {

    private FormValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new FormValidator();
    }

    @Test
    public void failGivenLessThreeSymbolsInPassword() {
        assertFalse(validator.validatePassword("h2"));
        assertFalse(validator.validatePassword("3f"));
    }

    @Test
    public void failGivenOnlyDigitsInPassword() {
        assertFalse(validator.validatePassword("7618372816431"));
        assertFalse(validator.validatePassword("4851826325"));
        assertFalse(validator.validatePassword("83712622"));
    }

    @Test
    public void failGivenOnlyLettersInPassword() {
        assertFalse(validator.validatePassword("mvkocvnixocnbvxoiuvb"));
        assertFalse(validator.validatePassword("kjghsijfhefseuif"));
        assertFalse(validator.validatePassword("oseyrwieyri"));
    }

    @Test
    public void succeedGivenStrongPassword() {
        assertTrue(validator.validatePassword("irewgi32i32gf3ui2f"));
        assertTrue(validator.validatePassword("gyu12g32uy3u"));
        assertTrue(validator.validatePassword("123ttt2t"));
    }

    @Test
    public void failGivenInvalidEmail() {
        assertFalse(validator.validateEmail("hafuiehf3uif2h"));
        assertFalse(validator.validateEmail("@gmail.com"));
        assertFalse(validator.validateEmail("test@$##$com"));
    }

    @Test
    public void succeedGivenValidEmail() {
        assertTrue(validator.validateEmail("test@gmail.com"));
        assertTrue(validator.validateEmail("testdef@123.com"));
    }

    @Test
    public void failGivenInvalidPhone() {
        assertFalse(validator.validatePhone("+213213312323232"));
        assertFalse(validator.validatePhone("+3162g056333"));
        assertFalse(validator.validatePhone("0663333"));
    }

    @Test
    public void succeedGivenValidPhone() {
        assertTrue(validator.validatePhone("0620065560"));
        assertTrue(validator.validatePhone("(415)555-0132"));
        assertTrue(validator.validatePhone("+31620056666"));
    }

    @Test
    public void failGivenInvalidFullname() {
        assertFalse(validator.validateFullname("123GIAFIFWIU Fh"));
        assertFalse(validator.validateFullname("123"));
    }

    @Test
    public void succeedGivenValidFullname() {
        assertTrue(validator.validateFullname("John Smith"));
        assertTrue(validator.validateFullname("Jo Jo"));
    }

    @Test
    public void failGivenInvalidPerson() {
        assertFalse(validator.validatePerson(new Person(0, "Valid Fullname", "123",
                "valid.email@gmail.com", "123", ServerRole.CLIENT)));
        assertFalse(validator.validatePerson(new Person(0, "123", "(415)555-0132",
                "valid.email@gmail.com", "123", ServerRole.CLIENT)));
    }

    @Test
    public void succeedGivenValidPerson() {
        assertTrue(validator.validatePerson(new Person(0, "Valid Fullname", "(415)555-0132",
                "valid.email@gmail.com", "StrongPassword123", ServerRole.CLIENT)));
    }

    @Test
    public void failGivenInvalidCredentials() {
        assertFalse(validator.validateCredentials(new Credentials("123", "StrongPassword123")));
        assertFalse(validator.validateCredentials(new Credentials("valid.email@gmail.com", "123")));
        assertFalse(validator.validateCredentials(new Credentials("123", "123")));
    }

    @Test
    public void succeedGivenValidCredentials() {
        assertTrue(validator.validateCredentials(new Credentials("valid.email@gmail.com", "StrongPassword123")));
        assertTrue(validator.validateCredentials(new Credentials("123@ggg.com", "iafhui2uihfGEuh")));
    }
}