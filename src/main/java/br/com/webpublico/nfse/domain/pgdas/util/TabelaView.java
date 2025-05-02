package br.com.webpublico.nfse.domain.pgdas.util;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabelaView {

    private String nomeTabela;
    private List<ColunaView> headers;
    private Map<Object, List<ColunaView>> objetos;

    public TabelaView() {
        this.objetos = new HashMap<>();
        this.headers = Lists.newArrayList();
    }

    public String getNomeTabela() {
        return nomeTabela;
    }

    public void setNomeTabela(String nomeTabela) {
        this.nomeTabela = nomeTabela;
    }

    public Map<Object, List<ColunaView>> getObjetos() {
        return objetos;
    }

    public List<Object> getObjetosHeader() {
        return Lists.newArrayList(objetos.keySet());
    }

    public List<ColunaView> getObjetosColuna(Object ob) {
        return objetos.get(ob);
    }

    public void setObjetos(Map<Object, List<ColunaView>> objetos) {
        this.objetos = objetos;
    }

    public List<ColunaView> getHeaders() {
        return headers;
    }

    public void setHeaders(List<ColunaView> headers) {
        this.headers = headers;
    }
}
