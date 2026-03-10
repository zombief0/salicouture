package com.norman.couture.restcontroller;

import com.norman.couture.service.dto.MessageResponse;
import com.norman.couture.service.dto.enums.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ControllerTools {

    public ResponseEntity<MessageResponse> getResponse(Message createMessage, HttpStatus status) {
        if (createMessage.equals(Message.SUCCES)) {
            return ResponseEntity.status(status).body(new MessageResponse(createMessage.name()));
        }

        return ResponseEntity.badRequest().body(new MessageResponse(createMessage.name()));
    }

}
