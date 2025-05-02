/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.enums.TipoContaBancaria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Bancario")
@Entity

@Audited
@Etiqueta("Conta Bancária")
public class ContaBancariaEntidade extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição da Conta")
    @Tabelavel
    private String nomeConta;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo")
    private TipoContaBancaria tipoContaBancaria;
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private SituacaoConta situacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Abertura")
    private Date dataAbertura;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Encerramento")
    private Date dataEncerramento;
    @Etiqueta("Número da Conta")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String numeroConta;
    @Etiqueta("Dígito Verificador")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String digitoVerificador;
    @ManyToOne
    @Etiqueta("Agência")
    @Pesquisavel
    @Tabelavel
    private Agencia agencia;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private Entidade entidade;
    @OneToMany(mappedBy = "contaBancariaEntidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubConta> subContas;
    @Etiqueta("Código do Convênio")
    private String codigoDoConvenio;
    @Etiqueta("Observaçao")
    private String observacao;
    @Etiqueta("Tipo de Conta")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    private ModalidadeConta modalidadeConta;
    @Etiqueta("Conta Principal Folha de Pagamentos")
    private Boolean contaPrincipalFP;

    private String parametroTransmissao;

    @Pesquisavel
    @Etiqueta("Operação da Conta Bancaria")
    private String contaBancaria;

    private String carteiraCedente;
    private String numeroCarteiraCobranca;
    private String variacaoCarteiraCobranca;
    private String versaoLayoutLoteCreditoSalario;
    private String indicativoFormaPagamentoServ;
    private String camaraCompensacao;
    private String tipoDeCompromisso;
    private String codigoDoCompromisso;

    private String densidade;


    public ContaBancariaEntidade() {
        subContas = new ArrayList<SubConta>();
        contaPrincipalFP = false;
    }

    public String getNumeroContaComDigitoVerificador() {
        return this.numeroConta + "-" + this.digitoVerificador;
    }

    public String getNumeroContaComDigitoVerificadorArquivoOBN600() {
        return this.numeroConta + this.digitoVerificador;
    }

    public ContaBancariaEntidade(Long id, String nomeConta, TipoContaBancaria tipoContaBancaria, SituacaoConta situacao, Date dataAbertura, Date dataEncerramento) {
        this.id = id;
        this.nomeConta = nomeConta;
        this.tipoContaBancaria = tipoContaBancaria;
        this.situacao = situacao;
        this.dataAbertura = dataAbertura;
        this.dataEncerramento = dataEncerramento;
    }

    public ContaBancariaEntidade(String numeroConta, String digitoVerificador, Agencia agencia, SituacaoConta situacao, Entidade entidade, List<SubConta> subContas, TipoContaBancaria tipoContaBancaria, String codigoDoConvenio, String observacao, String nomeConta, Date dataAbertura, Date dataEncerramento) {
        this.numeroConta = numeroConta;
        this.digitoVerificador = digitoVerificador;
        this.agencia = agencia;
        this.situacao = situacao;
        this.entidade = entidade;
        this.subContas = subContas;
        this.tipoContaBancaria = tipoContaBancaria;
        this.codigoDoConvenio = codigoDoConvenio;
        this.observacao = observacao;
        this.nomeConta = nomeConta;
        this.dataAbertura = dataAbertura;
        this.dataEncerramento = dataEncerramento;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoDoConvenio() {
        return codigoDoConvenio;
    }

    public void setCodigoDoConvenio(String codigoDoConvenio) {
        this.codigoDoConvenio = codigoDoConvenio;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public SituacaoConta getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoConta situacao) {
        this.situacao = situacao;
    }

    public List<SubConta> getSubContas() {
        return subContas;
    }

    public void setSubContas(List<SubConta> subContas) {
        this.subContas = subContas;
    }

    public TipoContaBancaria getTipoContaBancaria() {
        return tipoContaBancaria;
    }

    public void setTipoContaBancaria(TipoContaBancaria tipoContaBancaria) {
        this.tipoContaBancaria = tipoContaBancaria;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Date getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(Date dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public String getNomeConta() {
        return nomeConta;
    }

    public void setNomeConta(String nomeConta) {
        this.nomeConta = nomeConta;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        if (entidade != null && agencia != null) {
            return entidade + " - " + agencia + "/" + numeroConta + "-" + digitoVerificador;
        } else {
            return numeroConta + " - " + digitoVerificador + " - " + nomeConta;
        }
    }

    public String toStringAutoComplete() {
        try {
            return agencia.getBanco().getNumeroBanco() + " - " + agencia.getNumeroAgencia() + "-" + agencia.getDigitoVerificador() + " - " + numeroConta + "-" + digitoVerificador + " - " + nomeConta;
        } catch (Exception e) {
            return " ";
        }
    }

    public String toStringComponenteContaBancaria() {
        try {
            return agencia.getBanco().getNumeroBanco() + "/" + agencia.getNumeroAgencia() + "-" + agencia.getDigitoVerificador() + "/" + numeroConta + "-" + digitoVerificador + " - " + nomeConta + " - " + tipoContaBancaria.toString() + " - " + situacao.toString();
        } catch (Exception e) {
            return " ";
        }
    }

    public String toStringNumeroNomeConta() {
        try {
            return numeroConta + "-" + digitoVerificador + " " + nomeConta;
        } catch (Exception e) {
            return " ";
        }
    }

    public String toStringBancoBancoAgenciaTipoContaNumeroConta() {
        String descricaoContaBancaria = "";
        if (agencia.getBanco().getDescricao() != null) {
            descricaoContaBancaria += "Banco: " + agencia.getBanco().getDescricao() + " ";
        }
        if (agencia != null) {
            descricaoContaBancaria += "Número Agência: " + agencia.getNumeroAgencia() + "-";
            if (agencia.getDigitoVerificador() != null) {
                descricaoContaBancaria += agencia.getDigitoVerificador() + " ";
            }
        }
        if (tipoContaBancaria != null) {
            descricaoContaBancaria += "Tipo Conta: " + tipoContaBancaria + " ";
        }
        if (numeroConta != null) {
            descricaoContaBancaria += "Número Conta: " + numeroConta + "-";
            if (digitoVerificador != null) {
                descricaoContaBancaria += digitoVerificador;
            }
        }
        return descricaoContaBancaria;
    }

    public String toStringBancoAgenciaContaDescricao() {
        String descricaoContaBancaria = "";
        if (agencia.getBanco() != null) {
            descricaoContaBancaria += agencia.getBanco().getNumeroBanco() + "/";
        }
        if (agencia != null) {
            descricaoContaBancaria += agencia.getNumeroAgencia() + "-";
            if (agencia.getDigitoVerificador() != null) {
                descricaoContaBancaria += agencia.getDigitoVerificador() + " ";
            } else {
                descricaoContaBancaria += " ";
            }
            descricaoContaBancaria += "/";
        }
        if (numeroConta != null) {
            descricaoContaBancaria += numeroConta + "-";
            if (digitoVerificador != null) {
                descricaoContaBancaria += digitoVerificador;
            } else {
                descricaoContaBancaria += " ";
            }
        }
        descricaoContaBancaria += "/" + nomeConta;
        return descricaoContaBancaria;
    }

    public String toStringAutoCompleteContaBancaria() {
        try {
            return agencia.getBanco().getNumeroBanco() +
                "/" + agencia.getNumeroAgencia() + "-" + agencia.getDigitoVerificador() +
                "/" + numeroConta + "-" + digitoVerificador +
                " - " + nomeConta +
                " - " + tipoContaBancaria.getDescricao() +
                " - " + situacao;
        } catch (Exception ex) {

        }
        return " ";
    }

    public String getNumeroCompleto() {
        return this.numeroConta + " - " + this.digitoVerificador;
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

    public Boolean getContaPrincipalFP() {
        return contaPrincipalFP;
    }

    public void setContaPrincipalFP(Boolean contaPrincipalFP) {
        this.contaPrincipalFP = contaPrincipalFP;
    }

    public String getParametroTransmissao() {
        return parametroTransmissao;
    }

    public void setParametroTransmissao(String parametroTransmissao) {
        this.parametroTransmissao = parametroTransmissao;
    }

    public String getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    public String getCarteiraCedente() {
        return carteiraCedente;
    }

    public void setCarteiraCedente(String carteiraCedente) {
        this.carteiraCedente = carteiraCedente;
    }

    public String getNumeroCarteiraCobranca() {
        return numeroCarteiraCobranca;
    }

    public void setNumeroCarteiraCobranca(String numeroCarteiraCobranca) {
        this.numeroCarteiraCobranca = numeroCarteiraCobranca;
    }

    public String getVariacaoCarteiraCobranca() {
        return variacaoCarteiraCobranca;
    }

    public void setVariacaoCarteiraCobranca(String variacaoCarteiraCobranca) {
        this.variacaoCarteiraCobranca = variacaoCarteiraCobranca;
    }

    public String getDensidade() {
        return densidade;
    }

    public void setDensidade(String densidade) {
        this.densidade = densidade;
    }

    public String getVersaoLayoutLoteCreditoSalario() {
        return versaoLayoutLoteCreditoSalario;
    }

    public void setVersaoLayoutLoteCreditoSalario(String versaoLayoutLoteCreditoSalario) {
        this.versaoLayoutLoteCreditoSalario = versaoLayoutLoteCreditoSalario;
    }

    public String getIndicativoFormaPagamentoServ() {
        return indicativoFormaPagamentoServ;
    }

    public void setIndicativoFormaPagamentoServ(String indicativoFormaPagamentoServ) {
        this.indicativoFormaPagamentoServ = indicativoFormaPagamentoServ;
    }

    public String getCamaraCompensacao() {
        return camaraCompensacao;
    }

    public void setCamaraCompensacao(String camaraCompensacao) {
        this.camaraCompensacao = camaraCompensacao;
    }

    public String getTipoDeCompromisso() {
        return tipoDeCompromisso;
    }

    public void setTipoDeCompromisso(String tipoDeCompromisso) {
        this.tipoDeCompromisso = tipoDeCompromisso;
    }

    public String getCodigoDoCompromisso() {
        return codigoDoCompromisso;
    }

    public void setCodigoDoCompromisso(String codigoDoCompromisso) {
        this.codigoDoCompromisso = codigoDoCompromisso;
    }
}
