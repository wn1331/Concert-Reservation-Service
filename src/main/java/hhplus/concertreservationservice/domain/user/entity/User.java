package hhplus.concertreservationservice.domain.user.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal point;


    @Builder
    public User(String name, BigDecimal point) {
        this.name = name;
        this.point = point;
    }

    public void pointCharge(BigDecimal amount){
        this.point = this.point.add(amount);
    }

    public void pointUse(BigDecimal price) {
        this.point = this.point.subtract(price);
    }
}
