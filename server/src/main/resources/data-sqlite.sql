-- seed default user (id=1, nexus / md5(nexus123)). Idempotent.
INSERT INTO t_user (id, deleted, user_name, password)
SELECT 1, 0, 'nexus', 'f2821100ff0b8a7cf4dd23073d67586a'
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE id = 1);
