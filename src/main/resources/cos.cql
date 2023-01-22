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
    PRIMARY KEY(user_id, auction_id, id)
    );

    INSERT INTO user_by_id(id,name) VALUES (1, 'Hubert');
    UPDATE user_by_id_with_balance SET balance = balance + 500000 WHERE id = 1;
    UPDATE user_by_id_with_debt SET debt = debt + 1000 WHERE id = 1;
   INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (1,1,500,dateof(now()), 10,50);


