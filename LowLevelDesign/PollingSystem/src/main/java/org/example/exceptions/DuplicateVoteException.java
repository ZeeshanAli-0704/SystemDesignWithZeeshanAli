package org.example.exceptions;

public class DuplicateVoteException extends RuntimeException {
    public DuplicateVoteException(String msg) { super(msg); }
}
