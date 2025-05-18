-- liquibase formatted sql

-- changeset kadyrovas:1
create table notification_task (
       task_id bigserial primary key,
       chat_id bigint,
       date_time timestamp,
       task_text text
);