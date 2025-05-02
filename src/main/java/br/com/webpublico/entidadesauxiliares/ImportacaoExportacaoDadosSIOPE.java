package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HardRock on 29/03/2017.
 */
public class ImportacaoExportacaoDadosSIOPE {
    private String numero;
    private String localExercicio;
    private String numeroLocalExercicio;
    private String nome;
    private String cpf;
    private BigDecimal cargaHoraria;
    private String tipoCategoria;
    private String categoria;
    private BigDecimal salarioBase;
    private BigDecimal remuneracaoParcelaMaximaSessenta;
    private BigDecimal remuneracaoParcelaMaximaQuarenta;
    private BigDecimal outrasReceita;
    private BigDecimal total;
    private PessoaFisica pessoaFisica;
    private List<VinculoFP> vinculos;

    public ImportacaoExportacaoDadosSIOPE() {
        salarioBase = BigDecimal.ZERO;
        remuneracaoParcelaMaximaSessenta = BigDecimal.ZERO;
        remuneracaoParcelaMaximaQuarenta = BigDecimal.ZERO;
        outrasReceita = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
        vinculos = Lists.newLinkedList();
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLocalExercicio() {
        return localExercicio;
    }

    public void setLocalExercicio(String localExercicio) {
        this.localExercicio = localExercicio;
    }

    public BigDecimal getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(BigDecimal cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(String tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public BigDecimal getRemuneracaoParcelaMaximaSessenta() {
        return remuneracaoParcelaMaximaSessenta;
    }

    public void setRemuneracaoParcelaMaximaSessenta(BigDecimal remuneracaoParcelaMaximaSessenta) {
        this.remuneracaoParcelaMaximaSessenta = remuneracaoParcelaMaximaSessenta;
    }

    public BigDecimal getRemuneracaoParcelaMaximaQuarenta() {
        return remuneracaoParcelaMaximaQuarenta;
    }

    public void setRemuneracaoParcelaMaximaQuarenta(BigDecimal remuneracaoParcelaMaximaQuarenta) {
        this.remuneracaoParcelaMaximaQuarenta = remuneracaoParcelaMaximaQuarenta;
    }

    public BigDecimal getOutrasReceita() {
        return outrasReceita;
    }

    public void setOutrasReceita(BigDecimal outrasReceita) {
        this.outrasReceita = outrasReceita;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public List<VinculoFP> getVinculos() {
        return vinculos;
    }

    public void setVinculos(List<VinculoFP> vinculos) {
        this.vinculos = vinculos;
    }

    public String getNumeroLocalExercicio() {
        return numeroLocalExercicio;
    }

    public void setNumeroLocalExercicio(String numeroLocalExercicio) {
        this.numeroLocalExercicio = numeroLocalExercicio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
