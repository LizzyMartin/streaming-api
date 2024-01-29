package br.com.fiap.streaming.exceptions;

public class VideoAlreadyLikedException extends Exception {

    private static final long serialVersionUID = 1L;

    public VideoAlreadyLikedException(String message) {
        super(message);
    }

    public VideoAlreadyLikedException() {
        //TODO Auto-generated constructor stub
    }
    
}
