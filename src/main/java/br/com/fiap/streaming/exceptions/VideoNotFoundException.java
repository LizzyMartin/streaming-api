package br.com.fiap.streaming.exceptions;

public class VideoNotFoundException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public VideoNotFoundException(String message) {
        super(message);
    }

    public VideoNotFoundException() {
        //TODO Auto-generated constructor stub
    }
}
