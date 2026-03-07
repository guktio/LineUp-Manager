package com.grenade.main.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.GenerationType;

@Entity
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE grenade SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@ToString()
public class Grenade {

    public enum GrenadeType {
        SMOKE,
        FLASH,
        HE,
        MOLOTOV,
        INCENDIARY,
    }

    public enum MapType {
        DUST2,
        MIRAGE,
        NUKE,
        ANCIENT,
        TRAIN,
        INFERNO,
        OVERPASS,
    }

    public enum Side {
        Both,
        T,
        CT
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MapType map;

    @Enumerated(EnumType.STRING)
    private GrenadeType grenadeType;

    private String team;
    
    private Side side;

    private String media;

    private String thumbnail;

    private String command;

    @Column(nullable = false)
    @Builder.Default
    private Long stars = 0L;

    private String movement;
    private String strength;

    private String description;

    @ManyToOne(optional = false)
    private User author;

    @Builder.Default
    private boolean deleted = false;

    @Builder.Default
    private boolean approved = false;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Kyiv")
    private LocalDateTime createdAt;
}
