
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.SituacaoSolicitacaoITBI;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class SolicitacaoItbiOnline extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    private Long codigo;
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    @Enumerated(EnumType.STRING)
    private TipoITBI tipoITBI;
    @Enumerated(EnumType.STRING)
    private SituacaoSolicitacaoITBI situacao;
    @Temporal(TemporalType.DATE)
    private Date dataDesignacao;
    @ManyToOne
    private UsuarioSistema auditorFiscal;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @ManyToOne
    private CadastroRural cadastroRural;
    private String observacao;
    @OneToMany(mappedBy = "solicitacaoItbiOnline", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoSolicitacaoItbiOnline> calculos;
    @OneToMany(mappedBy = "solicitacaoItbiOnline", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoItbiOnlineDocumento> documentos;
    @ManyToOne
    private UsuarioWeb usuarioWeb;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @OneToOne
    private Arquivo planilhaAvaliacao;
    private BigDecimal valorAvaliado;
    private Boolean procurador;

    public SolicitacaoItbiOnline() {
        super();
        this.calculos = Lists.newArrayList();
        this.documentos = Lists.newArrayList();
        this.procurador = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public TipoITBI getTipoITBI() {
        return tipoITBI;
    }

    public void setTipoITBI(TipoITBI tipoITBI) {
        this.tipoITBI = tipoITBI;
    }

    public SituacaoSolicitacaoITBI getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoITBI situacao) {
        this.situacao = situacao;
    }

    public Date getDataDesignacao() {
        return dataDesignacao;
    }

    public void setDataDesignacao(Date dataDesignacao) {
        this.dataDesignacao = dataDesignacao;
    }

    public UsuarioSistema getAuditorFiscal() {
        return auditorFiscal;
    }

    public void setAuditorFiscal(UsuarioSistema auditorFiscal) {
        this.auditorFiscal = auditorFiscal;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public CadastroRural getCadastroRural() {
        return cadastroRural;
    }

    public void setCadastroRural(CadastroRural cadastroRural) {
        this.cadastroRural = cadastroRural;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<CalculoSolicitacaoItbiOnline> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoSolicitacaoItbiOnline> calculos) {
        this.calculos = calculos;
    }

    public Cadastro getCadastro() {
        return cadastroImobiliario != null ? cadastroImobiliario : (cadastroRural != null ? cadastroRural : null);
    }

    public Long getIdCadastro() {
        return getCadastro() != null ? getCadastro().getId() : null;
    }

    public String getInscricao() {
        return cadastroImobiliario != null ? cadastroImobiliario.getInscricaoCadastral() :
            (cadastroRural != null ? cadastroRural.getNumeroCadastro() : "");
    }

    public boolean isImobiliario() {
        return cadastroImobiliario != null;
    }

    public CalculoSolicitacaoItbiOnline getUltimoCalculo() {
        if (calculos != null && !calculos.isEmpty()) {
            ordenarCalculos(true);
            return calculos.get(0);
        }
        return null;
    }

    private void ordenarCalculos(Boolean desc) {
        calculos.sort(new Comparator<CalculoSolicitacaoItbiOnline>() {
            @Override
            public int compare(CalculoSolicitacaoItbiOnline c1, CalculoSolicitacaoItbiOnline c2) {
                return desc
                    ? c2.getOrdemCalculo().compareTo(c1.getOrdemCalculo())
                    : c1.getOrdemCalculo().compareTo(c2.getOrdemCalculo());
            }
        });
    }

    public List<CalculoSolicitacaoItbiOnline> getCalculosOrdenados(Boolean desc) {
        ordenarCalculos(desc);
        return calculos;
    }

    public List<SolicitacaoItbiOnlineDocumento> getDocumentos() {
        if (documentos != null) documentos.sort((o1, o2) -> o1.getDataRegistro().compareTo(o2.getDataRegistro()));
        return documentos;
    }

    public void setDocumentos(List<SolicitacaoItbiOnlineDocumento> documentos) {
        this.documentos = documentos;
    }

    public UsuarioWeb getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Arquivo getPlanilhaAvaliacao() {
        return planilhaAvaliacao;
    }

    public void setPlanilhaAvaliacao(Arquivo planilhaAvaliacao) {
        this.planilhaAvaliacao = planilhaAvaliacao;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    public Boolean getProcurador() {
        return procurador;
    }

    public void setProcurador(Boolean procurador) {
        this.procurador = procurador;
    }

    public void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (tipoITBI == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de ITBI deve ser informado.");
        } else {
            String tipoCadastro = "";
            if (TipoITBI.RURAL.equals(tipoITBI)) {
                tipoCadastro = "Rural";
            } else {
                tipoCadastro = "Imobiliário";
            }
            if (cadastroImobiliario == null && cadastroRural == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro " + tipoCadastro + " deve ser informado.");
            }
        }
        if (calculos.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo menos uma Transmissão ao processo.");
        }
        if (documentos != null && !documentos.isEmpty()) {
            for (SolicitacaoItbiOnlineDocumento solicitacaoItbiOnlineDocumento : documentos) {
                if (solicitacaoItbiOnlineDocumento.getParametrosITBIDocumento().getObrigatorio()
                    && solicitacaoItbiOnlineDocumento.getDocumento() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O documento " +
                        solicitacaoItbiOnlineDocumento.getDescricao() + " deve ser informado.");
                }
            }
        }
        ve.lancarException();
    }

    public boolean isEmAnalise() {
        return SituacaoSolicitacaoITBI.EM_ANALISE.equals(situacao);
    }

    public boolean isAprovada() {
        return SituacaoSolicitacaoITBI.APROVADA.equals(situacao);
    }

    public boolean isDesignada() {
        return SituacaoSolicitacaoITBI.DESIGNADA.equals(situacao);
    }

    public boolean isDeferida() {
        return SituacaoSolicitacaoITBI.DEFERIDA.equals(situacao);
    }

    public boolean isHomologada() {
        return SituacaoSolicitacaoITBI.HOMOLOGADA.equals(situacao);
    }

    public Integer quantidadeParcelaCalculo() {
        return calculos.get(0).getQuantidadeParcelas();
    }

    public boolean calculoIsento() {
        return calculos.get(0).getIsentoSub();
    }

    @Override
    public String toString() {
        return exercicio.getAno() + "/" + codigo;
    }
}
