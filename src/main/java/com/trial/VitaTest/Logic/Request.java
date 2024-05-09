package com.trial.VitaTest.Logic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;

@Entity
@Setter
@NoArgsConstructor
@Table(name="Requests")
public class Request {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private long id;

    @Getter
    @NotBlank
    @Column(name = "request")
    private String request;

    @Getter
    @Column(name = "requestStatus")
    private String requestStatus;

    @UpdateTimestamp
    @Column(name = "date")
    private Instant date;

    @Getter
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private RequestUser user;

    public Request(long id, String name, String requestStatus, Instant date) {
        super();
        this.request = name;
        this.requestStatus = requestStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return id == request.id && Objects.equals(request, request.request) && Objects.equals(requestStatus, request.requestStatus) && Objects.equals(date, request.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, request, requestStatus, date);
    }

    public void setName(String name) {
        this.request = name;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}
