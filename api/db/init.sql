create table users (
  id bigserial primary key not null,
  user_name varchar unique not null,
  encrypted_password varchar,
  role_id bigint,
  state varchar,
  created_at timestamp,
  updated_at timestamp
);
create table roles (
  id bigserial primary key not null,
  name varchar,
  permissions text,
  created_at timestamp,
  updated_at timestamp
);
create table menus (
  id bigserial primary key not null,
  parent_id bigint,
  key varchar,
  name varchar,
  url varchar
);
create table resources (
  id bigserial primary key not null,
  menu_id bigint,
  entity_name varchar,
  permission varchar
);
create table storages (
  id bigserial primary key not null,
  file_hash varchar unique not null,
  file_name varchar,
  file_size bigint,
  file_type varchar,
  original_file_name varchar,
  created_at timestamp
);

-- admin/123456
insert into users(id, user_name, encrypted_password, state, created_at, updated_at) values (1, 'admin', '$2a$10$apcHrdYrw.4mV6jUgVSX9ubLdnv6CyrM9Bp7a/eYoO4gOGak8ksJG', 'enable', now(), now());

insert into menus(id, parent_id, key, name, url) values (1, 0, 'sys', 'System', '');
insert into menus(id, parent_id, key, name, url) values (10, 1, 'user', 'User', '/users');
insert into menus(id, parent_id, key, name, url) values (11, 1, 'role', 'Role', '/roles');

insert into resources(id, menu_id, entity_name, permission) values (100, 10, 'User', 'read');
insert into resources(id, menu_id, entity_name, permission) values (101, 10, 'User', 'create');
insert into resources(id, menu_id, entity_name, permission) values (102, 10, 'User', 'update');
insert into resources(id, menu_id, entity_name, permission) values (103, 10, 'User', 'destroy');
insert into resources(id, menu_id, entity_name, permission) values (110, 11, 'Role', 'read');
insert into resources(id, menu_id, entity_name, permission) values (111, 11, 'Role', 'create');
insert into resources(id, menu_id, entity_name, permission) values (112, 11, 'Role', 'update');
insert into resources(id, menu_id, entity_name, permission) values (113, 11, 'Role', 'destroy');