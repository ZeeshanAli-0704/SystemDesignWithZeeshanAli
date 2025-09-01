package org.example.exceptions;
public class PollNotFoundException extends RuntimeException {
    public PollNotFoundException(int pollId) { super("Poll not found: " + pollId); }
}
