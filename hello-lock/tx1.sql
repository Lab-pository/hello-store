use hello_store;

show tables;

insert into product(product_id, name, price)
values (1, '핸드폰', '1000000');

insert into stock(stock_id, product_id, version, quantity)
values (1, 1, 0, 10000);

start transaction;

select * from stock where stock_id = 1 for share ;

commit;


