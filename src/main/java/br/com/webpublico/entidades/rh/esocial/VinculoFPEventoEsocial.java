package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class VinculoFPEventoEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private PessoaFisica prestadorServico;
    @ManyToOne
    private RegistroEventoEsocial registroEventoEsocial;

    @Transient
    private String nome;
    @Transient
    private String matricula;
    @Transient
    private Long idFichaFinanceira;
    @Transient
    private Long idVinculo;

    @Transient
    private Long idPessoa;
    @Transient
    private BigDecimal valorEventoFP;
    @Transient
    private Long idFolhaPagamento;
    @Transient
    private BigDecimal valorPrestador;
    @Transient
    private Date dataPagamentoPrestador;
    @Transient
    private List<Long> idsFichas;
    @Transient
    private String cpf;

    public VinculoFPEventoEsocial() {
        idsFichas = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public RegistroEventoEsocial getRegistroEventoEsocial() {
        return registroEventoEsocial;
    }

    public void setRegistroEventoEsocial(RegistroEventoEsocial registroEventoEsocial) {
        this.registroEventoEsocial = registroEventoEsocial;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Long getIdFichaFinanceira() {
        return idFichaFinanceira;
    }

    public void setIdFichaFinanceira(Long idFichaFinanceira) {
        this.idFichaFinanceira = idFichaFinanceira;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }

    public BigDecimal getValorEventoFP() {
        return valorEventoFP;
    }

    public void setValorEventoFP(BigDecimal valorEventoFP) {
        this.valorEventoFP = valorEventoFP;
    }

    public Long getIdFolhaPagamento() {
        return idFolhaPagamento;
    }

    public void setIdFolhaPagamento(Long idFolhaPagamento) {
        this.idFolhaPagamento = idFolhaPagamento;
    }

    public PessoaFisica getPrestadorServico() {
        return prestadorServico;
    }

    public void setPrestadorServico(PessoaFisica prestadorServico) {
        this.prestadorServico = prestadorServico;
    }

    public BigDecimal getValorPrestador() {
        return valorPrestador;
    }

    public void setValorPrestador(BigDecimal valorPrestador) {
        this.valorPrestador = valorPrestador;
    }

    public Date getDataPagamentoPrestador() {
        return dataPagamentoPrestador;
    }

    public void setDataPagamentoPrestador(Date dataPagamentoPrestador) {
        this.dataPagamentoPrestador = dataPagamentoPrestador;
    }

    public List<Long> getIdsFichas() {
        return idsFichas;
    }

    public void setIdsFichas(List<Long> idsFichas) {
        this.idsFichas = idsFichas;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        if (matricula == null && vinculoFP != null) {
            return vinculoFP.getMatriculaFP().toString();
        }
        if (prestadorServico != null) {
            return prestadorServico.getNomeCpfCnpj();
        }
        if (nome != null) {
            return nome;
        }
        return matricula + " - " + nome;
    }
}
