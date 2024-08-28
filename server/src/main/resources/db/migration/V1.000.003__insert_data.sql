INSERT INTO addresses VALUES (1, 'Straße', '1a', 'Stadt', 'NRW', 'DE', 12345, '2024-01-01 00:00:00', NULL);

INSERT INTO users VALUES (
                             1, 'MALE', 'test', 'test', 'admin@test.de',
                             '',
                             '+491629276912',
                             true, true, true, true, true,1, false,  '', false,
                             '2024-01-01 00:00:00', 'admin');
INSERT INTO users VALUES (
                             2, 'MALE', 'test', 'test', 'teacher@test.de',
                             '',
                             '',
                             true, true, true, true, true,1, false,  '', false,
                             '2024-01-01 00:00:00', 'admin');
INSERT INTO users VALUES (
                             3, 'MALE', 'test', 'test', 'student@test.de',
                             '',
                             '',
                             true, true, true, true, true,1, false,  '', false,
                             '2024-01-01 00:00:00', 'admin');
INSERT INTO teachers VALUES (2,'','', 14.50,'12345678',16);
INSERT INTO students VALUES (3,'5b',true,null);


INSERT INTO permissions VALUES (1, 'Permission to fetch users ( without admin)', 'users:read', true);
INSERT INTO permissions VALUES (2, 'Permission to fetch users ( without admin)', 'users:write', true);
INSERT INTO permissions VALUES (3, 'Permission to fetch users ( without admin)', 'users:delete', true);
INSERT INTO permissions VALUES (4, 'Permission to read teachers', 'teachers:write', true);
INSERT INTO permissions VALUES (5, 'Permission to modify teachers', 'teachers:read', true);
INSERT INTO permissions VALUES (6, 'Permission to modify teachers', 'teachers:delete', true);
INSERT INTO permissions VALUES (7, 'Permission to read students', 'students:read', true);
INSERT INTO permissions VALUES (8, 'Permission to modify students', 'students:write', true);
INSERT INTO permissions VALUES (9, 'Permission to modify students', 'students:delete', true);

INSERT INTO roles VALUES (1, '', 'ADMIN');
INSERT INTO roles VALUES (2, '', 'TEACHER');
INSERT INTO roles VALUES (3, '', 'STUDENT');
INSERT INTO roles VALUES (4, '', 'PARENT');
INSERT INTO roles VALUES (5, '', 'SUPERVISOR');
INSERT INTO roles VALUES (6, '', 'SUPERADMIN');

INSERT INTO permission_role VALUES (1, 1);
INSERT INTO permission_role VALUES (1, 2);
INSERT INTO permission_role VALUES (2, 4);
INSERT INTO permission_role VALUES (2, 3);
INSERT INTO permission_role VALUES (3, 5);
INSERT INTO permission_role VALUES (4, 5);
INSERT INTO permission_role VALUES (5, 1);
INSERT INTO permission_role VALUES (1, 6);
INSERT INTO permission_role VALUES (2, 6);


INSERT INTO user_role VALUES (1, 6);
INSERT INTO user_role VALUES (2, 2);
INSERT INTO user_role VALUES (3, 3);


SELECT setval('users_id_seq', max(id)) FROM users;
SELECT setval('addresses_id_seq', max(id)) FROM addresses;
SELECT setval('roles_id_seq', max(id)) FROM roles;
SELECT setval('permissions_id_seq', max(id)) FROM permissions;