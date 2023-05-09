let events = {};
let calendar;

let borderColors = {
    "Wedding": "green",
    "Commercial": "gray",
    "Business": "#7B70FF",
    "Festival": "orange",
    "Club": "pink",
};

$(document).ready(() => {
    // addTestEvents();
    initCalender();
    loadEvents();
});

/**
 * Loads events from either the local memory or the server and fills the list.
 */
function loadEvents() {
    if (Object.keys(events).length !== 0) {
        console.log("Loading events from local memory...")
        for (let id in events) addEvent(events[id]);
        return;
    }
    console.log("Loading events from server...")
    $.ajax({
        type: "GET",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/all",
        success: (data) => {
            console.log("Loaded events: " + data.length)
            data.forEach(e => addEvent(e));
        },
        error: (e) => alert(e.responseText)
    });
}

/**
 * Adds an event to the list using the following format:
 * Event: { id, name, bookingType, eventType, location, startDate, endDate, duration, notes, client,
 *      status, productManager, departmentType }
 *
 * @param event event to be added
 */
function addEvent(event) {
    let id = event["id"];
    let options = {month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric'}
    let startDate = new Date(event["startDate"]).toLocaleDateString("en-US", options);
    let endDate = new Date(event["endDate"]).toLocaleDateString("en-US", options);
    events[id] = event;

    $(".events").append(`
        <div class="cardEvent" id="event-${id}">
            <h2>${event["name"]}</h2>
            <p>${event["location"]}</p>
            <p>${startDate} - ${endDate}</p>
            <p>${event["notes"]}</p>

            <div class="penguins">
                <img src="/images/penguin.png" alt="Crew Member">
                <img src="/images/penguin.png" alt="Crew Member">
                <img src="/images/penguin.png" alt="Crew Member">
            </div>
        </div>
    `);

    let eventItem = $("#event-" + id);
    let type = event["eventType"];
    let borderColor = (type in borderColors) ? borderColors[type] : "gray";

    eventItem.css("border-color", borderColor);
    eventItem.click(() => openEventInfo(event));
    addEventToCalendar(event);
}

/**
 * Initializes the calendar by configuring its view position, what happens to the events when
 * they are created or when being clicked.
 */
function initCalender() {
    let calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        eventClick: (info) => {
            let id = info.el.fcSeg.eventRange.def.publicId;
            openEventInfo(events[id]);
        },
        eventDidMount: (info) => {
            info.event.id
            let type = events[info.event.id]["eventType"];
            let color = (type in borderColors) ? borderColors[type] : "gray";
            let elements = [
                info.el.querySelector(".fc-event-title"),
                info.el.querySelector(".fc-event-main-frame"),
                info.el.querySelector(".fc-event-title-container")
            ];
            elements.forEach(el => {
                if (el !== null) el.style.background = color;
            });
        }
    });
    calendar.render();
}

/**
 * Adds an event instance to the calendar.
 *
 * @param event event object that is being added.
 */
function addEventToCalendar(event) {
    calendar.addEvent({
        id: event["id"],
        title: event["name"],
        start: new Date(event["startDate"]),
        end: new Date(event["endDate"])
    });
}

/**
 * Loads events to the list for testing.
 */
function addTestEvents() {
    events[666] = {
        id: 666,
        name: "Test Event 2",
        bookingType: "Film",
        eventType: "Wedding",
        location: "Test Location 2",
        startDate: "2022-06-01 11:00",
        endDate: "2022-06-02 12:00",
        duration: 2,
        notes: "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Vitae sed perspiciatis, ex quod " +
            "nesciunt corporis id. Deserunt tempora ",
        client: {
            phone: "+222"
        },
        status: "To Do",
        productManager: 4,
        departmentType: null,
    };

    events[667] = {
        id: 667,
        name: "ZZZ",
        bookingType: "Photography",
        eventType: "Club",
        location: "Test Location 1",
        startDate: "2022-06-02 11:00",
        endDate: "2022-06-02 12:00",
        duration: 2,
        notes: "",
        client: {
            phone: "+222"
        },
        status: "In Progress",
        productManager: null,
        departmentType: null,
    };

    events[668] = {
        id: 668,
        name: "Test Event 1",
        bookingType: "Photography",
        eventType: "Club",
        location: "Test Location 1",
        startDate: "2022-05-30 11:00",
        endDate: "2022-05-30 12:00",
        duration: 2,
        notes: "nesciunt corporis id. Deserunt tempora",
        client: {
            phone: "+222"
        },
        status: "Review",
        productManager: null,
        departmentType: null,
    };

    events[669] = {
        id: 669,
        name: "Test Event 1",
        bookingType: "Photography",
        eventType: "Club",
        location: "Test Location 1",
        startDate: "2022-06-01",
        endDate: "2022-06-01",
        duration: 2,
        notes: "nesciunt corporis id. Deserunt tempora. nesciunt corporis id. Deserunt tempora",
        client: {
            phone: "+222"
        },
        status: "To Do",
        productManager: null,
        departmentType: null,
    };

    events[670] = {
        id: 670,
        name: "Strange feeling",
        bookingType: "Photography",
        eventType: "Club",
        location: "Test Location 1",
        startDate: "2022-06-01",
        endDate: "2022-06-01",
        duration: 2,
        notes: "nesciunt corporis id. Deserunt tempora. nesciunt corporis id. Deserunt tempora",
        client: {
            phone: "+222"
        },
        status: "To Do",
        productManager: null,
        departmentType: null,
    };
}
