package com.intelligrated.generic.controller;


import com.intelligrated.generic.interfaces.GenericInterface;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
public class GenericController {

    @Autowired
    GenericInterface genericInterface;

    private final static Logger logger = LogManager.getLogger();

    @CrossOrigin(origins = "*")
    @ApiOperation(value = "post hello", nickname = "post hello message")
    @RequestMapping(method = RequestMethod.POST, value = "/hello", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public Object postHello() {
        logger.info("end point /hello ");
        return genericInterface.postHello();
    }

    public GenericInterface getGenericInterface() {
        return genericInterface;
    }

    @SuppressWarnings("unused")
    public void setGenericInterface(GenericInterface genericInterface) {
        this.genericInterface = genericInterface;
    }
}

