/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
public class RescisaoContrato extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Rescisão")
    private Date rescindidoEm;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Responsável Unidade Administrativa")
    private ContratoFP responsavelPrefeitura;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Responsável Fornecedor")
    private PessoaFisica responsavelEmpresa;

    @Etiqueta("Motivo da Rescisão")
    @Obrigatorio
    private String motivoRescisao;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Publicação")
    private Date publicadoEm;

    @ManyToOne
    @Etiqueta("Veículo de Publicação")
    @Obrigatorio
    private VeiculoDePublicacao veiculoDePublicacao;

    @Obrigatorio
    @Etiqueta("Número da Edição")
    private String numeroEdicao;

    @Etiqueta("Número da Página")
    @Obrigatorio
    private String numeroPagina;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Date getRescindidoEm() {
        return rescindidoEm;
    }

    public void setRescindidoEm(Date rescindidoEm) {
        this.rescindidoEm = rescindidoEm;
    }

    public ContratoFP getResponsavelPrefeitura() {
        return responsavelPrefeitura;
    }

    public void setResponsavelPrefeitura(ContratoFP responsavelPrefeitura) {
        this.responsavelPrefeitura = responsavelPrefeitura;
    }

    public PessoaFisica getResponsavelEmpresa() {
        return responsavelEmpresa;
    }

    public void setResponsavelEmpresa(PessoaFisica responsavelEmpresa) {
        this.responsavelEmpresa = responsavelEmpresa;
    }

    public String getMotivoRescisao() {
        return motivoRescisao;
    }

    public void setMotivoRescisao(String motivoRescisao) {
        this.motivoRescisao = motivoRescisao;
    }

    public Date getPublicadoEm() {
        return publicadoEm;
    }

    public void setPublicadoEm(Date publicadoEm) {
        this.publicadoEm = publicadoEm;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public String getNumeroEdicao() {
        return numeroEdicao;
    }

    public void setNumeroEdicao(String numeroEdicao) {
        this.numeroEdicao = numeroEdicao;
    }

    public String getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(String numeroPagina) {
        this.numeroPagina = numeroPagina;
    }
}
