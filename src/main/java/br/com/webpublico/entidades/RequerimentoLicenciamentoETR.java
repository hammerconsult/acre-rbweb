package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.entidades.comum.RespostaFormulario;
import br.com.webpublico.entidades.comum.RespostaFormularioCampo;
import br.com.webpublico.enums.SituacaoLicenciamentoETR;
import br.com.webpublico.enums.SolicitacaoLicenciamentoETR;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class RequerimentoLicenciamentoETR extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    private Long codigo;
    @Temporal(TemporalType.DATE)
    private Date dataRequerimento;
    private String numeroProtocolo;
    private Integer anoProtocolo;
    @Enumerated(EnumType.STRING)
    private SituacaoLicenciamentoETR situacao;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    private String email;
    @Enumerated(EnumType.STRING)
    private SolicitacaoLicenciamentoETR solicitacao;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @OneToOne
    private EnderecoCorreio enderecoInstalacao;
    @OneToMany(mappedBy = "requerimentoLicenciamentoETR")
    private List<RequerimentoLicenciamentoETRRespostaFormulario> requerimentoLicenciamentoETRRespostaFormularioList;
    @OneToMany(mappedBy = "requerimentoLicenciamentoETR")
    private List<ExigenciaETR> exigenciaETRList;
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;
    @ManyToOne
    private UsuarioSistema usuarioEfetivacao;
    private String justificativa;
    private Boolean dispensaAlvara;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    private String telefone;
    private String responsavel;
    @OneToMany(mappedBy = "requerimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataRegistro")
    private List<RequerimentoLicenciamentoETRHistorico> historicoList;
    @OneToMany(mappedBy = "requerimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequerimentoLicenciamentoETRAceite> aceiteList;

    public RequerimentoLicenciamentoETR() {
        super();
        dispensaAlvara = Boolean.FALSE;
        historicoList = Lists.newArrayList();
        aceiteList = Lists.newArrayList();
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

    public Date getDataRequerimento() {
        return dataRequerimento;
    }

    public void setDataRequerimento(Date dataRequerimento) {
        this.dataRequerimento = dataRequerimento;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public String getNumeroAnoProtocolo() {
        return numeroProtocolo + "/" + anoProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Integer getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(Integer anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public SituacaoLicenciamentoETR getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLicenciamentoETR situacao) {
        this.situacao = situacao;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SolicitacaoLicenciamentoETR getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoLicenciamentoETR solicitacao) {
        this.solicitacao = solicitacao;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public EnderecoCorreio getEnderecoInstalacao() {
        return enderecoInstalacao;
    }

    public void setEnderecoInstalacao(EnderecoCorreio enderecoInstalacao) {
        this.enderecoInstalacao = enderecoInstalacao;
    }

    public List<RequerimentoLicenciamentoETRRespostaFormulario> getRequerimentoLicenciamentoETRRespostaFormularioList() {
        if (requerimentoLicenciamentoETRRespostaFormularioList != null) {
            Collections.sort(requerimentoLicenciamentoETRRespostaFormularioList);
        }
        return requerimentoLicenciamentoETRRespostaFormularioList;
    }

    public void setRequerimentoLicenciamentoETRRespostaFormularioList(List<RequerimentoLicenciamentoETRRespostaFormulario> requerimentoLicenciamentoETRRespostaFormularioList) {
        this.requerimentoLicenciamentoETRRespostaFormularioList = requerimentoLicenciamentoETRRespostaFormularioList;
    }

    public List<ExigenciaETR> getExigenciaETRList() {
        return exigenciaETRList;
    }

    public void setExigenciaETRList(List<ExigenciaETR> exigenciaETRList) {
        this.exigenciaETRList = exigenciaETRList;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getUsuarioEfetivacao() {
        return usuarioEfetivacao;
    }

    public void setUsuarioEfetivacao(UsuarioSistema usuarioEfetivacao) {
        this.usuarioEfetivacao = usuarioEfetivacao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Boolean getDispensaAlvara() {
        return dispensaAlvara;
    }

    public void setDispensaAlvara(Boolean dispensaAlvara) {
        this.dispensaAlvara = dispensaAlvara;
    }

    public RespostaFormularioCampo buscarRespostaFormularioCampo(FormularioCampo formularioCampo) {
        if (requerimentoLicenciamentoETRRespostaFormularioList != null) {
            for (RequerimentoLicenciamentoETRRespostaFormulario requerimentoLicenciamentoETRRespostaFormulario : requerimentoLicenciamentoETRRespostaFormularioList) {
                RespostaFormulario respostaFormulario = requerimentoLicenciamentoETRRespostaFormulario.getRespostaFormulario();
                for (RespostaFormularioCampo respostaFormularioCampo : respostaFormulario.getRespostaFormularioCampoList()) {
                    if (respostaFormularioCampo.getFormularioCampo().equals(formularioCampo)) {
                        return respostaFormularioCampo;
                    }
                }
            }
        }
        return null;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public List<RequerimentoLicenciamentoETRHistorico> getHistoricoList() {
        return historicoList;
    }

    public void setHistoricoList(List<RequerimentoLicenciamentoETRHistorico> historicoList) {
        this.historicoList = historicoList;
    }

    public List<RequerimentoLicenciamentoETRAceite> getAceiteList() {
        return aceiteList;
    }

    public void setAceiteList(List<RequerimentoLicenciamentoETRAceite> aceiteList) {
        this.aceiteList = aceiteList;
    }

    public void adicionarHistorico(Date dataRegistro, SituacaoLicenciamentoETR situacao) {
        if (historicoList == null) {
            historicoList = Lists.newArrayList();
        }
        RequerimentoLicenciamentoETRHistorico historico = new RequerimentoLicenciamentoETRHistorico();
        historico.setRequerimento(this);
        historico.setDataRegistro(dataRegistro);
        historico.setSituacao(situacao);
        this.situacao = situacao;
        this.historicoList.add(historico);
    }

    public RequerimentoLicenciamentoETRAceite getAceite(UnidadeOrganizacional unidadeOrganizacional) {
        if (aceiteList != null) {
            for (RequerimentoLicenciamentoETRAceite requerimentoLicenciamentoETRAceite : aceiteList) {
                if (requerimentoLicenciamentoETRAceite.getUnidadeAceite().equals(unidadeOrganizacional))
                    return requerimentoLicenciamentoETRAceite;
            }
        }
        return null;
    }
}
