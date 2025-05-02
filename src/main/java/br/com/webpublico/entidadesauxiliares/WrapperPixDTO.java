package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class WrapperPixDTO implements Serializable {
    private List<PixDTO> lotePix;
    private String appKey;
    private String urlToken;
    private String urlQrCode;

    public WrapperPixDTO() {
        this.lotePix = Lists.newArrayList();
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUrlToken() {
        return urlToken;
    }

    public void setUrlToken(String urlToken) {
        this.urlToken = urlToken;
    }

    public String getUrlQrCode() {
        return urlQrCode;
    }

    public void setUrlQrCode(String urlQrCode) {
        this.urlQrCode = urlQrCode;
    }

    public List<PixDTO> getLotePix() {
        return lotePix;
    }

    public void setLotePix(List<PixDTO> lotePix) {
        this.lotePix = lotePix;
    }
}
