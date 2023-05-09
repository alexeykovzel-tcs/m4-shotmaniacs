-- insert admins
INSERT INTO PERSON (id, fullname, email, password_hash, phone_no, server_role)
VALUES (9991, 'Test Admin', 'admin@gmail.com', 'pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', '+222', 'ADMIN');

-- insert clients
INSERT INTO PERSON (id, fullname, email, password_hash, phone_no, server_role)
VALUES (9992, 'Test Client', 'client@gmail.com', 'pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', '+222', 'CLIENT');
INSERT INTO CLIENT (id, company)
VALUES (9992, 'Test company 1');

-- insert crew members
INSERT INTO PERSON (id, fullname, email, password_hash, phone_no, server_role)
VALUES (9993, 'Test Crew 1', 'crew1@gmail.com', 'pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', '+222', 'CREW');
INSERT INTO CREW_MEMBER (id, availability, department_type)
VALUES (9993, 'AVAILABLE', 'EVENT_PHOTOGRAPHY');

INSERT INTO PERSON (id, fullname, email, password_hash, phone_no, server_role)
VALUES (9994, 'Test Crew 2', 'crew2@gmail.com', 'pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', '+222', 'CREW');
INSERT INTO CREW_MEMBER (id, availability, department_type)
VALUES (9994, 'AVAILABLE', 'EVENT_PHOTOGRAPHY');

INSERT INTO PERSON (id, fullname, email, password_hash, phone_no, server_role)
VALUES (9995, 'Test Crew 3', 'crew3@gmail.com', 'pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', '+222', 'CREW');
INSERT INTO CREW_MEMBER (id, availability, department_type)
VALUES (9995, 'AVAILABLE', 'EVENT_PHOTOGRAPHY');

-- insert announcements
INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9991, 'Some title', 'Donec arcu dui, lobortis non lacinia sit amet, scelerisque nec quam. Pellentesque vitae.', '2022-07-01 18:30:28', 'LEVEL_1', null, null, 9991);

INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9992, 'Some title', 'Donec arcu dui, lobortis non lacinia sit amet, scelerisque nec quam. Pellentesque vitae.', '2022-07-01 18:30:28', 'LEVEL_2', null, null, 9991);

INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9993, 'Announcement Title 1', 'Nullam non lorem turpis. Maecenas feugiat at dui non auctor. Suspendisse ut malesuada eros. Fusce eget venenatis magna, vitae pretium purus.', '2022-06-05 18:30:28', 'LEVEL_1', null, null, 9991);

INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9994, 'Announcement Title 2', 'Curabitur rhoncus vehicula dui eu fermentum.', '2022-06-10 18:30:28', 'LEVEL_2', null, null, 9991);

INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9995, 'Announcement Title 3', 'Suspendisse eget quam eu dui laoreet ultricies. Quisque aliquam id purus non sagittis. Proin in bibendum orci. Ut maximus enim et ante cursus, non bibendum eros sollicitudin.', '2022-06-21 18:30:28', 'LEVEL_3', null, null, 9991);

INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9996, 'Announcement Title 4', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', '2022-06-03 18:30:28', 'LEVEL_1', null, null, 9991);

INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9997, 'Announcement Title 5', 'Lorem ipsum dolor sit amet, consectetur adipiscing.', '2022-06-05 18:30:28', 'LEVEL_2', null, null, 9991);

INSERT INTO ANNOUNCEMENT (id, title, body, timestamp, urgency, department, receive, sender)
VALUES (9998, 'Announcement Title 6', 'Lorem ipsum dolor sit amet.', '2022-06-04 18:30:28', 'LEVEL_3', null, null, 9991);

-- insert events
INSERT INTO EVENT (id, title, location, start_date, end_date, duration, notes, client, booking, status, event_type)
VALUES (9991, 'Jo Jo Wedding', 'Some location', '2022-07-02 18:30:00', '2022-07-02 21:30:00',
        2, 'Some notes 1', 9992, null, 'TO_APPROVE', 'WEDDING');

INSERT INTO EVENT (id, title, location, start_date, end_date, duration, notes, client, booking, status, event_type)
VALUES (9992, 'SpaceBar Club', 'Some location', '2022-07-03 12:30:00', '2022-07-03 18:30:00',
        6, 'Some notes 2', 9992, null, 'TO_APPROVE', 'CLUB');

INSERT INTO EVENT (id, title, location, start_date, end_date, duration, notes, client, booking, status, event_type)
VALUES (9993, 'Video Shooting', 'Some location', '2022-07-05 11:30:00', '2022-07-06 18:30:00',
        5, 'Lorem ipsum dolor', 9992, null, 'TODO', 'COMMERCIAL');

INSERT INTO EVENT (id, title, location, start_date, end_date, duration, notes, client, booking, status, event_type)
VALUES (9994, 'Mario Party', 'Some location', '2022-07-08 09:30:00', '2022-07-08 18:30:00',
        2, 'Lorem ipsum dolor sit amet, consectetur adipiscing.', 9992, null, 'IN_PROGRESS', 'FESTIVAL');

INSERT INTO EVENT (id, title, location, start_date, end_date, duration, notes, client, booking, status, event_type)
VALUES (9995, 'Boss of the gym', 'Some location', '2022-07-04 18:30:00', '2022-07-04 19:45:00',
        1, 'Lorem ipsum dolor sit amet, consectetur.', 9992, null, 'IN_PROGRESS', 'BUSINESS');

INSERT INTO EVENT (id, title, location, start_date, end_date, duration, notes, client, booking, status, event_type)
VALUES (9996, 'Hello World!', 'Some location', '2022-07-02 09:30:00', '2022-07-02 16:30:00',
        2, '', 9992, null, 'TO_APPROVE', 'FESTIVAL');