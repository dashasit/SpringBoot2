package com.intelligrated.generic.impl;


import com.intelligrated.generic.interfaces.GateWayInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

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


public class GateWay implements GateWayInterface, Serializable {

    @Autowired
    @LoadBalanced
    RestTemplate restTemplate;

    private Logger logger = LogManager.getLogger();

    public GateWay(){
    }

    public void postToEndPoint(String urlString){
        logger.info("post for end point {}", urlString);
        restTemplate.postForObject(urlString,null, String.class);
    }

}
