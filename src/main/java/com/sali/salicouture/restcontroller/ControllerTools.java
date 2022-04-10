package com.sali.salicouture.restcontroller;

import com.sali.salicouture.service.dto.MessageResponse;
import com.sali.salicouture.service.dto.enums.Message;
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
