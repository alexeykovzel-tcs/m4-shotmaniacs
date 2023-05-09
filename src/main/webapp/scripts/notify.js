$(document).ready(() => {
    loadAnnouncements();
    // addTestNotifications();
});

function addNotification(item) {
    let date = new Date(item["timestamp"]);
    $(".dropdown-wrapper").append(`
        <div class="notify_item">
            <div class="notify_img">
                <img src="/images/penguin.png" alt="profile_pic" style="width: 50px">
            </div>
            <div class="notify_info">
                <h4>${item["title"]}</h4>
                <p>${item["body"]}</p>
                <span class="notify_time">${getTimeDiff(date)}</span>
            </div>
        </div>
    `);
}

function getTimeDiff(date) {
    let diffSec = Math.round((new Date().getTime() - date.getTime()) / 1000);
    if (diffSec <= 60) return diffSec + " seconds ago"; 
    let diffMin = Math.round(diffSec / 60);
    if (diffMin <= 60) return diffMin + " minutes ago";
    let diffHours = Math.round(diffMin / 60);
    if (diffHours <= 24) return diffHours + " hours ago";
    let diffDays = Math.round(diffHours / 24);
    return diffDays + " days ago";
}

function loadAnnouncements() {
    $.ajax({
        type: "GET",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/announcements",
        success: (data) => {
            data.reverse().forEach(item => addNotification(item));
        },
        error: (e) => alert(e.responseText)
    });
}

/**
 * Loads test notifications to the list.
 */
 function addTestNotifications() {
    addNotification({
        id: 666,
        title: "Some title 1",
        body: "Lorem ipsum dolor sitLorem ipsum dolor sit amet consectetur adipisicing elit. NLorem ipsum dolor " +
            "sit amet consectetur adipisicing elit. NLorem ipsum dolor sit amet consectetur adipisicing elit. " +
            "os impedit corporis ipsa quibusdam?",
        timestamp: "2022-06-30 11:00",
        urgency: "LEVEL_3",
        department: null,
        receiverId: 0,
    });

    addNotification({
        id: 666,
        title: "Some title 1",
        body: "Lorem ipsum dolor sitLorem ipsum dolor sit amet consectetur adipisicing elit. NLorem ipsum dolor " +
            "sit amet consectetur adipisicing elit. NLorem ipsum dolor sit amet consectetur adipisicing elit. " +
            "os impedit corporis ipsa quibusdam?",
        timestamp: "2022-06-30 11:00",
        urgency: "LEVEL_3",
        department: null,
        receiverId: 0,
    });

    addNotification({
        id: 666,
        title: "Some title 1",
        body: "Lorem ipsum dolor sitLorem ipsum dolor sit amet consectetur adipisicing elit. NLorem ipsum dolor " +
            "sit amet consectetur adipisicing elit. NLorem ipsum dolor sit amet consectetur adipisicing elit. " +
            "os impedit corporis ipsa quibusdam?",
        timestamp: "2022-06-30 11:00",
        urgency: "LEVEL_3",
        department: null,
        receiverId: 0,
    });

    addNotification({
        id: 667,
        title: "Some title 2",
        body: "Lorem ipsum dolor sit amet consectetur adipisicing elit?",
        timestamp: "2022-06-30 16:00",
        urgency: "LEVEL_2",
        department: null,
        receiverId: 0,
    });
}