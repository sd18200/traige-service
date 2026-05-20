package org.acme;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TriageInput {

     public String getMessage() {
          return message;
     }

     public void setMessage(String message) {
          this.message = message;
     }

     String message;

}
