package br.com.webpublico.entidadesauxiliares;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class WrapperPayloadPixDTO implements Serializable {

    @JsonProperty("pix")
    private List<PayloadPixDTO> payloads;

    public WrapperPayloadPixDTO() {
        this.payloads = Lists.newArrayList();
    }

    public List<PayloadPixDTO> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<PayloadPixDTO> payloads) {
        this.payloads = payloads;
    }

    public static WrapperPayloadPixDTO toObject(String json) throws IOException {
        return new ObjectMapper().readValue(json, WrapperPayloadPixDTO.class);
    }
}
