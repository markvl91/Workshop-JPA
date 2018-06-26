CREATE TABLE audit_trail (
  id BIGINT NOT NULL AUTO_INCREMENT,
  account_id BIGINT NOT NULL,
  sale_id BIGINT,

  CONSTRAINT pk_audit_trail PRIMARY KEY(id)
);