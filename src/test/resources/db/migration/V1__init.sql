create schema "my_test_org";

create table "my_test_org".comments
(
    id            bigint generated always as identity,
    org_id        character varying(24) default null,
    project_id    character varying(24)  not null,
    domain        character varying(24) default 'NONE',
    resource_id   character varying(24)  not null,
    is_deleted    boolean               default false,
    like_count    bigint                default 0,
    dislike_count bigint                default 0,
    data          json                   not null,
    parent_id     bigint                default null,
    created_at    timestamp             default current_timestamp,
    created_by_id character varying(255) not null,
    updated_at    timestamp             default current_timestamp,
    updated_by_id character varying(255) not null,

    primary key (id)
);

create table "my_test_org".comment_interactions
(
    id         bigint generated always as identity,
    comment_id bigint                 not null,
    type       character varying(24)  not null,
    user_id    character varying(255) not null,

    primary key (id),
    constraint comment_interactions_uq_1 unique (comment_id, user_id)
);

insert into "my_test_org".comments
    (org_id, project_id, "domain", resource_id, is_deleted, data, created_by_id, updated_by_id)
values
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', false, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', false, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', false, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', false, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', false, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', 'TESTER', 'TESTER');