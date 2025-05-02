package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.Ordenacao;

import java.math.BigDecimal;

public class MaioresDevedores implements Comparable<MaioresDevedores> {

    private Long idPessoa;
    private Long idCadastro;
    private BigDecimal valor;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    private BigDecimal desconto;
    private String nome;
    private String cpfCnpj;
    private String tipoPessoa;
    private Ordenacao ordenacao;
    private String inscricaoCadastral;

    public MaioresDevedores() {
        zeraValores();
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    @Override
    public int compareTo(MaioresDevedores o) {
        if (Ordenacao.CRESCENTE.equals(this.getOrdenacao())) {
            return this.getValor().compareTo(o.getValor());
        } else {
            return o.getValor().compareTo(this.getValor());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaioresDevedores)) return false;

        MaioresDevedores that = (MaioresDevedores) o;

        if (idPessoa != null ? !idPessoa.equals(that.idPessoa) : that.idPessoa != null) return false;
        if (idCadastro != null ? !idCadastro.equals(that.idCadastro) : that.idCadastro != null) return false;
        if (valor != null ? !valor.equals(that.valor) : that.valor != null) return false;
        if (imposto != null ? !imposto.equals(that.imposto) : that.imposto != null) return false;
        if (taxa != null ? !taxa.equals(that.taxa) : that.taxa != null) return false;
        if (juros != null ? !juros.equals(that.juros) : that.juros != null) return false;
        if (multa != null ? !multa.equals(that.multa) : that.multa != null) return false;
        if (correcao != null ? !correcao.equals(that.correcao) : that.correcao != null) return false;
        if (honorarios != null ? !honorarios.equals(that.honorarios) : that.honorarios != null) return false;
        if (desconto != null ? !desconto.equals(that.desconto) : that.desconto != null) return false;
        if (nome != null ? !nome.equals(that.nome) : that.nome != null) return false;
        if (cpfCnpj != null ? !cpfCnpj.equals(that.cpfCnpj) : that.cpfCnpj != null) return false;
        if (inscricaoCadastral != null ? !inscricaoCadastral.equals(that.inscricaoCadastral) : that.inscricaoCadastral != null) return false;
        return !(tipoPessoa != null ? !tipoPessoa.equals(that.tipoPessoa) : that.tipoPessoa != null);

    }

    @Override
    public int hashCode() {
        int result = idPessoa != null ? idPessoa.hashCode() : 0;
        result = 31 * result + (idCadastro != null ? idCadastro.hashCode() : 0);
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        result = 31 * result + (imposto != null ? imposto.hashCode() : 0);
        result = 31 * result + (taxa != null ? taxa.hashCode() : 0);
        result = 31 * result + (juros != null ? juros.hashCode() : 0);
        result = 31 * result + (multa != null ? multa.hashCode() : 0);
        result = 31 * result + (correcao != null ? correcao.hashCode() : 0);
        result = 31 * result + (honorarios != null ? honorarios.hashCode() : 0);
        result = 31 * result + (desconto != null ? desconto.hashCode() : 0);
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        result = 31 * result + (cpfCnpj != null ? cpfCnpj.hashCode() : 0);
        result = 31 * result + (tipoPessoa != null ? tipoPessoa.hashCode() : 0);
        result = 31 * result + (inscricaoCadastral != null ? inscricaoCadastral.hashCode() : 0);
        return result;
    }

    public void zeraValores() {
        valor = BigDecimal.ZERO;
        imposto = BigDecimal.ZERO;
        taxa = BigDecimal.ZERO;
        juros = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        correcao = BigDecimal.ZERO;
        honorarios = BigDecimal.ZERO;
        desconto = BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "MaioresDevedores{" +
            "idPessoa=" + idPessoa +
            ", idCadastro=" + idCadastro +
            ", valor=" + valor +
            ", imposto=" + imposto +
            ", taxa=" + taxa +
            ", juros=" + juros +
            ", multa=" + multa +
            ", correcao=" + correcao +
            ", honorarios=" + honorarios +
            ", desconto=" + desconto +
            ", nome='" + nome + '\'' +
            ", cpfCnpj='" + cpfCnpj + '\'' +
            ", tipoPessoa='" + tipoPessoa + '\'' +
            ", ordenacao=" + ordenacao +
            ", inscricaoCadastral='" + inscricaoCadastral + '\'' +
            '}';
    }
}
