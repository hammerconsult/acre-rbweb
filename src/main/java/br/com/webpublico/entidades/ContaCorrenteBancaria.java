/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "Bancario")
@Entity
@Audited
@Etiqueta("Conta Corrente Bancária")
public class ContaCorrenteBancaria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Transient
    @Tabelavel
    @Etiqueta("Banco")
    private Banco banco;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Agência")
    @Tabelavel
    @Pesquisavel
    private Agencia agencia;
    @Etiqueta("Tipo da Conta")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private ModalidadeConta modalidadeConta;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Número da Conta")
    private String numeroConta;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Dígito Verificador")
    @Obrigatorio
    private String digitoVerificador;
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private SituacaoConta situacao;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "contacor_pessoa",
        joinColumns = {
            @JoinColumn(name = "CONTASCORRENTESBANCARIAS_ID")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "PESSOAS_ID")
        })
    @Invisivel
    @Etiqueta("Pessoa")
    private List<Pessoa> pessoas;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToMany(mappedBy = "contaCorrenteBancaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContaCorrenteBancPessoa> contaCorrenteBancPessoas;

    @Pesquisavel
    @Etiqueta("Operação da Conta Bancaria")
    @Tabelavel
    private String contaBancaria;

    private Boolean contaConjunta;


    public ContaCorrenteBancaria() {
        dataRegistro = new Date();
        if (agencia != null && agencia.getBanco() != null) {
            banco = agencia.getBanco();
        }
    }

    public ContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.setId(contaCorrenteBancaria.getId());
        this.setBanco(contaCorrenteBancaria.getAgencia().getBanco());
        this.setAgencia(contaCorrenteBancaria.getAgencia());
        this.setNumeroConta(contaCorrenteBancaria.getNumeroConta());
        this.setDigitoVerificador(contaCorrenteBancaria.getDigitoVerificador());
        this.setSituacao(contaCorrenteBancaria.getSituacao());
        this.setModalidadeConta(contaCorrenteBancaria.getModalidadeConta());
    }

    public ContaCorrenteBancaria(String numeroConta, String digitoVerificador, Agencia agencia, SituacaoConta situacao, List<Pessoa> pessoas) {
        this.numeroConta = numeroConta;
        this.digitoVerificador = digitoVerificador;
        this.agencia = agencia;
        this.situacao = situacao;
        this.pessoas = new ArrayList<>();
        this.criadoEm = System.nanoTime();
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getNumeroContaSemCaracteres() {
        return numeroConta != null ? StringUtil.retornaApenasNumeros(numeroConta) : "0";
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public SituacaoConta getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoConta situacao) {
        this.situacao = situacao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ContaCorrenteBancPessoa> getContaCorrenteBancPessoas() {
        return contaCorrenteBancPessoas;
    }

    public void setContaCorrenteBancPessoas(List<ContaCorrenteBancPessoa> contaCorrenteBancPessoas) {
        this.contaCorrenteBancPessoas = contaCorrenteBancPessoas;
    }

    public ModalidadeConta getModalidadeConta() {
        return modalidadeConta;
    }

    public void setModalidadeConta(ModalidadeConta modalidadeConta) {
        this.modalidadeConta = modalidadeConta;
    }

    public String getNumeroContaComDigito() {
        return this.numeroConta + this.digitoVerificador;
    }

    public boolean isModalidadeContaSalarioNSGD() {
        return ModalidadeConta.CONTA_SALARIO_NSGD.equals(modalidadeConta);
    }

    @Override
    public String toString() {
        String descricao = "";

        if (agencia != null) {
            if (agencia.getBanco().getNumeroBanco() != null) {
                descricao += agencia.getBanco().getNumeroBanco() + "/";
            }
        }
        if (agencia != null) {
            descricao += agencia.getNumeroAgencia() + (agencia.getDigitoVerificador() == null ? "" : "-" + agencia.getDigitoVerificador()) + "/";
        }
        if (numeroConta != null) {
            descricao += numeroConta + "-";
        }
        if (digitoVerificador != null) {
            descricao += digitoVerificador + " - ";
        }
        if (modalidadeConta != null) {
            descricao += modalidadeConta.getDescricao() + " - ";
        }
        if (situacao != null) {
            descricao += situacao.getDescricao();
        }

        return descricao;
    }

    public Banco getBanco() {
        return this.getAgencia().getBanco();
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getDescricaoParaFichaFinanceira() {
        String descricao = "";
        if (agencia != null) {
            if (agencia.getBanco().getNumeroBanco() != null) {
                descricao += "Banco: " + agencia.getBanco().getNumeroBanco();
            }
        }
        if (agencia != null) {
            descricao += " Agência: " + agencia.getNumeroAgencia() + (agencia.getDigitoVerificador() == null ? "" : "-" + agencia.getDigitoVerificador());
        }
        if (numeroConta != null) {
            descricao += " Conta: " + numeroConta + "-";
        }
        if (digitoVerificador != null) {
            descricao += digitoVerificador;
        }
        return descricao;
    }

    public String getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    public Boolean getContaConjunta() {
        return contaConjunta;
    }

    public void setContaConjunta(Boolean contaConjunta) {
        this.contaConjunta = contaConjunta;
    }
}
