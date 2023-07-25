delete from roles;
delete from users;
delete from users_roles;
drop sequence if exists hibernate_sequence;

INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_USER'), (3, 'ROLE_GUEST');
INSERT INTO users (id, email, enabled, name, last_name, password) VALUES (1, 'admin@its.com', true, 'Василий', 'Уткин', '$2y$10$kbBc2/YyhalAHuK.SRiFPeu/iENCtVjUS9sK4A3/4b5EXdgqcj0cy');
INSERT INTO users_roles VALUES (1, 1);
create sequence if not exists hibernate_sequence start with 2;