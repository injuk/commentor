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

create index idx_1 on "my_test_org".comments (parent_id);
create index idx_2 on "my_test_org".comments (project_id, domain, resource_id);
create index idx_3 on "my_test_org".comments (created_at, id);
create index idx_4 on "my_test_org".comments (updated_at, id);

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
    (org_id, project_id, "domain", resource_id, is_deleted, data, parent_id, created_by_id, updated_by_id)
values
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'delete_target', false, '[]', NULL, 'SYSTEM', 'SYSTEM'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'update_target', false, '[]', NULL, 'SYSTEM', 'SYSTEM'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'parent_resource_id', false, '[]', NULL, 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'already_deleted', true, '[]', NULL, 'SYSTEM', 'SYSTEM'),

    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', false, '[]', NULL, 'SYSTEM', 'SYSTEM'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', false, '[]', NULL, 'SYSTEM', 'SYSTEM'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', NULL, 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', NULL, 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', NULL, 'TESTER', 'TESTER'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'migrated_resource_id', true, '[]', NULL, 'TESTER', 'TESTER'), -- 10
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'bulk_delete_target_1', false, '[]', NULL, 'SYSTEM1', 'SYSTEM1'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'bulk_delete_target_2', false, '[]', NULL, 'SYSTEM2', 'SYSTEM2'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'bulk_delete_target_3', false, '[]', NULL, 'SYSTEM3', 'SYSTEM3'),
    ('migrated_org_id', 'migrated_project_id', 'ARTICLE', 'bulk_delete_target_2', false, '[]', NULL, 'SYSTEM1', 'SYSTEM1'),

    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM3', 'SYSTEM3'), -- 20
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', NULL, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'), -- 30
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'), -- 40
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'), -- 50
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'), -- 60
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM3', 'SYSTEM3'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM1', 'SYSTEM1'),
    ('list_test_org', 'list_test_proj', 'VIDEO', 'list_test_res', false, '[]', 15, 'SYSTEM2', 'SYSTEM2');

insert into "my_test_org".comment_interactions
(comment_id, type, user_id)
values
    (20, 'LIKE', 'SYSTEM'),
    (20, 'LIKE', 'TESTER'),
    (21, 'LIKE', 'SYSTEM'),
    (21, 'LIKE', 'TESTER'),
    (22, 'LIKE', 'SYSTEM');

insert into "my_test_org".comments
(org_id, project_id, "domain", resource_id, is_deleted, data, parent_id, created_by_id, updated_by_id)
values
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'), -- 70
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'), -- 80
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'), -- 90
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'), -- 100
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),

    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 105, 'SYSTEM1', 'SYSTEM1'), -- 110
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 106, 'SYSTEM2', 'SYSTEM2'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 107, 'SYSTEM3', 'SYSTEM3'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 108, 'SYSTEM1', 'SYSTEM1'),
    ('list_sub_test_org', 'list_sub_test_proj', 'VIDEO', 'list_sub_test_res', true, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 109, 'SYSTEM2', 'SYSTEM2');

insert into "my_test_org".comment_interactions
(comment_id, type, user_id)
values
    (105, 'LIKE', 'SYSTEM'),
    (106, 'LIKE', 'SYSTEM'),
    (107, 'LIKE', 'TESTER'),
    (108, 'LIKE', 'SYSTEM'),
    (109, 'LIKE', 'TESTER');

insert into "my_test_org".comments
(org_id, project_id, "domain", resource_id, is_deleted, data, parent_id, created_by_id, updated_by_id)
values
    ('action_test_org', 'action_test_proj', 'VIDEO', 'action_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', null, 'SYSTEM1', 'SYSTEM1'), -- 115
    ('action_test_org', 'action_test_proj', 'VIDEO', 'action_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 50, 'SYSTEM1', 'SYSTEM1'),
    ('action_test_org', 'action_test_proj', 'VIDEO', 'action_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 51, 'SYSTEM1', 'SYSTEM1'),
    ('action_test_org', 'action_test_proj', 'VIDEO', 'action_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 52, 'SYSTEM1', 'SYSTEM1'),
    ('action_test_org', 'action_test_proj', 'VIDEO', 'action_test_res', false, '[{"type":"PARAGRAPH","attrs":{"level":1},"content":[{"type":"TEXT","text":"aaa","marks":[{"type":null,"attrs":{"type":null,"color":"blue"}}],"attrs":{"id":null,"text":null}}]}]', 53, 'SYSTEM1', 'SYSTEM1');