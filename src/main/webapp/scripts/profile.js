let userRole;
let userId;

$(document).ready(() => {
    // Timeout is set as cookies are loaded slower than the document
    setTimeout(() => loadProfile(), 500);
});

/**
 * Retrieves the user data from the server and displays it instead of the login button at the top.
 * If the pathname is "/profile", then the data is also filled into the corresponding input fields.
 */
function loadProfile() {
    $.ajax({
        type: "GET",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/profile",
        success: (data) => {
            console.log("Loading profile: " + JSON.stringify(data));
            userRole = data["role"];
            userId = data["id"];

            $(".profile").css("display", "flex");
            $("#fullname").text(`${data["fullname"]} (${data["role"]})`);
            $("#email").text(`${data["email"]}`);

            if (location.pathname === "/profile") {
                $("#fullname-input").val(data["fullname"]);
                $("#phone-input").val(data["phone"]);
                $("#email-input").val(data["email"]);
                $(".profile-options").css("display", "flex");
            }
        },
        error: () => {
            console.log("Failed to load profile");
            $(".login-btn").css("display", "block");
        }
    });
}

/**
 * Retrieves data from the input fields on the page and sends it to the server to update the user profile.
 * If success, the data is also updated on the page ( without reloading ).
 */
function updateProfile() {
    let fullname = $("#fullname-input").val();
    let phone = $("#phone-input").val();
    let email = $("#email-input").val();

    $.ajax({
        type: "PUT",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/profile",
        data: JSON.stringify({
            id: userId,
            fullname: fullname,
            phone: phone,
            email: email,
        }),
        contentType: "application/json",
        success: () => {
            console.log("Profile is successfully updated");
            $("#fullname").text(`${fullname} (${userRole})`);
            $("#email").text(`${email}`);
        },
        error: () => console.log("[ERROR] Failed to update profile")
    });
}

/**
 * Sends a request to the server to delete a user profile.
 * If success, the user is redirected to the index page.
 */
function deleteProfile() {
    $.ajax({
        type: "DELETE",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/profile/" + userId,
        success: () => location.replace("/"),
        error: () => console.log("[ERROR] Failed to delete profile")
    });
}

/**
 * Sends a logout request to the server.
 * If success, the user is redirected to the index page.
 */
function logout() {
    $.ajax({
        type: "POST",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/auth/logout",
        success: () => location.replace("/"),
        error: () => console.log("[ERROR] Failed to log out")
    });
}