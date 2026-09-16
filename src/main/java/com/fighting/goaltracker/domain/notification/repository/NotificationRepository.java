package com.fighting.goaltracker.domain.notification.repository;

import com.fighting.goaltracker.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    void deleteByUser_UserId(Integer userId);

    Optional<Notification> findByNotificationIdAndUser_UserId(Integer notificationId, Integer userId);
}
