 CREATE KEYSPACE aukcje
   WITH REPLICATION = {
                 'class' : 'SimpleStrategy',
                 'replication_factor' : 1
     };

    use keyspace aukcje;

    CREATE TABLE bid_order_by_item_id (
            item_id int,
            auction_id int,
            winning_user_id int,
            current_price bigint,
            max_number_of_participants int,
            participant_count int,
            bid_start_time date,
            bid_end_time date,
            min_bid_amount int,
            timestamp timestamp,
            PRIMARY KEY(item_id)
    );

    CREATE TABLE item_by_id (
    id int,
    item_name text,
    description text,
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
    PRIMARY KEY(user_id,auction_id)
    );