# cassandra

## to run application in docker

### start the docker compose first 
```bash
docker compose up -d 
```


### build Containerfile
```bash
docker build -f Containerfile -t projekt .
```
If you are able to connect to cassandra shell
```bash
docker exec -ti cassandra cqlsh -u cassandra -p cassandra
```
Initialize the script
```cqlsh
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
```
### run users
```bash
docker run --net cassandra_kasia --init --ip 172.20.0.4 -e user_id=1 projekt
docker run --net cassandra_kasia --init --ip 172.20.0.7 -e user_id=2 projekt
docker run --net cassandra_kasia --init --ip 172.20.0.6 -e user_id=3 projekt
```

When everything is running spawn some auctions so the users can bid them:
```cqlsh

INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (1,1,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (2,2,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (3,3,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (4,4,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (5,5,0,dateof(now()), 10,50);
INSERT INTO bid_order_by_auction_id (auction_id , item_id , current_price,bid_end_time,min_bid_amount, winning_user_id ) VALUES (6,6,0,dateof(now()), 10,50);
```
