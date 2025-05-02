/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.enums.administrativo.frotas.SituacaoSolicitacaoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Solicitação")
public class SolicitacaoObjetoFrota extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Objeto")
    @Enumerated(EnumType.STRING)
    private TipoObjetoFrota tipoObjetoFrota;

    @Pesquisavel
    @Etiqueta("Unidade Gestora da Frota")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Gestora da Frota")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Pesquisavel
    @Etiqueta("Unidade Solicitante")
    @Transient
    private HierarquiaOrganizacional hierarquiaSolicitante;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Solicitante")
    private UnidadeOrganizacional unidadeSolicitante;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Realizada em")
    @Obrigatorio
    private Date realizadaEm;

    @OneToOne
    @Tabelavel
    @Etiqueta("Solicitante")
    @Obrigatorio
    private PessoaFisica pessoaFisica;

    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Retirada")
    @Obrigatorio
    private Date dataRetirada;

    @Length(maximo = 3000)
    @Etiqueta("Observações")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    @Pesquisavel
    @Tabelavel
    private SituacaoSolicitacaoObjetoFrota situacao;

    @Length(maximo = 3000)
    @Etiqueta("Motivo")
    private String motivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data de Devolução")
    private Date dataDevolucao;

    @ManyToOne
    private UsuarioSistema usuarioCadastro;

    public SolicitacaoObjetoFrota() {
        super();
        situacao = SituacaoSolicitacaoObjetoFrota.AGUARDANDO_RESERVA;
    }

    public SolicitacaoObjetoFrota(SolicitacaoObjetoFrota solicitacaoObjetoFrota, HierarquiaOrganizacional unidadeSolicitante) {
        this.setId(solicitacaoObjetoFrota.getId());
        this.setDataDevolucao(solicitacaoObjetoFrota.getDataDevolucao());
        this.setDataRetirada(solicitacaoObjetoFrota.getDataRetirada());
        this.setHierarquiaSolicitante(unidadeSolicitante);
        this.setPessoaFisica(solicitacaoObjetoFrota.getPessoaFisica());
        this.setSituacao(solicitacaoObjetoFrota.getSituacao());
        this.setMotivo(solicitacaoObjetoFrota.getMotivo());
        this.setUsuarioCadastro(solicitacaoObjetoFrota.getUsuarioCadastro());
        this.setRealizadaEm(solicitacaoObjetoFrota.getRealizadaEm());
        this.setTipoObjetoFrota(solicitacaoObjetoFrota.getTipoObjetoFrota());
    }

    public UnidadeOrganizacional getUnidadeSolicitante() {
        return unidadeSolicitante;
    }

    public void setUnidadeSolicitante(UnidadeOrganizacional unidadeSolicitante) {
        this.unidadeSolicitante = unidadeSolicitante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Date getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(Date dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Date getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Date realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            this.unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaSolicitante() {
        return hierarquiaSolicitante;
    }

    public void setHierarquiaSolicitante(HierarquiaOrganizacional hierarquiaSolicitante) {
        if (hierarquiaSolicitante != null) {
            this.unidadeSolicitante = hierarquiaSolicitante.getSubordinada();
        }
        this.hierarquiaSolicitante = hierarquiaSolicitante;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(UsuarioSistema usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public SituacaoSolicitacaoObjetoFrota getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoObjetoFrota situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        try {
            return "Realizada por " + pessoaFisica.getNome() + " em " + DataUtil.getDataFormatada(this.realizadaEm);
        } catch (NullPointerException ne) {
            return "";
        }
    }

    public String getLabelCampoTipoOjeto() {
        if (tipoObjetoFrota != null) {
            if (tipoObjetoFrota.isVeiculo()) {
                return "Veículo";
            }
            if (tipoObjetoFrota.isEquipamento()) {
                return "Equipamento/Máquina";
            }
        }
        return "";
    }
}
