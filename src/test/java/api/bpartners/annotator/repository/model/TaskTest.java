package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class TaskTest {
  @Test
  void task_equals_hashcode() {
    var task = Task.builder().id("id").status(COMPLETED).filename("file").userId("userid").build();
    var task1 = Task.builder().id("id").status(COMPLETED).filename("file").userId("userid").build();
    var task2 = Task.builder().id("id").status(COMPLETED).filename("file").userId("userid").build();

    assertEquals(task, task1);
    assertEquals(task1, task);
    assertEquals(task2, task);
    assertEquals(task2.hashCode(), task.hashCode());
    assertEquals(task.hashCode(), task1.hashCode());
    assertEquals(task1.hashCode(), task.hashCode());
    assertEquals(task2.hashCode(), task.hashCode());
    assertEquals(task2.hashCode(), task.hashCode());
    assertNotEquals(task, new Task());
    assertNotEquals(task.hashCode(), new Task().hashCode());
  }
}
