let eventMode = "approve";
let events = {};

$(document).ready(() => {
    loadEvents();
    // addTestIncomingEvents();
});

/**
 * Loads events from either the local memory or the server and fills the list.
 */
function loadEvents() {
    if (Object.keys(events).length !== 0) {
        for (let id in events) addEvent(events[id]);
        return;
    }
    $.ajax({
        type: "GET",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/all?status=to_approve",
        success: (data) => {
            data.forEach(i => addEvent(i));
        },
        error: (e) => alert(e.responseText)
    });
}

/**
 * Approves an incoming event by id, and then removes it from the list.
 *
 * @param id identifier of the event to be approved.
 */
function approveEvent(id) {
    $.ajax({
        type: "PUT",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/all/" + id + "?status=todo",
        success: () => $("#event-" + id).remove(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Refuses an incoming event by id. It also removes it from the list.
 *
 * @param id identifier of the event to be refused.
 */
function refuseEvent(id) {
    console.log("Deleting event: " + id);
    $.ajax({
        type: "DELETE",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/all/" + id,
        success: () => $("#event-" + id).remove(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Adds an incoming event to the list using the following format:
 * Event: { id, name, bookingType, eventType, location, startDate, endDate, duration, notes, client,
 *      status, productManager, departmentType }
 *
 * @param event incoming event to be added
 */
function addEvent(event) {
    let id = event["id"];
    let options = {month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric'}
    let startDate = new Date(event["startDate"]).toLocaleDateString("en-US", options);
    let notes = event["notes"];
    events[id] = event;

    $("header").after(`
        <div class="event-box" id="event-${id}">
            <div class="event-details">
                <p class="event-title">${event["name"]}</p>
                <div class="event-desc">
                    <p>${event["eventType"]}</p>
                    <p>${event["location"]}</p>
                    <p>${startDate}</p>
                    <p>${event["duration"]}h</p>
                </div>
            </div>
        </div>
    `);

    // adds buttons that allow to interact with an event
    addEventOptions(id);

    // adds notes at the bottom of the event description
    if (notes !== null && notes !== "") {
        $("#event-" + id).find(".event-desc").after(`
            <p style="margin-top: 2rem">${notes}</p>
        `);
    }
}

function addEventOptions(id) {
    let event = $("#event-" + id);
    switch (eventMode) {
        case "approve":
            event.append(`
                <div class="event-options">
                    <button class="icon btn-info" onclick="openEventInfo(${id})"></button>
                    <button class="icon btn-approve" onclick="approveEvent(${id})"></button>
                    <button class="icon btn-refuse" onclick="refuseEvent(${id})"></button>
                </div>
            `);
            break;
        case "view":
            event.append(`
                <div class ="event-options align-self-end gap-4">
                    <button class="btn px-5 py-2" onclick="openEventInfo(events[${id}])">Edit</button>
                    <button class="btn cancel px-5 py-2" onclick="refuseEvent(${id})">Cancel</button>
                </div>
            `);
            break;
    }
}

/**
 * Adds incoming events to the list for testing.
 */
function addTestIncomingEvents() {
    addEvent({
        id: 666,
        name: "Test Event 2",
        bookingType: "FILM",
        eventType: "WEDDING",
        location: "Test Location 2",
        startDate: 0,
        endDate: 0,
        duration: 2,
        notes: "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Vitae sed perspiciatis, ex quod " +
            "quibusdam velit ad, asperiores, nesciunt sunt voluptates odit voluptate corporis id. Deserunt tempora " +
            "qui similique sit! Voluptatem dicta eveniet magni est ab sit quisquam sapiente quis? Facere dolore " +
            "atque laudantium ab amet praesentium tenetur enim nulla at!",
        client: null,
        status: "TO_APPROVE",
        productManager: null,
        departmentType: null,
    });

    addEvent({
        id: 667,
        name: "Test Event 1",
        bookingType: "PHOTOGRAPHY",
        eventType: "CLUB",
        location: "Test Location 1",
        startDate: 0,
        endDate: 0,
        duration: 2,
        notes: "",
        client: null,
        status: "TO_APPROVE",
        productManager: null,
        departmentType: null,
    });
}