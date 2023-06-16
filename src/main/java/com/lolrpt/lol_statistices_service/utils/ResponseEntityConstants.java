package com.lolrpt.lol_statistices_service.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * RestController 혹은 ResponseBody를 사용하기 위해 만들어 둔 Class
 */
public class ResponseEntityConstants {

    public static final ResponseEntity<Void> RESPONSE_OK = new ResponseEntity<>(HttpStatus.OK);
    public static final ResponseEntity<Void> RESPONSE_CONFLICT = new ResponseEntity<>(HttpStatus.CONFLICT);
    public static final ResponseEntity<Void> RESPONSE_NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

}
