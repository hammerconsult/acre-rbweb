package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by carlos on 06/10/15.
 */
public class RelatorioPatronalAuxiliar {
    private String codigo;
    private String orgao;
    private String matricula;
    private String numero;
    private String nome;
    private String descricaoGrupoRecurso;
    private BigDecimal quantidadeServidores;
    private BigDecimal vantagem;
    private BigDecimal baseRpps;
    private BigDecimal referenciaRpps;
    private BigDecimal rpps;
    private BigDecimal referenciaPatronal;
    private BigDecimal valorPatronal;
    private BigDecimal valortotal;
    private BigDecimal valorAliqutoSuplementar;
    private BigDecimal aliquotaSuplementar;

    public RelatorioPatronalAuxiliar() {
    }

    public BigDecimal getBaseRpps() {
        return baseRpps;
    }

    public void setBaseRpps(BigDecimal baseRpps) {
        this.baseRpps = baseRpps;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public BigDecimal getReferenciaPatronal() {
        return referenciaPatronal;
    }

    public void setReferenciaPatronal(BigDecimal referenciaPatronal) {
        this.referenciaPatronal = referenciaPatronal;
    }

    public BigDecimal getAliquotaSuplementar() {
        return aliquotaSuplementar;
    }

    public void setAliquotaSuplementar(BigDecimal aliquotaSuplementar) {
        this.aliquotaSuplementar = aliquotaSuplementar;
    }

    public String getDescricaoGrupoRecurso() {
        return descricaoGrupoRecurso;
    }

    public void setDescricaoGrupoRecurso(String descricaoGrupoRecurso) {
        this.descricaoGrupoRecurso = descricaoGrupoRecurso;
    }

    public BigDecimal getQuantidadeServidores() {
        return quantidadeServidores;
    }

    public void setQuantidadeServidores(BigDecimal quantidadeServidores) {
        this.quantidadeServidores = quantidadeServidores;
    }

    public BigDecimal getReferenciaRpps() {
        return referenciaRpps;
    }

    public void setReferenciaRpps(BigDecimal referenciaRpps) {
        this.referenciaRpps = referenciaRpps;
    }

    public BigDecimal getRpps() {
        return rpps;
    }

    public void setRpps(BigDecimal rpps) {
        this.rpps = rpps;
    }

    public BigDecimal getValorPatronal() {
        return valorPatronal;
    }

    public void setValorPatronal(BigDecimal valorPatronal) {
        this.valorPatronal = valorPatronal;
    }

    public BigDecimal getValortotal() {
        return valortotal;
    }

    public void setValortotal(BigDecimal valortotal) {
        this.valortotal = valortotal;
    }

    public BigDecimal getVantagem() {
        return vantagem;
    }

    public void setVantagem(BigDecimal vantagem) {
        this.vantagem = vantagem;
    }

    public BigDecimal getValorAliqutoSuplementar() {
        return valorAliqutoSuplementar;
    }

    public void setValorAliqutoSuplementar(BigDecimal valorAliqutoSuplementar) {
        this.valorAliqutoSuplementar = valorAliqutoSuplementar;
    }
}
