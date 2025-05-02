package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaldoGrupoBemMovelDTO {
    private Long id;
    private LocalDate data;
    private BigDecimal valor;
    private Long idUnidadeOrganizacional;

    private TipoGrupo tipoGrupo;

    private TipoOperacaoBensMoveis operacao;

    private TipoLancamento tipoLancamento;

    private TipoOperacao tipoOperacao;
    private Boolean validarSaldo;

    private Long idGrupoBem;
    private String idOrigem;
    private String classeOrigem;

    public SaldoGrupoBemMovelDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public TipoOperacaoBensMoveis getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoOperacaoBensMoveis operacao) {
        this.operacao = operacao;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public Boolean getValidarSaldo() {
        return validarSaldo;
    }

    public void setValidarSaldo(Boolean validarSaldo) {
        this.validarSaldo = validarSaldo;
    }

    public Long getIdGrupoBem() {
        return idGrupoBem;
    }

    public void setIdGrupoBem(Long idGrupoBem) {
        this.idGrupoBem = idGrupoBem;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }
}

