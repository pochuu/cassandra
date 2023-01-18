package org.example.backend.auction;

import lombok.Builder;

import java.util.Date;

@Builder
public class Auction {
    //item_id, auction_id, current_price, max_number_of_participants, bid_end_time, min_bid_amount
    int itemId;
    int auctionId;
    long currentPrice;
    Date bidEndTime;
    int minBidAmount;

    public Object[] getFields() {
        return new Object[]{itemId, auctionId, currentPrice, bidEndTime, minBidAmount};
    }
}
