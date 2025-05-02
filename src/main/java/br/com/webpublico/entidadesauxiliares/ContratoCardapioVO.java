package br.com.webpublico.entidadesauxiliares;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContratoCardapioVO {

    private Long id;
    private String numero;
    private String fornecedor;
    private String unidadeContrato;
    private Date fimVigencia;
    private List<ContratoCardapioMaterialVO> materiais;

    public ContratoCardapioVO() {
        materiais = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getUnidadeContrato() {
        return unidadeContrato;
    }

    public void setUnidadeContrato(String unidadeContrato) {
        this.unidadeContrato = unidadeContrato;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public List<ContratoCardapioMaterialVO> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<ContratoCardapioMaterialVO> materiais) {
        this.materiais = materiais;
    }
}
