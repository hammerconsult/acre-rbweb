package br.com.webpublico.esocial.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "eSocialUtilService")
public class ESocialUtilService {

    @Autowired
    private S1010Service s1010Service;

    @Autowired
    private S2210Service s2210Service;

    public S1010Service getS1010Service() {
        return s1010Service;
    }

    public S2210Service getS2210Service() {
        return s2210Service;
    }

}
