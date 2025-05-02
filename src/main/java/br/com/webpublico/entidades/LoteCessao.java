package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLoteCessao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 09/05/14
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Solicitação de Cessão de Bens")
public class LoteCessao extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CodigoGeradoAoConcluir
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data/Hora de Criação")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHoraCriacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    @Etiqueta("Tipo de Cessão")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCessao tipoCessao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private SituacaoLoteCessao situacaoLoteCessao;

    @Obrigatorio
    @Etiqueta("Unidade de Administrativa Origem")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrigem;

    @Tabelavel
    @Transient
    @Etiqueta("Unidade de Origem")
    private String unidadeAdmOrigem;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Responsável de Origem")
    @ManyToOne
    private PessoaFisica responsavelPeloEnvio;

    @Etiqueta("Unidade Administrativa de Destino")
    @ManyToOne
    private UnidadeOrganizacional unidadeDestino;

    @Tabelavel
    @Transient
    @Etiqueta("Unidade de Destino")
    private String unidadeAdmDestino;

    @Tabelavel
    @Etiqueta("Responsável de Destino")
    @ManyToOne
    private PessoaFisica responsavelPeloRecebimento;

    @Etiqueta("Destino Externo")
    @Tabelavel
    @Pesquisavel
    private String unidadeExterna;

    @Etiqueta("Responsável do Destino Externo")
    @Tabelavel
    @Pesquisavel
    private String responsavelExterno;

    @Etiqueta("Motivo da Recusa")
    private String motivoRecusa;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "loteCessao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Cessao> listaDeCessoes;

    @Invisivel
    @OneToMany(mappedBy = "loteCessao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PrazoCessao> listaDePrazos;

    @Invisivel
    @OneToMany(mappedBy = "loteCessao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SolicitacaoProrrogacaoCessao> listaDeSolicitacaoProrrogacaoCessao;

    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    public LoteCessao() {
        super();
        this.listaDeCessoes = new ArrayList<>();
        this.listaDePrazos = new ArrayList<>();
        this.listaDeSolicitacaoProrrogacaoCessao = new ArrayList<>();
        this.situacaoLoteCessao = SituacaoLoteCessao.EM_ELABORACAO;
        this.tipoCessao = TipoCessao.INTERNO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PessoaFisica getResponsavelPeloEnvio() {
        return responsavelPeloEnvio;
    }

    public void setResponsavelPeloEnvio(PessoaFisica responsavelPeloEnvio) {
        this.responsavelPeloEnvio = responsavelPeloEnvio;
    }

    public PessoaFisica getResponsavelPeloRecebimento() {
        return responsavelPeloRecebimento;
    }

    public void setResponsavelPeloRecebimento(PessoaFisica responsavelPeloRecebimento) {
        this.responsavelPeloRecebimento = responsavelPeloRecebimento;
    }

    public UnidadeOrganizacional getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(UnidadeOrganizacional unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public UnidadeOrganizacional getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(UnidadeOrganizacional unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public List<Cessao> getListaDeCessoes() {
        if (listaDeCessoes != null) {
            Collections.sort(listaDeCessoes);
        }
        return listaDeCessoes;
    }

    public void setListaDeCessoes(List<Cessao> listaDeCessoes) {
        this.listaDeCessoes = listaDeCessoes;
    }

    public List<PrazoCessao> getListaDePrazos() {
        return listaDePrazos;
    }

    public void setListaDePrazos(List<PrazoCessao> listaDePrazos) {
        this.listaDePrazos = listaDePrazos;
    }

    public List<SolicitacaoProrrogacaoCessao> getListaDeSolicitacaoProrrogacaoCessao() {
        return listaDeSolicitacaoProrrogacaoCessao;
    }

    public void setListaDeSolicitacaoProrrogacaoCessao(List<SolicitacaoProrrogacaoCessao> listaDeSolicitacaoProrrogacaoCessao) {
        this.listaDeSolicitacaoProrrogacaoCessao = listaDeSolicitacaoProrrogacaoCessao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public SituacaoLoteCessao getSituacaoLoteCessao() {
        return situacaoLoteCessao;
    }

    public void setSituacaoLoteCessao(SituacaoLoteCessao situacaoLoteCessao) {
        this.situacaoLoteCessao = situacaoLoteCessao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public TipoCessao getTipoCessao() {
        return tipoCessao;
    }

    public void setTipoCessao(TipoCessao tipoCessao) {
        this.tipoCessao = tipoCessao;
    }

    public String getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(String unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    public String getResponsavelExterno() {
        return responsavelExterno;
    }

    public void setResponsavelExterno(String responsavelExterno) {
        this.responsavelExterno = responsavelExterno;
    }

    public String getUnidadeAdmOrigem() {
        return unidadeAdmOrigem;
    }

    public void setUnidadeAdmOrigem(String unidadeAdmOrigem) {
        this.unidadeAdmOrigem = unidadeAdmOrigem;
    }

    public String getUnidadeAdmDestino() {
        return unidadeAdmDestino;
    }

    public void setUnidadeAdmDestino(String unidadeAdmDestino) {
        this.unidadeAdmDestino = unidadeAdmDestino;
    }

    @Override
    public String toString() {
        try {
            return this.codigo + " - " + this.descricao;
        } catch (NullPointerException ex) {
            return "";
        }
    }


    public Boolean foiRecusado() {
        return SituacaoLoteCessao.RECUSADA.equals(this.situacaoLoteCessao);
    }

    public Boolean emElaboracao() {
        return SituacaoLoteCessao.EM_ELABORACAO.equals(this.situacaoLoteCessao);
    }

    public Boolean foiAceita() {
        return SituacaoLoteCessao.ACEITA.equals(this.situacaoLoteCessao);
    }

    public Boolean aguardandoEfetivacao() {
        return SituacaoLoteCessao.AGUARDANDO_EFETIVACAO.equals(this.situacaoLoteCessao);
    }


    public PrazoCessao getPrimeiroPrazoCessao() {
        Collections.sort(listaDePrazos);
        return listaDePrazos.get(0);
    }

    public PrazoCessao getUltimoPrazoCessao() {
        for (PrazoCessao prazoCessao : getListaDePrazos()) {
            if (prazoCessao.getProrrogacaoCessao() == null) {
                return prazoCessao;
            }
        }
        return new PrazoCessao(this);
    }

    public String etiquetaAbaBem() {
        if (foiRecusado()) {
            return "Bens Recusados";
        }
        if (emElaboracao()) {
            return "Bens para Cessão";
        }
        if (foiAceita()) {
            return "Bens Cedidos";
        }
        if (aguardandoEfetivacao()) {
            return "Bens Aguardando Efetivação";
        }
        return "Bens";
    }

    public Boolean isExterno() {
        return TipoCessao.EXTERNO.equals(this.tipoCessao);
    }

    public Boolean isInterno() {
        return TipoCessao.INTERNO.equals(this.tipoCessao);
    }
}
