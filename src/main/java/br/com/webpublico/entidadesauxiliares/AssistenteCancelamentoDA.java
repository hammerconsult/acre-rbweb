package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;
import br.com.webpublico.entidades.UsuarioSistema;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteCancelamentoDA {
    private UsuarioSistema usuarioSistema;
    private Integer cancelados;
    private Integer total;
    private List<ItemInscricaoDividaAtiva> itens;
    private List<String> erros;
    private Boolean finalizar = Boolean.FALSE;

    public AssistenteCancelamentoDA(List<ItemInscricaoDividaAtiva> itens, UsuarioSistema usuarioSistema) {
        this();
        this.itens = itens;
        this.usuarioSistema = usuarioSistema;
        total = itens.size();
    }

    public AssistenteCancelamentoDA() {
        cancelados = 0;
        total = 0;
        erros = Lists.newArrayList();
    }

    public Double getPorcentagem() {
        if (cancelados == null || total == null) {
            return 0d;
        }
        return (cancelados.doubleValue() / total.doubleValue()) * 100;
    }

    public void conta() {
        cancelados++;
    }

    public Integer getCancelados() {
        return cancelados;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ItemInscricaoDividaAtiva> getItens() {
        return itens;
    }

    public void setItens(List<ItemInscricaoDividaAtiva> itens) {
        this.itens = itens;
    }

    public void addError(String error) {
        erros.add(error);
    }

    public List<String> getErros() {
        return erros;
    }

    public Boolean getFinalizar() {
        return finalizar;
    }

    public void setFinalizar(Boolean finalizar) {
        this.finalizar = finalizar;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
