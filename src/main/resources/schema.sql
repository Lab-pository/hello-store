drop table if exists product;
drop table if exists stock;
drop table if exists stock_v2;

create table if not exists product
(
    product_id bigint      not null,
    name       varchar(30) not null,
    price      bigint      not null,
    primary key (product_id)
);

create table if not exists stock
(
    stock_id   bigint not null,
    product_id bigint not null unique,
    version bigint not null,
    quantity   bigint not null check (quantity >= 0),
    primary key (stock_id)
);
