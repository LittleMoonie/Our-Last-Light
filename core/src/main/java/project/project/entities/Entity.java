package project.project.entities;

public interface Entity {
    <T> T getComponent(Class<T> componentClass);
}
