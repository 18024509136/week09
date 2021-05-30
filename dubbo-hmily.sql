-- 建立hmily框架需要的基础表，还没找到官方提供的sql，目前是看源码反推出来的，不准确，只能保证框架正确运行不报错
create table hmily_transaction_global (
	trans_id bigint not null,
	app_name varchar(100),
	status int,
	trans_type varchar(100),
	retry int,
	version int,
	create_time datetime,
	update_time datetime,
	PRIMARY KEY ( trans_id ) 
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;
			
create table hmily_transaction_participant (
	participant_id bigint not null,
	participant_ref_id bigint,
	trans_id bigint not null,
	trans_type varchar(100),
	status int,
	app_name varchar(100),
	role int,
	retry int,
	target_class varchar(500),
	target_method varchar(500),
	confirm_method varchar(500),
	cancel_method varchar(500),
	confirm_invocation longblob,
	cancel_invocation longblob,
	version int,
	create_time datetime,
	update_time datetime,
	primary key (participant_id) 
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 建立业务相关表库
CREATE DATABASE dubbo_hmily_test1 CHARACTER SET utf8mb4 ;

USE dubbo_hmily_test1;

CREATE TABLE account_cny
(
    id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
    user_id BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
    fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '资金',
	version INT NOT NULL DEFAULT 0 COMMENT '版本',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	KEY key_user_id (user_id)
)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE account_usd
(
    id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
    user_id BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
    fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '资金',
	version INT NOT NULL DEFAULT 0 COMMENT '版本',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	KEY key_user_id (user_id)
)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE frozen_fund_cny
(
	tx_id BIGINT NOT NULL DEFAULT 0 COMMENT '交易ID，主键',
	account_id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
	frozen_fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '冻结资金',
	TYPE TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1.待转入 2.待转出',
	STATUS TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0.解冻 1.冻结',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (tx_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE frozen_fund_usd
(
	tx_id BIGINT NOT NULL DEFAULT 0 COMMENT '交易ID，主键',
	account_id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
	frozen_fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '冻结资金',
	TYPE TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1.待转入 2.待转出',
	STATUS TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0.解冻 1.冻结',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (tx_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;



CREATE DATABASE dubbo_hmily_test2 CHARACTER SET utf8mb4 ;

USE dubbo_hmily_test2;

CREATE TABLE account_cny
(
    id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
    user_id BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
    fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '资金',
	version INT NOT NULL DEFAULT 0 COMMENT '版本',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	KEY key_user_id (user_id)
)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE account_usd
(
    id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
    user_id BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
    fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '资金',
	version INT NOT NULL DEFAULT 0 COMMENT '版本',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	KEY key_user_id (user_id)
)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE frozen_fund_cny
(
	tx_id BIGINT NOT NULL DEFAULT 0 COMMENT '交易ID，主键',
	account_id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
	frozen_fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '冻结资金',
	TYPE TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1.待转入 2.待转出',
	STATUS TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0.解冻 1.冻结',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (tx_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE frozen_fund_usd
(
	tx_id BIGINT NOT NULL DEFAULT 0 COMMENT '交易ID，主键',
	account_id BIGINT NOT NULL DEFAULT 0 COMMENT '账户ID',
	frozen_fund DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '冻结资金',
	TYPE TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1.待转入 2.待转出',
	STATUS TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0.解冻 1.冻结',
	create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (tx_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 测试时还原测试数据用
USE `dubbo_hmily_test1`;

TRUNCATE `account_cny`;
TRUNCATE `account_usd`;
TRUNCATE `frozen_fund_cny`;
TRUNCATE `frozen_fund_usd`;

INSERT INTO `account_cny` (id, user_id, fund, VERSION) VALUES(1,1,100,0);
INSERT INTO `account_usd` (id, user_id, fund, VERSION) VALUES(11,1,100,0);

USE `dubbo_hmily_test2`;

TRUNCATE `account_cny`;
TRUNCATE `account_usd`;
TRUNCATE `frozen_fund_cny`;
TRUNCATE `frozen_fund_usd`;

INSERT INTO `account_cny` (id, user_id, fund, VERSION) VALUES(2,2,100,0);
INSERT INTO `account_usd` (id, user_id, fund, VERSION) VALUES(22,2,100,0);