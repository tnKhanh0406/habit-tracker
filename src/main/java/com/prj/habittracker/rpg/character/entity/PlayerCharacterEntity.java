package com.prj.habittracker.rpg.character.entity;

import com.prj.habittracker.common.enums.CharacterClass;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "player_characters",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_player_character_user",
                        columnNames = "user_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Long exp;

    @Column(nullable = false)
    private Integer hp;

    @Column(name = "max_hp", nullable = false)
    private Integer maxHp;

    @Column(nullable = false)
    private Integer mp;

    @Column(name = "max_mp", nullable = false)
    private Integer maxMp;

    @Column(nullable = false)
    private Long coin;

    @Column(nullable = false)
    private Long gem;

    @Column(nullable = false)
    private Integer strength;

    @Column(nullable = false)
    private Integer intelligence;

    @Column(nullable = false)
    private Integer vitality;

    @Column(nullable = false)
    private Integer agility;

    @Column(name = "stat_points", nullable = false)
    private Integer statPoints;

    @Column(name = "max_level_reached", nullable = false)
    private Integer maxLevelReached;

    @Enumerated(EnumType.STRING)
    @Column(name = "character_class", length = 30)
    private CharacterClass characterClass;

    private OffsetDateTime classChangedAt;

    @Column(name = "mana_regenerated_at", nullable = false)
    private LocalDate manaRegeneratedAt;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
