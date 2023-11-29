package ru.practicum.ewmmain.compilation.error;

public class CompilationExistsException extends RuntimeException {
    public CompilationExistsException(String name) {
        super(String.format("Compilation c name=%s уже существует.", name));
    }
}
