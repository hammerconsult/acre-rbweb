package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Construcao;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class VOCadastroImobiliario {

    private Long id;
    private String numero;
    private String inscricaoCadastral;
    private BigDecimal valorVenal;
    private String descricaoProprietarios;
    private String enderecoCompleto;
    private List<VOConstrucao> construcoes;
    private String setor;
    private String lote;
    private String quadra;
    private String bairro;
    private String logradouro;
    private boolean selecionado;

    public VOCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.id = cadastroImobiliario.getId();
        this.inscricaoCadastral = cadastroImobiliario.getInscricaoCadastral();
        this.descricaoProprietarios = cadastroImobiliario.getDescricaoProprietarios();
        this.enderecoCompleto = cadastroImobiliario.getEnderecoCompletoResumido();
        this.construcoes = Lists.newArrayList();
        for (Construcao construcoe : cadastroImobiliario.getConstrucoes()) {
            this.construcoes.add(new VOConstrucao(construcoe));
        }
    }

    public VOCadastroImobiliario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorVenal() {
        return valorVenal;
    }

    public void setValorVenal(BigDecimal valorVenal) {
        this.valorVenal = valorVenal;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getDescricaoProprietarios() {
        return descricaoProprietarios;
    }

    public void setDescricaoProprietarios(String descricaoProprietarios) {
        this.descricaoProprietarios = descricaoProprietarios;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public List<VOConstrucao> getConstrucoes() {
        return construcoes;
    }

    public void setConstrucoes(List<VOConstrucao> construcoes) {
        this.construcoes = construcoes;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOCadastroImobiliario that = (VOCadastroImobiliario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
