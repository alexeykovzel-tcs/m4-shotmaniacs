window.defaultPath = "/login";

window.loaders = {
    "/login": loadLogin,
    "/signup": loadSignup,
    "/forgot-password": loadResetPassword
};

let nav = {
    "CREW": () => location.replace("/crew/dashboard"),
    "ADMIN": () => location.replace("/admin/dashboard"),
    "CLIENT": () => location.replace("/bookings"),
    "DEFAULT": () => history.back()
}

/**
 * Sends a post request to the server to authenticate either a new or an existing user. After that,
 * the user is redirected to their corresponding page depending on their role.
 *
 * @param data authentication details of the user
 * @param path path to which a request is forwarded
 */
function authenticate(data, path) {
    $.ajax({
        type: "POST",
        url: window.globalUrl + path,
        data: JSON.stringify(data),
        contentType: "application/json",
        success: (role) => nav[(role in nav) ? role : "DEFAULT"](),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Loads input fields for the user to fill their credentials in. After the user does that, they can
 * authenticate to their account.
 */
function loadLogin() {
    document.title = "Login";
    $("#panel").html(`
        <p class="title">Login</p>
        <h4>Sign in to your account to manage booking, follow their status \n and receive more details</h4>
        <div class="container">
            <label for="email">Email address</label>
            <input id="email" class="input-field" placeholder="E-mail" type="email">
            <label for="password">Password</label>
            <input id="password" class="input-field" placeholder="Password" type="password">
            <button id="login" class="button" type="button" onclick="authenticate({
                  email: $('#email').val(),
                  password: $('#password').val()},'/auth/login')">Log in
            </button>
            <a href="/forgot-password" data-link>Forgot your password?</a>
            <div style="display: flex">
                <hr class="line"><p style="padding: 0 20px">or<br></p><hr class="line">
            </div>
            <button class="white-btn" onclick="history.back()">
                <img src="../images/without-account.png" alt="Without Account">Continue without account
            </button>
            <button class="white-btn" onclick="loadSignup()">
                <img src="../images/new-account.png" alt="New Account">Create shotmaniacs account
            </button>
        </div>
    `);
}

/**
 * Loads signup input fields where the user can fill their details and register a new account.
 */
function loadSignup() {
    document.title = "Sign Up";
    $("#panel").html(`
        <p class="title">Sign Up</p>
        <h4>Create an account to manage bookings, follow their status \n and receive more details</h4>
         <div class="container">
            <label for="fullname">Full name</label>
            <input id="fullname" class="input-field" placeholder="Fullname" type="text">
            <label for="email">Email address</label>
            <input id="email" class="input-field" placeholder="E-mail" type="email">
            <label for="password">Password</label>
            <input id="password" class="input-field" placeholder="Password" type="password">
            <label for="repeat_password">Repeat Password</label>
            <input id="repeat_password" class="input-field" placeholder="Repeat Password" type="password">
            <button id="signup" class="button" type="button">Sign Up</button>
            <p>Already have an account?
                <a href="/login" data-link>Login</a>
            </p>
        </div>
    `);

    $("#signup").click(() => {
        let p1 = $("#password").val();
        let p2 = $("#repeat_password").val();
        if (p1 !== p2) alert("[ERROR] Passwords do not match");

        authenticate({
            email: $("#email").val(),
            fullname: $("#fullname").val(),
            password: p1,
        }, "/auth/signup")
    });
}

/**
 * Loads an input field for the user to fill their email in. After they do it, a confirmation is being
 * shown where the user should proceed to the next phase of resetting password.
 */
function loadResetPassword() {
    document.title = "Forgot Password?";
    $("#panel").html(`
        <p class="title">Forgot Password?</p>
        <h4>No problem, it happens to the best of us</h4>
        <div class="container">
            <label for="email">Email address</label>
            <input class="input-field" placeholder="E-mail" type="text">
            <button class="button" type="button" onclick="confirmReset()">Reset Password</button>
            <a href="/login" data-link>Cancel</a>
        </div>
   `);
}

/**
 * Displays a confirmation text on the page that the password is being reset.
 */
function loadResetPasswordConfirm() {
    document.title = "Forgot Password?";
    $("#panel").html(`
        <p class="title">Forgot Password?</p>
        <div class="container">
            <p>An email has been sent.</p>
            <p>Please click on the link when you get it.</p>
            <button class="button" type="button" onclick="navigateTo('/login')">Go Back</button>
        </div>
    `);
}

function confirmReset() {
    let email = $("#email").val();
    // TODO: Send confirmation letter to the user's mail box.
    loadResetPasswordConfirm();
}