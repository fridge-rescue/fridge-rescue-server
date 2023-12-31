package team.rescue.fridge.fridge.ingredient.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.rescue.fridge.fridge.Fridge;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_ingredient")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class userIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ingredient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "memo", length = 20)
    private String memo;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
