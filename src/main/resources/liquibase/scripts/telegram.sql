-- liquibase formatted sql

-- changeset kadyrovas:1
create table notification_task (
       id bigserial primary key,
       chat_id bigint,
       date_time timestamp,
       task_text text
);