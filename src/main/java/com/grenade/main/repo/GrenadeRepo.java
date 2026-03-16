package com.grenade.main.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.grenade.main.entity.Grenade;
import com.grenade.main.entity.User;

@Repository
public interface GrenadeRepo extends RepoBase<Grenade, UUID>{
    @Query(""" 
        SELECT g FROM Grenade g 
        WHERE (:map IS NULL OR g.map = :map) 
        AND (:grenadeType IS NULL OR g.grenadeType = :grenadeType)
        AND (:author IS NULL OR g.author.id = :author)
        AND (
            :name IS NULL 
            OR CAST(g.name AS string) LIKE CONCAT('%', CAST(:name AS string), '%')
            OR CAST(g.description AS string) LIKE CONCAT('%', CAST(:name AS string), '%')
        )
        AND (:likedByUserId IS NULL OR EXISTS(
            SELECT 1 FROM Stars s
            WHERE s.grenade = g
            AND s.user.id = :likedByUserId
        ))
        AND  g.approved = true
        AND  g.ready = true
    """)
    Page<Grenade> findByFilter(Pageable pageable,
                            @Param("map") Grenade.MapType map,
                            @Param("grenadeType") Grenade.GrenadeType grenadeType,
                            @Param("author") Long author,
                            @Param("name") String name,
                            @Param("likedByUserId") Long likedByUserId);

    List<Grenade> findByAuthor(User author);


    @Query("""
            SELECT g FROM Grenade g
            WHERE g.author.id = :author
            AND g.ready = false
            """)
    Page<Grenade> findUnreadyByAuthor(Pageable pageable, @Param("author") Long author);

    @Query("""
            SELECT g FROM Grenade g
            WHERE g.approved = :approved
            """)
    Page<Grenade> findApproved(Pageable pageable, @Param("approved") boolean approved);


    @Modifying
    @Transactional
    @Query("UPDATE Grenade g SET g.approved = true WHERE g.uuid = :uuid")
    void setApprovedTrueByUuid(UUID uuid);

    @Modifying
    @Transactional
    @Query("UPDATE Grenade g SET g.ready = true WHERE g.uuid = :uuid")
    void setReadyTrueByUuid(UUID uuid);

    @Modifying
    @Transactional
    @Query("UPDATE Grenade g SET g.stars = g.stars +1 WHERE g.uuid = :uuid")
    void increaseStars(@Param("uuid") UUID uuid);

    @Modifying
    @Transactional
    @Query("UPDATE Grenade g SET g.stars = g.stars -1 WHERE g.uuid = :uuid")
    void decreaseStars(@Param("uuid") UUID uuid);
}
