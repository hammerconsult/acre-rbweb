package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeferimentoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.CancelamentoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.enums.MotivoCancelamentoNota;
import br.com.webpublico.nfse.enums.TipoCancelamento;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Audited
@Etiqueta("Cancelamento de NFS-e")
@Table(name = "CANCDECLAPRESTACAOSERVICO")
public class CancelamentoDeclaracaoPrestacaoServico extends SuperEntidade implements Serializable, NfseEntity {

    private static final Logger log = LoggerFactory.getLogger(CancelamentoDeclaracaoPrestacaoServico.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LoteCancelamentoDeclaracaoPrestacaoServico lote;

    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Solicitação")
    private Date dataCancelamento;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCancelamento tipoCancelamento;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Solicitante")
    private String solicitante;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Motivo")
    @Enumerated(EnumType.STRING)
    private MotivoCancelamentoNota motivoCancelamento;

    @ManyToOne
    private UsuarioSistema fiscalResposavel;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação de Deferimento")
    private Situacao situacaoFiscal;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo Documento")
    private TipoDocumento tipoDocumento;

    @ManyToOne
    @NotAudited
    @Etiqueta("Tomador")
    private UsuarioWeb usuarioTomador;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação do Tomador")
    private Situacao situacaoTomador;

    @Etiqueta("Observação")
    private String observacoesCancelamento;

    @Etiqueta("Observação do Fiscal")
    private String observacoesFiscal;

    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Deferimento (Fiscal)")
    private Date dataDeferimentoFiscal;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Deferimento (Tomador)")
    private Date dataDeferimentoTomador;

    @ManyToOne
    private DeclaracaoPrestacaoServico declaracaoSubstituta;

    @ManyToOne
    private DeclaracaoPrestacaoServico declaracao;

    @Tabelavel
    @Etiqueta("Prestador")
    @Transient
    private String prestador;

    @Tabelavel
    @Etiqueta("N°")
    @Transient
    private Long numero;

    @Tabelavel
    @Etiqueta("Tomador")
    @Transient
    private String tomador;

    private Boolean visualizadoPeloContribuinte;

    public CancelamentoDeclaracaoPrestacaoServico() {
        super();
    }

    public CancelamentoDeclaracaoPrestacaoServico(CancelamentoDeclaracaoPrestacaoServico cancelamento, Long numero) {
        this();
        if (cancelamento != null) {
            this.id = cancelamento.getId();
            this.dataCancelamento = cancelamento.getDataCancelamento();
            this.tipoDocumento = cancelamento.getTipoDocumento();
            this.tipoCancelamento = cancelamento.getTipoCancelamento();
            this.solicitante = cancelamento.getSolicitante();
            this.motivoCancelamento = cancelamento.getMotivoCancelamento();
            this.fiscalResposavel = cancelamento.getFiscalResposavel();
            this.situacaoFiscal = cancelamento.getSituacaoFiscal();
            this.dataDeferimentoFiscal = cancelamento.getDataDeferimentoFiscal();
            this.observacoesFiscal = cancelamento.getObservacoesFiscal();
            this.numero = numero;
            this.prestador = cancelamento.getDeclaracao().getDadosPessoaisPrestador().toString();
            if (cancelamento.getDeclaracao().getDadosPessoaisTomador() != null &&
                !Strings.isNullOrEmpty(cancelamento.getDeclaracao().getDadosPessoaisTomador().getNomeRazaoSocial())) {
                this.tomador = cancelamento.getDeclaracao().getDadosPessoaisTomador().toString();
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteCancelamentoDeclaracaoPrestacaoServico getLote() {
        return lote;
    }

    public void setLote(LoteCancelamentoDeclaracaoPrestacaoServico lote) {
        this.lote = lote;
    }

    public TipoCancelamento getTipoCancelamento() {
        return tipoCancelamento;
    }

    public void setTipoCancelamento(TipoCancelamento tipoCancelamento) {
        this.tipoCancelamento = tipoCancelamento;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public MotivoCancelamentoNota getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(MotivoCancelamentoNota motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getObservacoesCancelamento() {
        return observacoesCancelamento;
    }

    public void setObservacoesCancelamento(String observacoesCancelamento) {
        this.observacoesCancelamento = observacoesCancelamento;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getObservacoesFiscal() {
        return observacoesFiscal;
    }

    public void setObservacoesFiscal(String observacoesFiscal) {
        this.observacoesFiscal = observacoesFiscal;
    }

    public UsuarioSistema getFiscalResposavel() {
        return fiscalResposavel;
    }

    public void setFiscalResposavel(UsuarioSistema fiscalResposavel) {
        this.fiscalResposavel = fiscalResposavel;
    }

    public Situacao getSituacaoFiscal() {
        return situacaoFiscal;
    }

    public void setSituacaoFiscal(Situacao situacaoFiscal) {
        this.situacaoFiscal = situacaoFiscal;
    }

    public Date getDataDeferimentoFiscal() {
        return dataDeferimentoFiscal;
    }

    public void setDataDeferimentoFiscal(Date dataDeferimentoFiscal) {
        this.dataDeferimentoFiscal = dataDeferimentoFiscal;
    }

    public DeclaracaoPrestacaoServico getDeclaracaoSubstituta() {
        return declaracaoSubstituta;
    }

    public void setDeclaracaoSubstituta(DeclaracaoPrestacaoServico declaracaoSubstituta) {
        this.declaracaoSubstituta = declaracaoSubstituta;
    }

    public UsuarioWeb getUsuarioTomador() {
        return usuarioTomador;
    }

    public void setUsuarioTomador(UsuarioWeb usuarioTomador) {
        this.usuarioTomador = usuarioTomador;
    }

    public Situacao getSituacaoTomador() {
        return situacaoTomador;
    }

    public void setSituacaoTomador(Situacao situacaoTomador) {
        this.situacaoTomador = situacaoTomador;
    }

    public Date getDataDeferimentoTomador() {
        return dataDeferimentoTomador;
    }

    public void setDataDeferimentoTomador(Date dataDeferimentoTomador) {
        this.dataDeferimentoTomador = dataDeferimentoTomador;
    }

    public String getPrestador() {
        return prestador;
    }

    public void setPrestador(String prestador) {
        this.prestador = prestador;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getTomador() {
        return tomador;
    }

    public void setTomador(String tomador) {
        this.tomador = tomador;
    }

    public DeclaracaoPrestacaoServico getDeclaracao() {
        return declaracao;
    }

    public void setDeclaracao(DeclaracaoPrestacaoServico declaracao) {
        this.declaracao = declaracao;
    }

    public Boolean getVisualizadoPeloContribuinte() {
        return visualizadoPeloContribuinte;
    }

    public void setVisualizadoPeloContribuinte(Boolean visualizadoPeloContribuinte) {
        this.visualizadoPeloContribuinte = visualizadoPeloContribuinte;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @Override
    public CancelamentoNfseDTO toNfseDto() {
        CancelamentoNfseDTO dto = new CancelamentoNfseDTO();
        dto.setId(this.id);
        dto.setDataCancelamento(this.dataCancelamento);
        dto.setMotivoCancelamento(this.motivoCancelamento.toNfseDto());
        dto.setObservacoesCancelamento(this.observacoesCancelamento);
        dto.setSituacaoFiscal(this.situacaoFiscal != null ? this.situacaoFiscal.getSituacaoNfseDTO() : null);
        dto.setSituacaoTomador(this.situacaoTomador != null ? this.situacaoTomador.getSituacaoNfseDTO() : null);
        return dto;
    }

    public enum Situacao implements EnumComDescricao {
        DEFERIDO("Deferido", SituacaoDeferimentoNfseDTO.DEFERIDO),
        NAO_DEFERIDO("Não Deferido", SituacaoDeferimentoNfseDTO.NAO_DEFERIDO),
        EM_ANALISE("Em análise", SituacaoDeferimentoNfseDTO.EM_ANALISE);

        private String descricao;
        private SituacaoDeferimentoNfseDTO situacaoNfseDTO;

        Situacao(String descricao, SituacaoDeferimentoNfseDTO situacaoNfseDTO) {
            this.descricao = descricao;
            this.situacaoNfseDTO = situacaoNfseDTO;
        }

        public String getDescricao() {
            return descricao;
        }

        public SituacaoDeferimentoNfseDTO getSituacaoNfseDTO() {
            return situacaoNfseDTO;
        }
    }

    public enum TipoDocumento {
        NOTA_FISCAL("Nota Fiscal"),
        SERVICO_DECLARADO("Serviço Declarado");

        private String descricao;

        TipoDocumento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
