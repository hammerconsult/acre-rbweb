package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.tributario.OrigemProcessoLicenciamentoAmbiental;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ProcessoLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    private Long sequencial;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataProcesso;
    private String numeroProtocolo;
    private String anoProtocolo;
    @Enumerated(EnumType.STRING)
    private OrigemProcessoLicenciamentoAmbiental origemProcesso;
    @ManyToOne
    private AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental;
    @OneToOne(cascade = CascadeType.ALL)
    private ProcessoLicenciamentoAmbientalPessoa requerente;
    @OneToOne(cascade = CascadeType.ALL)
    private ProcessoLicenciamentoAmbientalPessoa representante;
    @OneToOne(cascade = CascadeType.ALL)
    private ProcessoLicenciamentoAmbientalPessoa procurador;
    private String numeroDocumento;
    private String descricao;
    @OneToMany(mappedBy = "processoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoLicenciamentoAmbientalDocumento> documentos;
    @OneToMany(mappedBy = "processoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoLicenciamentoAmbientalCNAE> cnaes;
    private String descricaoAtividade;
    @OneToMany(mappedBy = "processoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoLicenciamentoAmbientalUnidadeMedida> processoUnidadeMedidas;
    @Enumerated(EnumType.STRING)
    private StatusLicenciamentoAmbiental status;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoLicenciamentoAmbiental processoCalculoLicenciamentoAmbiental;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoLicenciamentoAmbiental processoCalculoTaxaExpediente;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processoLicenciamentoAmbiental", orphanRemoval = true)
    private List<HistoricoProcessoLicenciamentoAmbiental> historicosAlteracoes;
    @OneToMany(mappedBy = "processoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TecnicoProcessoLicenciamentoAmbiental> tecnicos;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    private String observacao;
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Secretário")
    private AssinaturaSecretarioLicenciamentoAmbiental assinaturaSecretario;
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Diretor")
    private AssinaturaSecretarioLicenciamentoAmbiental assinaturaDiretor;

    public ProcessoLicenciamentoAmbiental() {
        super();
        this.dataProcesso = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencial() {
        return sequencial;
    }

    public void setSequencial(Long sequencial) {
        this.sequencial = sequencial;
    }

    public OrigemProcessoLicenciamentoAmbiental getOrigemProcesso() {
        return origemProcesso;
    }

    public void setOrigemProcesso(OrigemProcessoLicenciamentoAmbiental origemProcesso) {
        this.origemProcesso = origemProcesso;
    }

    public AssuntoLicenciamentoAmbiental getAssuntoLicenciamentoAmbiental() {
        return assuntoLicenciamentoAmbiental;
    }

    public void setAssuntoLicenciamentoAmbiental(AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental) {
        this.assuntoLicenciamentoAmbiental = assuntoLicenciamentoAmbiental;
    }

    public ProcessoLicenciamentoAmbientalPessoa getRequerente() {
        return requerente;
    }

    public void setRequerente(ProcessoLicenciamentoAmbientalPessoa requerente) {
        this.requerente = requerente;
    }

    public ProcessoLicenciamentoAmbientalPessoa getRepresentante() {
        return representante;
    }

    public void setRepresentante(ProcessoLicenciamentoAmbientalPessoa representante) {
        this.representante = representante;
    }

    public ProcessoLicenciamentoAmbientalPessoa getProcurador() {
        return procurador;
    }

    public void setProcurador(ProcessoLicenciamentoAmbientalPessoa procurador) {
        this.procurador = procurador;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ProcessoLicenciamentoAmbientalDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<ProcessoLicenciamentoAmbientalDocumento> documentos) {
        this.documentos = documentos;
    }

    public List<ProcessoLicenciamentoAmbientalCNAE> getCnaes() {
        if (cnaes == null) cnaes = Lists.newArrayList();
        return cnaes;
    }

    public void setCnaes(List<ProcessoLicenciamentoAmbientalCNAE> cnaes) {
        this.cnaes = cnaes;
    }

    public String getDescricaoAtividade() {
        return descricaoAtividade;
    }

    public void setDescricaoAtividade(String descricaoAtividade) {
        this.descricaoAtividade = descricaoAtividade;
    }

    public Date getDataProcesso() {
        return dataProcesso;
    }

    public void setDataProcesso(Date dataProcesso) {
        this.dataProcesso = dataProcesso;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<HistoricoProcessoLicenciamentoAmbiental> getHistoricosAlteracoes() {
        if (historicosAlteracoes == null) historicosAlteracoes = Lists.newArrayList();
        return historicosAlteracoes;
    }

    public void setHistoricosAlteracoes(List<HistoricoProcessoLicenciamentoAmbiental> historicosAlteracoes) {
        this.historicosAlteracoes = historicosAlteracoes;
    }

    public AssinaturaSecretarioLicenciamentoAmbiental getAssinaturaSecretario() {
        return assinaturaSecretario;
    }

    public void setAssinaturaSecretario(AssinaturaSecretarioLicenciamentoAmbiental assinaturaSecretario) {
        this.assinaturaSecretario = assinaturaSecretario;
    }

    public AssinaturaSecretarioLicenciamentoAmbiental getAssinaturaDiretor() {
        return assinaturaDiretor;
    }

    public void setAssinaturaDiretor(AssinaturaSecretarioLicenciamentoAmbiental assinaturaDiretor) {
        this.assinaturaDiretor = assinaturaDiretor;
    }

    public Boolean isOnline() {
        return OrigemProcessoLicenciamentoAmbiental.ONLINE.equals(origemProcesso);
    }

    public Boolean isRedeSim() {
        return OrigemProcessoLicenciamentoAmbiental.REDE_SIM.equals(origemProcesso);
    }

    public Boolean isCac() {
        return OrigemProcessoLicenciamentoAmbiental.CAC.equals(origemProcesso);
    }

    public Boolean isRbDoc() {
        return OrigemProcessoLicenciamentoAmbiental.RBDOC.equals(origemProcesso);
    }

    public Boolean isEmailInstitucional() {
        return OrigemProcessoLicenciamentoAmbiental.EMAIL_INSTITUCIONAL.equals(origemProcesso);
    }

    public List<ProcessoLicenciamentoAmbientalUnidadeMedida> getProcessoUnidadeMedidas() {
        if (processoUnidadeMedidas == null) processoUnidadeMedidas = Lists.newArrayList();
        return processoUnidadeMedidas;
    }

    public void setProcessoUnidadeMedidas(List<ProcessoLicenciamentoAmbientalUnidadeMedida> processoUnidadeMedidas) {
        this.processoUnidadeMedidas = processoUnidadeMedidas;
    }

    public Boolean isRecepcao() {
        return OrigemProcessoLicenciamentoAmbiental.RECEPCAO.equals(origemProcesso);
    }

    public Boolean isViaBanca() {
        return OrigemProcessoLicenciamentoAmbiental.VIA_BIANCA.equals(origemProcesso);
    }

    public String getTextoNumeroDocumento() {
        if (isCac()) {
            return "Nº Protocolo:";
        }
        return "";
    }

    public String getTextoRequerente() {
        if (isRedeSim()) {
            return "Empresa";
        }
        return "Requerente";
    }

    public StatusLicenciamentoAmbiental getStatus() {
        return status;
    }

    public void setStatus(StatusLicenciamentoAmbiental status) {
        this.status = status;
    }

    public ProcessoCalculoLicenciamentoAmbiental getProcessoCalculoLicenciamentoAmbiental() {
        return processoCalculoLicenciamentoAmbiental;
    }

    public void setProcessoCalculoLicenciamentoAmbiental(ProcessoCalculoLicenciamentoAmbiental processoCalculoLicenciamentoAmbiental) {
        this.processoCalculoLicenciamentoAmbiental = processoCalculoLicenciamentoAmbiental;
    }

    public ProcessoCalculoLicenciamentoAmbiental getProcessoCalculoTaxaExpediente() {
        return processoCalculoTaxaExpediente;
    }

    public void setProcessoCalculoTaxaExpediente(ProcessoCalculoLicenciamentoAmbiental processoCalculoTaxaExpediente) {
        this.processoCalculoTaxaExpediente = processoCalculoTaxaExpediente;
    }

    public List<TecnicoProcessoLicenciamentoAmbiental> getTecnicos() {
        if (tecnicos == null) tecnicos = Lists.newArrayList();
        return tecnicos;
    }

    public void setTecnicos(List<TecnicoProcessoLicenciamentoAmbiental> tecnicos) {
        this.tecnicos = tecnicos;
    }

    @Override
    public void validarCamposObrigatorios() {
        if (origemProcesso == null) {
            throw new ValidacaoException("O campo Origem do Processo deve ser informado.");
        }
        ValidacaoException ve = new ValidacaoException();
        if (assuntoLicenciamentoAmbiental == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Assunto deve ser informado.");
        }
        if ((isCac()) && (Strings.isNullOrEmpty(numeroProtocolo) || Strings.isNullOrEmpty(anoProtocolo))) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número/Ano do Protocolo deve ser informado.");
        }
        if ((isRbDoc()) && Strings.isNullOrEmpty(numeroDocumento)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Nº Documento deve ser informado.");
        }
        if (documentos != null && !documentos.isEmpty()) {
            for (ProcessoLicenciamentoAmbientalDocumento docProcesso : documentos) {
                if (docProcesso.getDocumento() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O Documento " + docProcesso.getDocumentoLicenciamentoAmbiental().getDescricaoReduzida() + " deve ter um arquivo anexado.");
                }
            }
        }
        if (requerente.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + getTextoRequerente() + " deve ser informado.");
        }
        validarEndereco(ve, "Dados do Requerente - Endereço da Atividade. ", requerente.getEnderecoCorreio());
        if (origemProcesso != null && !isRedeSim() && Strings.isNullOrEmpty(requerente.getTelefone())) {
            ve.adicionarMensagemDeCampoObrigatorio("Dados do Requerente. O campo Telefone deve ser informado.");
        }
        if (origemProcesso != null && Strings.isNullOrEmpty(requerente.getEmail())) {
            ve.adicionarMensagemDeCampoObrigatorio("Dados do Requerente. O campo E-mail deve ser informado.");
        }
        if (requerente.getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Dados do Requerente. O Anexo do Documento Pessoal deve ser informado.");
        }
        if (origemProcesso != null && !isRedeSim() && !isOnline() && representante.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Representante deve ser informado.");
        }
        if (origemProcesso != null && !isRedeSim() && !isOnline() && Strings.isNullOrEmpty(representante.getTelefone())) {
            ve.adicionarMensagemDeCampoObrigatorio("Dados do Representante. O campo Telefone deve ser informado.");
        }
        if (getCnaes().isEmpty() && Strings.isNullOrEmpty(descricaoAtividade)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um CNAE ou informe a Descrição da Atividade.");
        }
        if (getProcessoUnidadeMedidas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma unidade de medida.");
        }
        ve.lancarException();
    }

    private void validarEndereco(ValidacaoException ve,
                                 String preFixo,
                                 EnderecoCorreio enderecoCorreio) {
        if (Strings.isNullOrEmpty(enderecoCorreio.getLogradouro())) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Logradouro deve ser informado.");
        }
        if (Strings.isNullOrEmpty(enderecoCorreio.getBairro())) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Bairro deve ser informado.");
        }
        if (Strings.isNullOrEmpty(enderecoCorreio.getNumero())) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Número deve ser informado.");
        }
        if (Strings.isNullOrEmpty(enderecoCorreio.getCep())) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo CEP deve ser informado.");
        }
        if (Strings.isNullOrEmpty(enderecoCorreio.getLocalidade())) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Cidade deve ser informado.");
        }
        if (Strings.isNullOrEmpty(enderecoCorreio.getUf())) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo UF deve ser informado.");
        }
    }

    public boolean hasUnidadeMedida(ProcessoLicenciamentoAmbientalUnidadeMedida processoUnidadeMedida) {
        if (processoUnidadeMedidas != null && !processoUnidadeMedidas.isEmpty()) {
            return processoUnidadeMedidas.stream().anyMatch(uniMed ->
                Util.isEntidadesIguais(uniMed.getUnidadeMedidaAmbiental(), processoUnidadeMedida.getUnidadeMedidaAmbiental())
                    && Util.isStringIgual(uniMed.getUnidadeMedidaAmbiental().getTipoUnidade().name(), processoUnidadeMedida.getUnidadeMedidaAmbiental().getTipoUnidade().name())
                    && Util.isStringIgual(uniMed.getUnidadeMedidaAmbiental().getDescricao(), processoUnidadeMedida.getUnidadeMedidaAmbiental().getDescricao()));
        }
        return false;
    }

    @Override
    public String toString() {
        return (sequencial != null && origemProcesso != null ? sequencial + " " + origemProcesso.getDescricao() + ". " : "") + assuntoLicenciamentoAmbiental.getDescricao() +
            (requerente.getPessoa() != null ? " - " + requerente.getPessoa().getNomeCpfCnpj() : "");
    }
}
