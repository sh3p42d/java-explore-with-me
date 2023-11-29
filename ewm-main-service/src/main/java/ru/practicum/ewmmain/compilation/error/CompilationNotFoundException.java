package ru.practicum.ewmmain.compilation.error;

import javax.persistence.EntityNotFoundException;

public class CompilationNotFoundException extends EntityNotFoundException {
    public CompilationNotFoundException(Long id) {
        super(String.format("Compilation с id=%s не найдена", id));
    }
}
