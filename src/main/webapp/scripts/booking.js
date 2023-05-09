let bookingMode = "view";

let defaultEvent = ({
    name: null,
    bookingType: "Choose a type",
    eventType: "Choose a type",
    location: null,
    startDate: null,
    endDate: null,
    duration: null,
    notes: null,
    client: {phone: null},
    status: "To Do",
    productManager: null,
    departmentType: null,
});

$(document).ready(() => {
    addDropdown($("#type-options-wrapper"), $("#type-selected"));
    addDropdown($("#booking-options-wrapper"), $("#booking-selected"));
    addDropdown($("#status-options-wrapper"), $("#status-selected"));
    addDropdown($("#department-options-wrapper"), $("#department-selected"));
    changeBookingOptions("create");
    $("#event-table").change(() => uploadFile());
});

/**
 * Sets listeners dropdown menus, so that they are being triggered when being clicked.
 *
 * @param options options of a dropdown menu
 * @param selected currently selected item
 */
function addDropdown(options, selected) {
    selected.click(() => options.toggleClass("active"));
    options.find(".option").click((event) => {
        let option = event.target.innerText;
        if (option === "") return;
        console.log("Choosing option: " + option);
        selected.text(option);
        options.removeClass("active");
    });
}

/**
 * Opens/closes a booking popup. If the popup is being closed, all of its data is being reset.
 */
function toggleBookingPopup() {
    let popup = $("#booking-popup");
    let hidden = (popup.css("display") === "none");
    if (!hidden) {
        fillBookingPopup(defaultEvent);
        changeBookingOptions("create");
    }
    popup.css("display", hidden ? "flex" : "none");
}

/**
 * Changes event options of a booking popup between different modes
 *
 * @param mode determines which options are loaded
 */
function changeBookingOptions(mode) {
    let options = $(".booking-options");
    options.empty();
    switch (mode) {
        case "create":
            options.append(`
               <form method="post" action="" enctype="multipart/form-data" id="file">
                   <input type="file" id="event-table" style="display: none" name="file">
                   <label for="event-table" class="btn">Select file</label>
               </form>
               <button type="button" class="btn" onclick="bookEvent()">Submit</button>
            `);
            break;
        case "view":
            options.append(`
                <button id="edit-btn" type="button" class="btn">Edit</button>
                <button id="delete-btn" type="button" class="btn cancel">Delete</button>
            `);
            break;
        case "assign":
            options.append(`
               <button type="button" class="btn" onclick="assignEvent()">Assign</button>
            `);
            break;
    }
}

