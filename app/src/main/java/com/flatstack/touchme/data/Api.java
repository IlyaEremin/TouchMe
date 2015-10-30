package com.flatstack.touchme.data;

import com.flatstack.touchme.data.responses.ConnectionResponse;

import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Ilya Eremin on 10/30/15.
 */
public interface Api {

    String BASE_URL = "https://thetouch.herokuapp.com/";

    @GET("/connections") Observable<ConnectionResponse> checkConnection(
        @Query("receiver_id") String receiverPhone);

    @PATCH("/connections/{id}") Observable<Void> updateSenderCoordinates(
        @Path("id") long connectionId,
        @Query("sender_x") int senderX,
        @Query("sender_y") int senderY
    );

    @PATCH("/connections/{id}") Observable<Void> updateReceiverCoordinates(
        @Path("id") long connectionId,
        @Query("receiver_x") int receiverX,
        @Query("receiver_y") int receiverY
    );

    @POST("/connections") Observable<Void> createConnection(
        @Query("sender_id") String senderId,
        @Query("receiver_id") String receiverId,
        @Query("sender_x") int senderX,
        @Query("sender_y") int sendery,
        @Query("receiver_x") int receiverX,
        @Query("receiver_y)") int receiverY
    );

}
