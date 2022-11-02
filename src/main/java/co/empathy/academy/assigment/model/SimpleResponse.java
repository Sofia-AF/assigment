package co.empathy.academy.assigment.model;

import lombok.Value;

@Value
public class SimpleResponse {
    // Stores int value of the request made
    private int statusCode;
    // Stores String value with the contents wanted to show for each request
    private String bodyMessage;

}
