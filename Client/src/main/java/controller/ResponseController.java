package controller;

import model.shared.ServerResponse;
import model.shared.TypeOfRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface ResponseController {
    ExecutorService exec = Executors.newFixedThreadPool(5);

    void onSuccess(TypeOfRequest action, ServerResponse response);

    void onError(TypeOfRequest action, ServerResponse response); //abbiamo diviso onError e onSocketError per motivazioni semantiche e per avere una descrizione piú precisa dell'errore

    void onSocketError(Exception e, String errorMessage);
}
