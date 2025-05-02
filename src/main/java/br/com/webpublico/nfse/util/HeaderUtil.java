package br.com.webpublico.nfse.util;

import org.springframework.http.HttpHeaders;

public class HeaderUtil {

    public static HttpHeaders setMessageError(String... messageError) {
        HttpHeaders header = new HttpHeaders();
        for (String s : messageError) {
            header.add("nfse-message-error", s);
        }
        return header;
    }
}
