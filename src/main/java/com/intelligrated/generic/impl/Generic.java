package com.intelligrated.generic.impl;

import com.intelligrated.generic.interfaces.GenericInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by sachin.subhedar on 01/19/2017.
 * <p>
 * Copyright (c) 2001-2016 Intelligrated [https://www.intelligrated.com/]
 * <p>
 * The  information  contained  herein  is  the  confidential  and  proprietary
 * information of Intelligrated.  This information is protected,  among others,
 * by the patent,  copyright,  trademark,  and trade secret laws of  the United
 * States and its several states.  Any use,  copying, or reverse engineering is
 * strictly prohibited. This software has been developed at private expense and
 * accordingly,  if used under Government  contract,  the use,  reproduction or
 * disclosure  of  this  information  is subject to  the restrictions set forth
 * under the  contract between  Intelligrated  and its customer.  By viewing or
 * receiving this information, you consent to the foregoing.
 */
@Service
public class Generic implements GenericInterface {

    Logger logger = LogManager.getLogger();

    @Autowired
    GateWay gateWay;

    public ResponseEntity<Object> postHello() {
        logger.info("postHello impl");
        String response = "hello";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public void setGateWay(GateWay gateWay) {
        this.gateWay = gateWay;
    }

}
