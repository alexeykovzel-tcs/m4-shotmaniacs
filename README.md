# Shotmaniacs project

This web portal was created for the Shotmaniacs company to make the management of events easier. The company provides photographers, videomakers, and marketing managers for the events.


## Roles

Depending on your role, you can access different pages.

- **Client**: the main page, make booking, client-events, profile
 
 - **Crew member**: main page, dashboard(calendar) with all events, view announcements, profile
 
 - **Client**: main page, dashboard(calendar) with all events, write and view announcements, profile, manage crew and approve or decline new events.

## Usage

If you want to access pages of certain people, you need to follow the instructions:

- For the client - you need to create an account on the main page

When you have created an account, you will be directed to the client-events page where you can view all events that you have created. Moreover, you can edit existing events and add more.

**important** - If you want to upload several events, you can check the example of the XLSX file in /test/resources/Test_Events.xlsx

- For crew - *login:* crew1@gmail.com or crew2@gmail.com and *password:* 123

When you have successfully passed the login, you will be redirected to the crew dashboard. On this page, you can view all events assigned to you and details about each booking. Furthermore, you can receive announcements from the admin.  

- For admin - *login:* admin@gmail.com and *password:* 123

When you have successfully passed the login, you will be redirected to the admin dashboard. On this page, you can view all events and details for them for all departments. Furthermore, you can switch to other tabs - Announcements, Incoming events, and Crew members.  


When a file with multiple events is read, the first row with column titles is ignored.

## All possible paths

```bash
            // Admin tabs
            "/admin/announcements" - Create and view announcements from admin side
            "/admin/crew-list" - View and edit information about crew members
            "/admin/dashboard" - Calendar with all events and information about them
            "/admin/incoming-events" - Approve or decline new events 

            // Client tabs
            "/contacts" - Contact details of the company
            "/faq" - Page with frequently ask questions
            "/privacy" - Privacy Policy of the company
           "/tos" - Terms of Use of the company

            // Crew tabs
            "/crew/dashboard" - Calendar with all events assigned to you or with events to which you can apply

            // auth
            "/login" - login page
            "/signup" - signup page
            "/forgot-password" - forgot password page

            // other
            "/" - Main page or Landing page with information about the company
            "/profile" - Profile of the user, you can edit info, delete the account, and sign out

If you cannot access some pages due to any reason, you can try to go there via a link with an HTML file. 
Here is a list of all possible HTML paths respectively:
            
             // admin
            "/admin/announcements" => "/pages/admin/announcements.html"
            "/admin/crew-list" => "/pages/admin/crew-list.html"
            "/admin/dashboard" => "/pages/admin/dashboard.html"
            "/admin/incoming-events" => "/pages/admin/incoming-events.html"
            "/admin/event-calender" => "/pages/admin/event-calender.html"

            // client
            "/contacts" => "/pages/client/contacts.html"
            "/faq" => "/pages/client/faq.html"
            "/privacy" => "/pages/client/privacy.html"
            "/tos" => "/pages/client/tos.html"

            // crew
           "/crew/dashboard" => "/pages/crew/dashboard.html"

            // auth
            "/login" => "/pages/auth.html"
            "/signup" => "/pages/auth.html"
            "/forgot-password" => "/pages/auth.html"

            // other
            "/" => "/pages/index.html"
            "/profile" => "/pages/profile.html"
```

## Instruction if something goes wrong

- **The main issue can be that you have https:// instead of http://**
- If the database does not work, go to /src/main/java/com/shotmaniacs/dao/Dao.java and insert your variables in  URL, USERNAME and PASSWORD. 
- If our database does not work, you can connect your database and run commands in files in /sql one by one (order is important!)
-  Session expires in one hour, thus you will be logged out and might lose access to some pages. The website will not tell you anything. A possible solution is to go back to "/" and log in again.


## Room for improvement
- Note for an event can be a maximum of 400 symbols due to database limitations. 
- Data from input fields are saved to the database properly, but sometimes it is not extracted from the database in a correct way.
- You can put the end date and time before the start date and time
- You can put letters in the phone number
- Event duration can be minus (while editing)
- On the profile page Home, Contacts and FAQ are visible (they should not)
- When a user presses Save on the Profile page, he should be redirected to client-events.html
- When you change the type of event to a club, there is a blue border
- If a page looks strange, then try to zoom out or zoom in
- Not all pages are adaptive for the mobile version
- Sometimes if you zoom out and zoom in, you may have a strange layout


## Libraries
- Bootstrap - to simplify the layout development
- Jakarta - for XML binding
- org.apache.poi - Processing XLSX files
- javax mail - to send emails
- JUnit - for Unit testing
- JQuery
