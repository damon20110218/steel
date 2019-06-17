DROP TABLE client_info;
CREATE TABLE client_info (client_id int NOT NULL, client_name varchar(400), create_date date, client_type varchar(45), client_mark varchar(45), PRIMARY KEY (client_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='客户信息表';
DROP TABLE steel_category;
CREATE TABLE steel_category (category_id int NOT NULL, steel_name varchar(200), steel_en_name varchar(200), steel_code varchar(45), length int, width int, PRIMARY KEY (category_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板种类表，维护钢板名称，长度与宽度';
DROP TABLE steel_order;
CREATE TABLE steel_order (order_id int NOT NULL, order_date date, order_no varchar(45), client_id int, client_no varchar(45), category_id int, client_spec varchar(100), client_amount int, price double(10,4), steel_calc_amount varchar(100), comment varchar(400), is_out int(2), is_sale int(2), is_delete int(2), PRIMARY KEY (order_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板订单表';
DROP TABLE steel_parameter;
CREATE TABLE steel_parameter (param_id int NOT NULL, density double(10,4), category_id int, PRIMARY KEY (param_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板基本信息表，维护密度等信息';
DROP TABLE steel_price;
CREATE TABLE steel_price (price_id int NOT NULL AUTO_INCREMENT, spec_id int, price double(10,4), price_date date, price_type int(2) COMMENT '1-期货价格 2-零售价格', PRIMARY KEY (price_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板每日价格表';
DROP TABLE steel_sale;
CREATE TABLE steel_sale (sale_id int NOT NULL, sale_no varchar(45), order_id int, PRIMARY KEY (sale_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板销售表';
DROP TABLE steel_specs;
CREATE TABLE steel_specs (spec_id int NOT NULL, thickness double(10,3), steel_code varchar(45), spec_desc varchar(300),  category_id int, PRIMARY KEY (spec_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板规格信息，主要维护钢板厚度';
DROP TABLE steel_storage;
CREATE TABLE steel_storage (storage_id int NOT NULL, store_date date, client_no varchar(100), store_no varchar(100), steel_amount double(10,4), steel_factory varchar(200), year int, month int, spec_id int, PRIMARY KEY (storage_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='工厂钢板入库信息表';
DROP TABLE storage;
CREATE TABLE storage (store_id bigint NOT NULL, PRIMARY KEY (store_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;
DROP TABLE user;
CREATE TABLE user (user_id int NOT NULL, user_name varchar(45), password varchar(45), role varchar(45), PRIMARY KEY (user_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;
