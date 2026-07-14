-- Nexus schema (SQLite). All tables use soft-delete via `deleted` column (0=valid,1=deleted).
-- VariableInfo has no `deleted` column (hard delete) to match original GORM model.

CREATE TABLE IF NOT EXISTS t_user (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  user_name TEXT,
  password TEXT
);

CREATE TABLE IF NOT EXISTS t_suite (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  suite_code TEXT,
  suite_name TEXT,
  suite_classify_id INTEGER,
  suite_image TEXT,
  suite_version TEXT,
  suite_desc TEXT,
  suite_help_doc_json TEXT,
  suite_flag INTEGER
);

CREATE TABLE IF NOT EXISTS t_api (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  suite_id INTEGER,
  api_code TEXT,
  api_protocol TEXT,
  api_url TEXT,
  api_name TEXT,
  api_desc TEXT,
  api_request_type TEXT,
  api_request_content_type TEXT
);

CREATE TABLE IF NOT EXISTS t_object (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  object_key TEXT,
  object_name TEXT,
  object_desc TEXT
);

CREATE TABLE IF NOT EXISTS t_parameter (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  param_type INTEGER,
  param_key TEXT,
  param_name TEXT,
  param_position TEXT,
  param_desc TEXT,
  data_type TEXT,
  required INTEGER,
  source_type TEXT,
  source_id INTEGER
);

CREATE TABLE IF NOT EXISTS t_flow_definition (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  flow_key TEXT,
  flow_name TEXT,
  flow_type TEXT,
  flow_content TEXT,
  remark TEXT
);

CREATE TABLE IF NOT EXISTS t_flow_info (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  flow_key TEXT,
  flow_name TEXT,
  flow_type TEXT,
  remark TEXT
);

CREATE TABLE IF NOT EXISTS t_flow_version (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  flow_id INTEGER,
  flow_version TEXT,
  flow_version_status INTEGER DEFAULT 0,
  flow_version_remark TEXT,
  flow_content TEXT,
  inputs TEXT,
  outputs TEXT,
  variables TEXT
);

CREATE TABLE IF NOT EXISTS t_variable_info (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  flow_definition_id INTEGER,
  variable_key TEXT,
  variable_name TEXT,
  variable_type INTEGER,
  data_type TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER
);

CREATE TABLE IF NOT EXISTS t_token (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  token_value TEXT,
  token_desc TEXT
);

CREATE TABLE IF NOT EXISTS t_data_source (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  data_source_name TEXT,
  data_source_type TEXT,
  data_source_desc TEXT,
  address TEXT,
  port TEXT,
  user_name TEXT,
  password TEXT,
  database_name TEXT,
  connect_ext_info TEXT,
  min_pool_size INTEGER,
  max_pool_size INTEGER,
  query_timeout INTEGER,
  data_source_ext_info TEXT
);

CREATE TABLE IF NOT EXISTS t_flow_template (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  deleted INTEGER DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by INTEGER,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_by INTEGER,
  template_name TEXT,
  template_remark TEXT,
  template_content TEXT,
  flow_type TEXT,
  inputs TEXT,
  outputs TEXT,
  variables TEXT
);
