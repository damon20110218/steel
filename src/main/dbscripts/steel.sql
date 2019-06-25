DROP TABLE client_info;
CREATE TABLE client_info (client_id int NOT NULL AUTO_INCREMENT, client_name varchar(400), create_date date, client_type varchar(8) COMMENT '1- 公司 2-客户', client_mark varchar(500), mobile varchar(100), telephone varchar(100), email varchar(100), address varchar(300), contact_person varchar(45), PRIMARY KEY (client_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='客户信息表';
DROP TABLE steel_category;
CREATE TABLE steel_category (category_id int NOT NULL, steel_name varchar(200), steel_en_name varchar(200), steel_code varchar(45), length int, width int, calc_type varchar(45), PRIMARY KEY (category_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板种类表，维护钢板名称，长度与宽度';
DROP TABLE steel_inventory;
CREATE TABLE steel_inventory (inventory_id int NOT NULL AUTO_INCREMENT, spec_id varchar(45), inventory_date varchar(45), store_in double(10,4), store_out double(10,4), year int, month int, PRIMARY KEY (inventory_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;
DROP TABLE steel_order;
CREATE TABLE steel_order (order_id int NOT NULL AUTO_INCREMENT, order_date date, order_no varchar(45), client_id int, client_no varchar(45), spec_id int, client_amount double(10,4), price double(10,4), steel_calc_amount varchar(100), comment varchar(400), is_out int(2) COMMENT '0-未出库  1-已出库', is_sale int(2) COMMENT '0-未销售  1-已销售', is_delete int(2), unit varchar(45), year int(4), month int(2), PRIMARY KEY (order_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板订单表';
DROP TABLE steel_outbound;
CREATE TABLE steel_outbound (out_id int NOT NULL AUTO_INCREMENT, order_no varchar(45), spec_id int, actual_amount double(10,4), out_date date, year int, month int, PRIMARY KEY (out_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;
DROP TABLE steel_parameter;
CREATE TABLE steel_parameter (param_id int NOT NULL, density double(10,4), category_id int, PRIMARY KEY (param_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板基本信息表，维护密度等信息';
DROP TABLE steel_price;
CREATE TABLE steel_price (price_id int NOT NULL AUTO_INCREMENT, price_code varchar(45), price double(10,4), price_date date, price_type int(2) COMMENT '1-期货价格 2-零售价格', PRIMARY KEY (price_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板每日价格表';
DROP TABLE steel_sale;
CREATE TABLE steel_sale (sale_id int NOT NULL AUTO_INCREMENT, sale_no varchar(45), order_no varchar(45), sale_date date, year int, month int, sale_amout double(10,4), cash_amount double(10,4), PRIMARY KEY (sale_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;
DROP TABLE steel_specs;
CREATE TABLE steel_specs (spec_id int NOT NULL, thickness double(10,3), steel_code varchar(45), spec_desc varchar(300), category_id int, price_code varchar(45), PRIMARY KEY (spec_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='钢板规格信息，主要维护钢板厚度';
DROP TABLE steel_storage;
CREATE TABLE steel_storage (storage_id int NOT NULL AUTO_INCREMENT, store_date date, client_no varchar(100), store_no varchar(100), steel_amount double(10,4), steel_factory varchar(200), year int, month int, spec_id int, client_id int, cash_amount double(10,2), price double(10,4), PRIMARY KEY (storage_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci COMMENT='工厂钢板入库信息表';
DROP TABLE user;
CREATE TABLE user (user_id int NOT NULL, user_name varchar(45), password varchar(45), role varchar(45), PRIMARY KEY (user_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_general_ci;
CREATE TABLE steel_future_price (
  price_id int(11) NOT NULL,
  price double(10,4) DEFAULT NULL,
  price_date date DEFAULT NULL,
  price_code varchar(45) DEFAULT NULL,
  PRIMARY KEY (price_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8