function assignEvent(id) {
    $.ajax({
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/" + id + "/assigned",
        data: JSON.stringify({
            memberId: userId,
            eventId: id
        }),
        contentType: "application/json",
        success: () => location.reload(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Fills a booking popup with the data from an event instance.
 *
 * @param event data that is filled in the input fields/dropdown menus of the booking popup
 */
function fillBookingPopup(event) {
    $("#event-name").val(event["name"]);
    $("#event-location").val(event["location"]);

    $("#type-selected").text(event["eventType"] || "Choose a type")
    $("#booking-selected").text(event["bookingType"] || "Choose a type");
    $("#department-selected").text(event["departmentType"] || "Choose a department");
    $("#status-selected").text(event["status"] || "To Do");

    if (event["startDate"] !== null) {
        let sDate = new Date(event["startDate"]);
        $("#startDate").val(toDate(sDate));
        $("#startTime").val(toTime(sDate));
    }

    if (event["endDate"] !== null) {
        let eDate = new Date(event["endDate"]);
        $("#endDate").val(toDate(eDate));
        $("#endTime").val(toTime(eDate));
    }

    $("#phone").val(event["client"]["phone"]);
    $("#event-duration").val(event["duration"]);
    $("#event-notes").val(event["notes"]);
}

/**
 * Sends an Excel file to the server to save multiple events.
 * If success, the page is reloaded to apply the changes on the page.
 */
function uploadFile() {
    let data = new FormData();
    let table = $("#event-table");
    let file = table[0].files[0];
    data.append('file', file);

    $.ajax({
        type: "POST",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/xssf",
        data: data,
        contentType: false,
        processData: false,
        success: () => location.reload(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Retries the data from the booking popup and converts it to a javascript object of the following format:
 *
 * Event: { id, name, bookingType, eventType, location, startDate, endDate, duration, notes, client, status, departmentType }
 */
function getPopupEvent() {
    const [sYear, sMonth, sDay] = $("#startDate").val().split('-');
    const [eYear, eMonth, eDay] = $("#endDate").val().split('-');
    const [sHour, sMin] = $("#startTime").val().split(':');
    const [eHour, eMin] = $("#endTime").val().split(':');

    let bookingType = $("#booking-selected").text();
    let department = $("#department-selected").text();
    let eventType = $("#type-selected").text();
    let status = $("#status-selected").text();

    bookingType = (bookingType === "Choose a type" || bookingType === "") ? null : bookingType.trim();
    department = (department === "Choose a department" || department === "") ? null : department.trim();
    eventType = (eventType === "Choose a type" || eventType === "") ? null : eventType.trim();
    status = (status === "") ? null : status.trim();

    return {
        name: $("#event-name").val(),
        location: $("#event-location").val(),
        startDate: new Date(+sYear, +sMonth - 1, +sDay, sHour, sMin),
        endDate: new Date(+eYear, +eMonth - 1, +eDay, eHour, eMin),
        duration: parseInt($("#event-duration").val()),
        notes: $("#event-notes").val(),
        status: status,
        bookingType: bookingType,
        departmentType: department,
        eventType: eventType,
        client: {
            id: userId,
            fullname: null,
            company: null,
            phone: $("#phone").val()
        }
    }
}

/**
 * Opends an event info when you click on it either in the calendar or the event board.
 * Also, 'edit' and 'delete' buttons are added.
 *
 * @param event event to be displayed.
 */
function openEventInfo(event) {
    fillBookingPopup(event);
    changeBookingOptions(bookingMode);
    toggleBookingPopup();

    let editBtn = $("#edit-btn");
    let deleteBtn = $("#delete-btn");
    editBtn.click(() => editEvent(event["id"]));
    deleteBtn.click(() => deleteEvent(event["id"]));
}

/**
 * Sends a delete request to the server to remove an event by id. If success, it is also removed from the
 * calendar and the event board.
 *
 * @param id identifier of the deleted event
 */
function deleteEvent(id) {
    $.ajax({
        type: "DELETE",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/all/" + id,
        success: () => location.reload(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Sends a request to the server to book a new event. If success, the page is reloaded to
 * apply the changes on the web page.
 */
function bookEvent() {
    let event = getPopupEvent();
    event["status"] = null;
    $.ajax({
        type: "POST",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/all",
        data: JSON.stringify(event),
        contentType: "application/json",
        success: () => location.reload(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Sends an edit request to the server to change event details by id. If success, the page is reloaded to
 * apply changes on the web page.
 *
 * @param id identifier of the edited event
 */
 function editEvent(id) {
    let event = getPopupEvent();
    event["client"] = events[id]["client"];
    event["id"] = id;
    $.ajax({
        type: "PUT",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/events/all",
        data: JSON.stringify(event),
        contentType: "application/json",
        success: () => location.reload(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Converts a date object to a displayable string by taking hours and minutes.
 *
 * @param date javascript date object
 * @returns {*} a displayable string
 */
function toTime(date) {
    return date.toTimeString().split(' ')[0];
}

/**
 * Converts a date object to a displayable string by taking a year, a month and a day.
 *
 * @param date javascript date object
 * @returns {*} a displayable string
 */
function toDate(date) {
    return date.toISOString().split('T')[0];
}