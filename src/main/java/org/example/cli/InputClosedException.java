package org.example.cli;

class InputClosedException extends RuntimeException {

    InputClosedException() {
        super("Input stream was closed");
    }
}
