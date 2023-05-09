window.globalUrl = "http://shotmaniacs4.paas.hosted-by-previder.com/rest";

/**
 * When a page is being loaded, one of the loaders is used to fill the content depending on the pathname.
 * It also prevents the page reloading after a <a> link is clicked, so that it navigates using only javascript.
 */
$(document).ready(() => {
    navigateTo(location.pathname);
    $("body").click((e) => {
        if (e.target.matches("[data-link]")) {
            e.preventDefault();
            history.replaceState(null, null, e.target.href);
            navigateTo(location.pathname);
        }
    });
});

/**
 * Navigates to the provided url (without page reloading) using one of the loader functions that
 * updates the webpage view. If the function is not found, the default path will be chosen.
 *
 * @param url path to a page that the user has requested
 */
function navigateTo(url) {
    window.loaders[(url in window.loaders) ? url : defaultPath]();
}