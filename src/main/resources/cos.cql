 CREATE KEYSPACE aukcje
   WITH REPLICATION = {
                 'class' : 'SimpleStrategy',
                 'replication_factor' : 1
     };

    use aukcje;

    CREATE TABLE bid_order_by_item_id (
            item_id int,
            auction_id int,
            winning_user_id int,
            current_price bigint,
            max_number_of_participants int,
            participant_count int,
            bid_end_time timestamp,
            min_bid_amount int,
            PRIMARY KEY(item_id)
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
    balance bigint,
    PRIMARY KEY(id)
    );

    CREATE TABLE bid_history(
    user_id int,
    auction_id int,
    amount bigint,
    PRIMARY KEY((user_id,auction_id))
    );

    INSERT INTO user_by_id(id,name,balance) VALUES (1, 'Hubert', 9223372036854775807);
    INSERT INTO user_by_id(id,name,balance) VALUES (2, 'Jonasz', 9223372036854775807);
    INSERT INTO user_by_id(id,name,balance) VALUES (3, 'Maciej', 9223372036854775807);

    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Obraz', 100);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Dom', 3000);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Mieszkanie', 2000);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Auto', 500);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Telewizor', 200);

    INSERT INTO bid_order_by_item_id(item_id, auction_id, current_price, max_number_of_participants, bid_end_time, min_bid_amount)
    VALUES (1, 1, 100, 5, dateof(now()), 10);
    INSERT INTO bid_order_by_item_id(item_id, auction_id, current_price, max_number_of_participants, bid_end_time, min_bid_amount)
    VALUES (2, 2, 3000, 5, dateof(now()), 50);
    INSERT INTO bid_order_by_item_id(item_id, auction_id, current_price, max_number_of_participants, bid_end_time, min_bid_amount)
    VALUES (3, 3, 2000, 5, dateof(now()), 100);
    INSERT INTO bid_order_by_item_id(item_id, auction_id, current_price, max_number_of_participants, bid_end_time, min_bid_amount)
    VALUES (4, 4, 500, 5, dateof(now()), 55);
    INSERT INTO bid_order_by_item_id(item_id, auction_id, current_price, max_number_of_participants, bid_end_time, min_bid_amount)
    VALUES (5, 5, 200, 5, dateof(now()), 40);