let announce = [];
let currentUrgency;

/**
 * Loads announcements from the server/local memory and adds them to the list.
 * Also, the default urgency is set to 1 (very urgent) when creating a new announcement,
 * and the text areas are set to grow automatically while typing.
 */
$(document).ready(() => {
    loadAnnouncements();
    setUrgency(1);
    autosize($("#ann-text"));
    // addTestAnnouncements();
});

/**
 * Retrieves data from the announcement panel and sends it to the server.
 * If sent successfully, the panel is closed and the announcement is stored locally.
 */
function sendAnnouncement() {
    let title = $("#ann-title").val();
    let body = $("#ann-text").val();

    // verify that the title is not empty
    if (title.length === 0) {
        alert("Title cannot be empty");
        return;
    }
    // create an announcement
    let announcement = {
        title: title,
        body: body,
        timestamp: new Date().getTime(),
        urgency: "LEVEL_" + currentUrgency,
    };
    // send the announcement
    $.ajax({
        type: "POST",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/announcements",
        data: JSON.stringify(announcement),
        contentType: "application/json",
        success: (id) => {
            announcement["id"] = id;
            announce.push(announcement);
            addAnnouncement(announcement);
            toggleAnnPanel();
        },
        error: (e) => alert(e.responseText)
    });
}

/**
 * Edits an announcement in the list by id.
 *
 * @param id identifier of the announcement to be updated.
 */
function editAnnouncement(id) {
    $.ajax({
        type: "PUT",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/announcements",
        data: JSON.stringify({
            id: id,
            title: $("#ann-title-" + id).val(),
            body: $("#ann-text-" + id).val()
        }),
        contentType: "application/json",
        error: (e) => alert(e.responseText)
    });
}

/**
 * Deletes an announcement from the list by id.
 *
 * @param id identifier of the announcement to be deleted.
 */
function deleteAnnouncement(id) {
    $.ajax({
        type: "DELETE",
        url: "hhttp://shotmaniacs4.paas.hosted-by-previder.com/rest/announcements/" + id,
        success: () => $("#ann-" + id).remove(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Loads announcements from either the local memory or the server and fills the list.
 */
function loadAnnouncements() {
    // try to load announcements from the local memory
    if (announce.length !== 0) {
        announce.forEach(i => addAnnouncement(i));
        return;
    }
    // otherwise load from the server
    $.ajax({
        type: "GET",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/announcements",
        success: (data) => {
            announce = data;
            announce.forEach(i => addAnnouncement(i));
        },
        error: (e) => alert(e.responseText)
    });
}

/**
 * Adds an announcement to the list of the following format:
 * Announcement = { id, title, body, urgency, department, receiverId }
 *
 * @param announcement announcement to be added
 */
function addAnnouncement(announcement) {
    let options = {year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric'}
    let timestamp = new Date(announcement["timestamp"]).toLocaleDateString("en-US", options);
    let urgency = announcement["urgency"].slice(-1);
    let id = announcement["id"];

    $("#ann-panel").after(`
        <div class="ann panel" id="ann-${id}">
            <div class="col">
                <div class="row ann-line-1">
                    <div class="col">
                        <label for="ann-title"></label>
                        <input type="text" value="${announcement["title"]}" name="title" id="ann-title-${id}">
                    </div>
                    <img class="ann-urgency" src="/images/urgency/norif-urgency-${urgency}.svg"
                        alt="Urgency Icon" style="margin-bottom: 0.5rem;">
                </div>
                <div class="col">
                    <label for="ann-text"></label>
                    <textarea name="value" id="ann-text-${id}">${announcement["body"]}</textarea>
                </div>
            </div>
            <div>
                <div class="col ann-options" style="align-items: end">
                    <h3>${timestamp}</h3>
                    <h3>Posted by admin</h3>
                    <div class="row ann-buttons">
                        <button class="btn" onclick="editAnnouncement(${id})">Edit</button>
                        <button class="btn cancel" onclick="deleteAnnouncement(${id})">Delete</button>
                    </div>
                </div>
            </div>
        </div>
    `);

    // autosize textarea
    autosize($("#ann-" + id).find("textarea"));
}

/**
 * Sets a listener to textareas while typing so that their height is automatically adjusted.
 *
 * @param textarea item that is being auto-sized
 */
function autosize(textarea) {
    textarea.each(function () {
        this.setAttribute("style", "height:"
            + this.scrollHeight + "px;overflow-y:hidden;");
    }).on("input", function () {
        this.style.height = "auto";
        this.style.height = (this.scrollHeight) + "px";
    });
}

/**
 * Opens/closes an announcement panel in case the announcement is being sent or cancelled.
 * If the panel is being closed, its values are reset.
 */
function toggleAnnPanel() {
    let panel = $("#ann-panel");
    let toClose = panel.css("display") === "flex";
    if (toClose) {
        $("#ann-title").val("");
        $("#ann-text").val("");
        setUrgency(1);
    }
    $(".add-ann-btn").css("display", toClose ? "" : "none");
    panel.css("display", toClose ? "none" : "flex");
}

/**
 * Sets the urgency of an announcement that can be sent by an admin.
 * Other urgencies are being reset.
 *
 * @param urgency updated urgency
 */
function setUrgency(urgency) {
    console.log("Urgency selected: " + urgency);
    $(".ann-urgency").css({"filter": "none"});
    $("#urgency-" + urgency).css({"filter": "invert(1) hue-rotate(180deg) brightness(2)"})
    currentUrgency = urgency;
}

/**
 * Loads test announcements to the list.
 */
function addTestAnnouncements() {
    addAnnouncement({
        id: 666,
        title: "Some title 1",
        body: "Lorem ipsum dolor sitLorem ipsum dolor sit amet consectetur adipisicing elit. NLorem ipsum dolor " +
            "sit amet consectetur adipisicing elit. NLorem ipsum dolor sit amet consectetur adipisicing elit. " +
            "NLorem ipsum dolor sit amet consectetur adipisicing elit. NLorem ipsum dolor sit amet consectetur " +
            "adipisicing elit. NLorem ipsum dolor sit amet consectetur adipisicing elit. NLorem ipsum dolor sit " +
            "amet consectetur adipisicing elit. NLorem ipsum dolor sit amet consectetur adipisicing elit. N amet " +
            "consectetur adipisicing elit. Nisi, alias dignissimos accusamus voluptate ducimus sint sapiente " +
            "doloremque, facilis tempora perspiciatis adipisci voluptatibus reprehenderit repudiandae porro cum? " +
            "Exercitationem, quasi vitae! Porro ex molestiae neque! Quaerat exercitationem, non corporis facilis " +
            "cum velit quo ducimus doloremque modi. Minima eos impedit corporis ipsa quibusdam?",
        timestamp: 0,
        urgency: "LEVEL_3",
        department: null,
        receiverId: 0,
    });

    addAnnouncement({
        id: 667,
        title: "Some title 2",
        body: "Lorem ipsum dolor sit amet consectetur adipisicing elit?",
        timestamp: 0,
        urgency: "LEVEL_2",
        department: null,
        receiverId: 0,
    });
}