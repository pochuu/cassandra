 CREATE KEYSPACE aukcje
   WITH REPLICATION = {
                 'class' : 'SimpleStrategy',
                 'replication_factor' : 1
     };

    use aukcje;

    CREATE TABLE bid_order_by_auction_id (
            auction_id int,
            item_id int,
            winning_user_id int,
            current_price bigint,
            bid_end_time timestamp,
            min_bid_amount int,
            PRIMARY KEY(auction_id)
    );

    CREATE TABLE item_by_id (
    id int,
    item_name text,
    winner_id int,
    start_bid bigint,
    PRIMARY KEY(id)
    );

    CREATE TABLE user_by_id (
    id int,
    name text,
    PRIMARY KEY(id)
    );

    CREATE TABLE user_by_id_with_debt(
    id int,
    debt counter,
    PRIMARY KEY(id));

    CREATE TABLE user_by_id_with_balance(
    id int,
    balance counter,
    PRIMARY KEY(id));

    CREATE TABLE bid_history(
    user_id int,
    auction_id int,
    id uuid,
    amount bigint,
    timestamp timestamp,
    PRIMARY KEY(user_id, auction_id, id)
    );

    INSERT INTO user_by_id(id,name) VALUES (1, 'Hubert');
    INSERT INTO user_by_id(id,name) VALUES (2, 'Jonasz');
    INSERT INTO user_by_id(id,name) VALUES (3, 'Maciej');
    UPDATE user_by_id_with_balance SET balance = balance + 100000 WHERE id = 1;
    UPDATE user_by_id_with_balance SET balance = balance + 100000 WHERE id = 2;
    UPDATE user_by_id_with_balance SET balance = balance + 100000 WHERE id = 3;
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (1,1,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (2,2,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (3,3,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (4,4,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (5,5,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (6,6,0,dateof(now()), 10,50);
select * from bid_order_by_auction_id;




TRUNCATE user_by_id_with_debt;
TRUNCATE user_by_id_with_balance;
TRUNCATE bid_order_by_auction_id;
TRUNCATE bid_history;
UPDATE user_by_id_with_debt SET debt = debt + 0 WHERE id = 1;
UPDATE user_by_id_with_debt SET debt = debt + 0 WHERE id = 2;
UPDATE user_by_id_with_debt SET debt = debt + 0 WHERE id = 3;
UPDATE user_by_id_with_balance SET balance = balance + 10 WHERE id = 1;
UPDATE user_by_id_with_balance SET balance = balance + 10 WHERE id = 2;
UPDATE user_by_id_with_balance SET balance = balance + 10 WHERE id = 3;
insert into bid_order_by_auction_id (auction_id, item_id, current_price, bid_end_time, min_bid_amount, winning_user_id) values (1, 1, 0, dateof(now()), 5, -1);

select * from user_by_id_with_debt;
select * from user_by_id_with_balance;
select * from bid_order_by_auction_id;
