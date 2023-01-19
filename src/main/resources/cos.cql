 CREATE KEYSPACE aukcje
   WITH REPLICATION = {
                 'class' : 'SimpleStrategy',
                 'replication_factor' : 1
     };

    use aukcje;

    CREATE TABLE bid_order_by_item_id (
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
    INSERT INTO bid_order_by_item_id (auction_id , item_id , current_price, winning_user_id  ) VALUES (1,1,500,1);



    INSERT INTO user_by_id(id,name) VALUES (2, 'Jonasz');
    INSERT INTO user_by_id(id,name) VALUES (3, 'Maciej');

    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Obraz', 100);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Dom', 3000);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Mieszkanie', 2000);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Auto', 500);
    INSERT INTO item_by_id(id, item_name, start_bid) VALUES (1, 'Telewizor', 200);


// TO NIZEJ WKLEIĆ BO WYSTARTOWANIU SERWERA

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


