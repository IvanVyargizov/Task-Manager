package hexlet.code.repository;

import hexlet.code.model.Task;
import hexlet.code.model.QTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface TaskRepository extends JpaRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>, QuerydslBinderCustomizer<QTask> {

    @Override
    default void customize(QuerydslBindings bindings, QTask task) {

        bindings.bind(task.taskStatus.id).first(
                (path, value) -> path.eq(value));
        bindings.bind(task.executor.id).first(
                (path, value) -> path.eq(value));
        bindings.bind(task.labels.any().id).first(
                (path, value) -> path.eq(value));
        bindings.bind(task.author.id).first(
                (path, value) -> path.eq(value));
    }

}
