package br.com.webpublico.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/log")
@Controller
public class LogResource {

    private static final Logger logger = LoggerFactory.getLogger(LogResource.class);

    @RequestMapping(method = RequestMethod.GET)
    public void logs() {
        logger.error("Logs ativos...");
        logger.trace("TRACE");
        logger.debug("DEBUG");
        logger.info("INFO");
        logger.warn("WARN");
        logger.error("ERROR");
    }

}
