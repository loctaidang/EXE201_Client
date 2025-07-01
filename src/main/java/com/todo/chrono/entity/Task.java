package com.todo.chrono.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

import com.todo.chrono.enums.TaskPriority;
import com.todo.chrono.enums.TaskStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    // relationship
    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TaskAssignee> assignees;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TaskBlock> blocks;
}