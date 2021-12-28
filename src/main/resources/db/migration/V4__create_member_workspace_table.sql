CREATE TABLE member_workspace
(
    member_id    UUID NOT NULL,
    workspace_id UUID NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members (id),
    FOREIGN KEY (workspace_id) REFERENCES workspaces (id)
);