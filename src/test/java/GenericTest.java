import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligrated.generic.Application;
import com.intelligrated.generic.configuration.GeneralConfiguration;
import com.intelligrated.generic.controller.GenericController;
import com.intelligrated.generic.impl.GateWay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@WebAppConfiguration
@SpringApplicationConfiguration(classes = {Application.class, GeneralConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class GenericTest {
    private MockMvc mvc;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    GenericController genericController;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    GateWay mockedGateWay = Mockito.mock(GateWay.class);

    @Before
    public void setup() {
        doNothing().when(mockedGateWay).postToEndPoint(any());
        genericController.getGenericInterface().setGateWay(mockedGateWay);
        mvc = MockMvcBuilders.standaloneSetup(genericController).build();
    }

    @Test
    public void postHelloTest() {
        try {
            mvc.perform((MockMvcRequestBuilders.post("/hello")
                    .contentType(MediaType.APPLICATION_JSON)))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception e) {
            logger.error(e);
        }

    }
}
