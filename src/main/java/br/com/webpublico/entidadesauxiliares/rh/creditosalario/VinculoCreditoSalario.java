package br.com.webpublico.entidadesauxiliares.rh.creditosalario;

import br.com.webpublico.entidades.ContaCorrenteBancaria;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class VinculoCreditoSalario implements Serializable {


    protected static final Logger logger = LoggerFactory.getLogger(VinculoCreditoSalario.class);

    private Long idVinculo;
    private String matricula;
    private BigDecimal valor;
    private String grupo;
    private Long unidadeId;
    private Long recursoFPId;
    private VinculoFP vinculoFP;
    private ContaCorrenteBancaria conta;
    private PessoaFisica pessoa;
    private Long grupoId;

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public ContaCorrenteBancaria getConta() {
        return conta;
    }

    public void setConta(ContaCorrenteBancaria conta) {
        this.conta = conta;
    }

    public PessoaFisica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaFisica pessoa) {
        this.pessoa = pessoa;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getRecursoFPId() {
        return recursoFPId;
    }

    public void setRecursoFPId(Long recursoFPId) {
        this.recursoFPId = recursoFPId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VinculoCreditoSalario that = (VinculoCreditoSalario) o;
        return idVinculo.equals(that.idVinculo) && pessoa.equals(that.pessoa) && valor.equals(that.valor) && grupoId.equals(that.grupoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVinculo, pessoa, valor, grupoId);
    }
}
