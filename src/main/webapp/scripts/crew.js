let members = [];

$(document).ready(() => {
    loadMembers();
    // addTestMembers();
});

/**
 * Loads members from either the local memory or from the server and fills them to the list.
 */
function loadMembers() {
    if (members.length !== 0) {
        members.forEach(i => addMember(i));
        return;
    }
    $.ajax({
        type: "GET",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/crew/all",
        success: (data) => {
            members = data;
            members.forEach(i => addMember(i));
        },
        error: (e) => alert(e.responseText)
    });
}

/**
 * Updates a crew member by id each time a change is made to one of the dropdown menus.
 *
 * @param id identifier of the member that is being updated
 */
function updateMember(id) {
    let member = $("#member-" + id);
    let department = member.find("[name='department']").find(".selected").text();

    let roles = [];
    member.find("[name='roles'] > div").each((i, el) => {
        roles.push($(el).find(".selected").text());
    });

    $.ajax({
        type: "PUT",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/crew/all",
        data: JSON.stringify({
            "id": id,
            "department": department,
            "roles": roles,
        }),
        contentType: "application/json",
        success: () => console.log("Updated member: " + id),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Deletes a crew member from the list.
 *
 * @param id identifier of the deleted member
 */
function fireMember(id) {
    $.ajax({
        type: "DELETE",
        url: "http://shotmaniacs4.paas.hosted-by-previder.com/rest/crew/all/" + id,
        success: () => $("#member-" + id).remove(),
        error: (e) => alert(e.responseText)
    });
}

/**
 * Adds a crew member to the list using the following format:
 * Member = { id, fullname, department, roles }
 *
 * @param member crew member that is being added
 */
function addMember(member) {
    let id = member["id"];
    $("header").after(`
        <div class="member" id="member-${id}">
            <img class="profile-pic" src="../images/penguin.png" alt="W3Schools.com">
            <p>Name:</p>
            <p class="profile-name">${member["fullname"]}</p>

            <!-- Dropdown menu for department options -->
            <div class="dropdown-wrapper">
                <p>Department:</p>
                <div class="select-box" name="department">
                    <div class="options-wrapper">
                        <div class="option">
                            <input type="radio" class="radio" id="event-photography-${id}" name="depart">
                            <label class="depart-label" for="event-photography-${id}">Event Photography</label>
                        </div>
                        <div class="option">
                            <input type="radio" class="radio" id="event-filmmaking-${id}" name="depart">
                            <label class="depart-label" for="event-filmmaking-${id}">Event Filmmaking</label>
                        </div>
                        <div class="option">
                            <input type="radio" class="radio" id="club-photography-${id}" name="depart">
                            <label class="depart-label" for="club-photography-${id}">Club Photography</label>
                        </div>
                        <div class="option">
                            <input type="radio" class="radio" id="marketing-${id}" name="depart">
                            <label class="depart-label" for="marketing-${id}">Marketing</label>
                        </div>
                    </div>
                    <div class="selected">${member["department"]}</div>
                </div>
            </div>

            <!-- Dropdown menu for role options -->
            <div class="dropdown-wrapper">
                <p>Roles:</p>
                <div class="roles" name="roles"></div>
                <button class="plus-btn" onclick="addRole(${id}); updateMember(${id});"></button>
            </div>

            <button type="button" class="btn cancel" onclick="openConfirmationWindow(${id})">Remove</button>
        </div>
    `);

    toggleDropdown(id, $("#member-" + id).find("[name='department']"));
    member["roles"].forEach(role => addRole(id, role), 1);
}

/**
 * Adds a new project role to a crew member.
 *
 * @param id identifier of the member panel to which a role should be added
 * @param val value of the role (default is "Photographer")
 */
function addRole(id, val) {
    val = (val == null) ? "Photographer" : val;
    let member = $("#member-" + id);
    let roles = member.find("[name='roles']");
    let index = roles.children().length;
    let roleId = id + '-' + index;

    if (index > 1) {
        member.find(".plus-btn").remove();
    }

    roles.append(`
        <div class="select-box">
            <div class="options-wrapper">
                <div class="option">
                    <input type="radio" class="radio" id="photographer-${roleId}" name="department">
                    <label class="roles-label" for="photographer-${roleId}">Photographer</label>
                </div>
                <div class="option">
                    <input type="radio" class="radio" id="videomaker-${roleId}" name="department">
                    <label class="roles-label" for="videomaker-${roleId}">Filmmaker</label>
                </div>
                <div class="option">
                    <input type="radio" class="radio" id="production-${roleId}" name="department">
                    <label class="roles-label" for="production-${roleId}">Production</label>
                </div>
            </div>
            <p class="selected">${val}</p>
        </div>
    `);

    toggleDropdown(id, roles.children().eq(index), index);
}

/**
 * Opens/closes a dropdown menu at a given Z index.
 *
 * @param id identifier of the member panel
 * @param menu given dropdown menu
 * @param zIndex order at which a menu should be displayed
 *              (in case multiple menus hover each other)
 */
function toggleDropdown(id, menu, zIndex) {
    let selected = menu.find(".selected");
    let options = menu.find(".options-wrapper");
    options.css("z-index", 100 - zIndex);
    selected.click(() => options.toggleClass("active"));

    options.find(".option").click((event) => {
        let option = event.target.innerText;
        if (option === "") return;
        selected.text(event.target.innerText);
        options.removeClass("active");
        updateMember(id);
    });
}

/**
 * Adds members to the list for testing.
 */
function addTestMembers() {
    addMember({
        id: 666,
        fullname: "Test Member",
        roles: ["Photographer", "Video Maker"],
        department: "EVENT_PHOTOGRAPHY"
    });

    addMember({
        id: 667,
        fullname: "Test Member",
        roles: ["Video Maker"],
        department: "CLUB_PHOTOGRAPHY"
    });
}

/* Confirmation window */
const Confirm = {
    open(options) {
        options = Object.assign({}, {
            title: '',
            message: '',
            openId: '',
            okText: 'Confirm',
            cancelText: 'Cancel',
            onok: function () {
            },
            oncancel: function () {
            }
        }, options);

        const html = `
            <div class="confirm">
                <div class="confirm__window">
                    <div class="confirm__titlebar">
                        <span class="confirm__title">${options.title}</span>
                        <button class="confirm__close">&times;</button>
                    </div>
                    <div class="confirm__content">${options.message}</div>
                    <div class="confirm__buttons">
                        <button class="confirm__button confirm__button--ok confirm__button--fill">${options.okText}</button>
                        <button class="confirm__button confirm__button--cancel">${options.cancelText}</button>
                    </div>
                </div>
            </div>
        `;

        const template = document.createElement('template');
        template.innerHTML = html;

        // Elements
        const confirmEl = template.content.querySelector('.confirm');
        const btnClose = template.content.querySelector('.confirm__close');
        const btnOk = template.content.querySelector('.confirm__button--ok');
        const btnCancel = template.content.querySelector('.confirm__button--cancel');

        confirmEl.addEventListener('click', e => {
            if (e.target === confirmEl) {
                options.oncancel();
                this._close(confirmEl);
            }
        });

        btnOk.addEventListener('click', () => {
            options.onok();
            this._close(confirmEl);
        });

        [btnCancel, btnClose].forEach(el => {
            el.addEventListener('click', () => {
                options.oncancel();
                this._close(confirmEl);
            });
        });

        document.body.appendChild(template.content);
    },

    _close(confirmEl) {
        confirmEl.classList.add('confirm--close');

        confirmEl.addEventListener('animationend', () => {
            document.body.removeChild(confirmEl);
        });
    }
};

function openConfirmationWindow(id) {
    Confirm.open({
        title: 'Member removal',
        message: 'Are you sure you wish to remove the member?',
        openId: id,
        onok: () => {
            fireMember(id);
        }
    })
}