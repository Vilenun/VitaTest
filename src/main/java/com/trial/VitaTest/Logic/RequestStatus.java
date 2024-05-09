package com.trial.VitaTest.Logic;

public enum RequestStatus {
    DRAFT ("Черновик"),
    SENT ("Отправлено"),
    ACCEPTED ("Принято"),
    DENIED ("Отклонено");
    private final String status;

    RequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
