package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@Entity
@Audited
@Etiqueta("Auxílio Funeral")
public class AuxilioFuneral extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataDoAtendimento;
    @ManyToOne
    @Etiqueta("Procedência")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Procedencia procedencia;
    @ManyToOne
    @Etiqueta("Funerária")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Funeraria funeraria;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome do Responsável pela Solicitação do Auxílio Funeral")
    private String nomeResponsavel;
    private String cpfResponsavel;
    @Length(maximo = 70)
    @Etiqueta("Endereço do Responsável pela Solicitação do Auxílio Funeral")
    private String enderecoResponsavel;
    @Length(maximo = 70)
    @Etiqueta("Bairro do Responsável pela Solicitação do Auxílio Funeral")
    private String bairroResponsavel;
    @Length(maximo = 70)
    @Etiqueta("Complemento do Responsável pela Solicitação do Auxílio Funeral")
    private String complementoResponsavel;
    private String cepResponsavel;
    private String telefoneResponsavel;
    private String celularResponsavel;

    //Falecido
    @Etiqueta("Nome do Falecido")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String nomeFalecido;
    private String cpfFalecido;
    private String rgFalecido;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Nascimento")
    private Date dataNascimentoFalecido;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Falecimento")
    private Date dataFalecimento;
    @Length(maximo = 70)
    private String enderecoFalecido;
    private Boolean trabalhava;
    private Boolean recebiaBeneficio;
    @Length(maximo = 255)
    private String beneficio;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "auxilioFuneral")
    private List<ComposicaoFamiliar> familiares;

    //Situação da Moradia
    @Etiqueta("Rua/Travessa/Beco")
    @Enumerated(EnumType.STRING)
    private TipoAsfalto tipoAsfalto;
    @Etiqueta("Terreno")
    @Enumerated(EnumType.STRING)
    private TipoTerreno tipoTerreno;
    @Etiqueta("Quantidade de casas no terreno")
    private Integer quantidadeCasas;
    private Integer numeroComodos;
    @Etiqueta("Condições de Ocupação")
    @Enumerated(EnumType.STRING)
    private CondicaoOcupacao condicaoOcupacao;
    @Etiqueta("Tipo de casa")
    @Enumerated(EnumType.STRING)
    private TipoCasa tipoCasa;
    @Etiqueta("Tipo de esgoto")
    @Enumerated(EnumType.STRING)
    private TipoEsgoto tipoEsgoto;
    private Boolean iluminacaoPublica;
    @Etiqueta("Água")
    @Enumerated(EnumType.STRING)
    private TipoDistribuicaoAgua tipoDistribuicaoAgua;
    @Etiqueta("Energia Elétrica")
    @Enumerated(EnumType.STRING)
    private TipoEnergiaEletrica tipoEnergiaEletrica;
    private String numeroGuiaSepultamento;

    //Parecer
    @Etiqueta("Tipo de benefício cedido")
    @Enumerated(EnumType.STRING)
    private TipoBeneficioCedido tipoBeneficioCedido;
    @Etiqueta("Motivo pelo qual requisitou apenas 01 (um) benefício")
    @Enumerated(EnumType.STRING)
    private MotivoRequisicaoUmBeneficio motivoRequisicaoUmBeneficio;
    @Length(maximo = 3000)
    private String avaliacaoPsicossocial;
    @Length(maximo = 3000)
    private String parecer;

    //Solicitação para Sepultamento
    @Etiqueta("Nome do responsável pela solicitação do Auxílio")
    @Length(maximo = 70)
    private String nomeResponsavelSolicitacaoSep;
    @Etiqueta("Endereço do responsável pela solicitação do Auxílio")
    @Length(maximo = 70)
    private String enderecoResponsavelSolicSep;
    @Etiqueta("RG do responsável pela solicitação do Auxílio")
    private String rgResponsavelSolicitacaoSep;
    @Etiqueta("Estado Civil do responsável pela solicitação do Auxílio")
    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivilResponsavelSolicSep;

    //Declaração de Benefícios Eventuais
    @Etiqueta("Nome do responsável pela solicitação do Auxílio")
    @Length(maximo = 70)
    private String nomeResponsavelBeneficio;
    @Etiqueta("Endereço do responsável pela solicitação do Auxílio")
    @Length(maximo = 70)
    private String enderecoResponsavelBeneficio;
    @Etiqueta("RG do responsável pela solicitação do Auxílio")
    private String rgResponsavelBeneficio;
    @Etiqueta("CPF do responsável pela solicitação do Auxílio")
    private String cpfResponsavelBeneficio;
    @Etiqueta("Estado Civil do responsável pela solicitação do Auxílio")
    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivilResponsavelBenef;
    @Etiqueta("Renúncia de Benefício")
    @Enumerated(EnumType.STRING)
    private TipoBeneficioCedido tipoBeneficioRenunciado;

    //Requisição
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraRequisicao;
    @Etiqueta("Cemitério")
    private String cemiterio;

    //Óbito
    @Etiqueta("Causa do óbito")
    private String causaObito;
    @Etiqueta("Ocorrência")
    private String ocorrenciaObito;
    @Etiqueta("Local")
    private String localObito;
    @Etiqueta("Data do Atendimento")
    @Temporal(TemporalType.DATE)
    private Date dataAtendimentoObito;
    @Etiqueta("Data da visita domiciliar")
    @Temporal(TemporalType.DATE)
    private Date dataVisitaDomiciliarObito;
    @Etiqueta("Responsável pela visita")
    private String responsavelVisitaObito;
    @Etiqueta("Endereço")
    private String enderecoObito;
    @Etiqueta("Vínculo")
    private String vinculoObito;
    @Etiqueta("CPF")
    private String cpfObito;
    @Etiqueta("RG")
    private String rgObito;
    @Etiqueta("Agente Social")
    private String agenteSocialObito;

    //Declaracao do solicitante do beneficio de auxilio funeral
    @Etiqueta("Nome do Responsável Solicitante do Benefício ")
    @Length(maximo = 70)
    private String nomeDeclaracaoAuxilio;

    @Etiqueta("RG Solicitante do Benefício")
    @Length(maximo = 20)
    private String rgDeclaracaoAuxilio;

    @Etiqueta("CPF Solicitante do Benefício")
    @Length(maximo = 20)
    private String cpfDeclaracaoAuxilio;

    @Etiqueta("Estado Civil Solicitante do Benefício")
    @Length(maximo = 20)
    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivilDeclaracaoAuxilio;

    @Etiqueta("Endereço Solicitante do Benefício")
    @Length(maximo = 70)
    private String enderecoDeclaracaoAuxilio;

    public AuxilioFuneral() {
        trabalhava = Boolean.FALSE;
        recebiaBeneficio = Boolean.FALSE;
        iluminacaoPublica = Boolean.FALSE;
        familiares = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataDoAtendimento() {
        return dataDoAtendimento;
    }

    public void setDataDoAtendimento(Date dataDoAtendimento) {
        this.dataDoAtendimento = dataDoAtendimento;
    }

    public Procedencia getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(Procedencia procedencia) {
        this.procedencia = procedencia;
    }

    public Funeraria getFuneraria() {
        return funeraria;
    }

    public void setFuneraria(Funeraria funeraria) {
        this.funeraria = funeraria;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getCpfResponsavel() {
        return cpfResponsavel;
    }

    public void setCpfResponsavel(String cpfResponsavel) {
        this.cpfResponsavel = cpfResponsavel;
    }

    public String getEnderecoResponsavel() {
        return enderecoResponsavel;
    }

    public void setEnderecoResponsavel(String enderecoResponsavel) {
        this.enderecoResponsavel = enderecoResponsavel;
    }

    public String getBairroResponsavel() {
        return bairroResponsavel;
    }

    public void setBairroResponsavel(String bairroResponsavel) {
        this.bairroResponsavel = bairroResponsavel;
    }

    public String getComplementoResponsavel() {
        return complementoResponsavel;
    }

    public void setComplementoResponsavel(String complementoResponsavel) {
        this.complementoResponsavel = complementoResponsavel;
    }

    public String getCepResponsavel() {
        return cepResponsavel;
    }

    public void setCepResponsavel(String cepResponsavel) {
        this.cepResponsavel = cepResponsavel;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public void setTelefoneResponsavel(String telefoneResponsavel) {
        this.telefoneResponsavel = telefoneResponsavel;
    }

    public String getCelularResponsavel() {
        return celularResponsavel;
    }

    public void setCelularResponsavel(String celularResponsavel) {
        this.celularResponsavel = celularResponsavel;
    }

    public String getNomeFalecido() {
        return nomeFalecido;
    }

    public void setNomeFalecido(String nomeFalecido) {
        this.nomeFalecido = nomeFalecido;
    }

    public String getCpfFalecido() {
        return cpfFalecido;
    }

    public void setCpfFalecido(String cpfFalecido) {
        this.cpfFalecido = cpfFalecido;
    }

    public String getRgFalecido() {
        return rgFalecido;
    }

    public void setRgFalecido(String rgFalecido) {
        this.rgFalecido = rgFalecido;
    }

    public Date getDataNascimentoFalecido() {
        return dataNascimentoFalecido;
    }

    public void setDataNascimentoFalecido(Date dataNascimentoFalecido) {
        this.dataNascimentoFalecido = dataNascimentoFalecido;
    }

    public Date getDataFalecimento() {
        return dataFalecimento;
    }

    public void setDataFalecimento(Date dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public String getEnderecoFalecido() {
        return enderecoFalecido;
    }

    public void setEnderecoFalecido(String enderecoFalecimento) {
        this.enderecoFalecido = enderecoFalecimento;
    }

    public Boolean getTrabalhava() {
        return trabalhava;
    }

    public void setTrabalhava(Boolean trabalhava) {
        this.trabalhava = trabalhava;
    }

    public Boolean getRecebiaBeneficio() {
        return recebiaBeneficio;
    }

    public void setRecebiaBeneficio(Boolean recebiaBeneficio) {
        this.recebiaBeneficio = recebiaBeneficio;
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String beneficio) {
        this.beneficio = beneficio;
    }

    public List<ComposicaoFamiliar> getFamiliares() {
        return familiares;
    }

    public void setFamiliares(List<ComposicaoFamiliar> familiares) {
        this.familiares = familiares;
    }

    public TipoAsfalto getTipoAsfalto() {
        return tipoAsfalto;
    }

    public void setTipoAsfalto(TipoAsfalto tipoAsfalto) {
        this.tipoAsfalto = tipoAsfalto;
    }

    public TipoTerreno getTipoTerreno() {
        return tipoTerreno;
    }

    public void setTipoTerreno(TipoTerreno tipoTerreno) {
        this.tipoTerreno = tipoTerreno;
    }

    public Integer getQuantidadeCasas() {
        return quantidadeCasas;
    }

    public void setQuantidadeCasas(Integer quantidadeCasas) {
        this.quantidadeCasas = quantidadeCasas;
    }

    public Integer getNumeroComodos() {
        return numeroComodos;
    }

    public void setNumeroComodos(Integer numeroComodos) {
        this.numeroComodos = numeroComodos;
    }

    public CondicaoOcupacao getCondicaoOcupacao() {
        return condicaoOcupacao;
    }

    public void setCondicaoOcupacao(CondicaoOcupacao condicaoOcupacao) {
        this.condicaoOcupacao = condicaoOcupacao;
    }

    public TipoCasa getTipoCasa() {
        return tipoCasa;
    }

    public void setTipoCasa(TipoCasa tipoCasa) {
        this.tipoCasa = tipoCasa;
    }

    public TipoEsgoto getTipoEsgoto() {
        return tipoEsgoto;
    }

    public void setTipoEsgoto(TipoEsgoto tipoEsgoto) {
        this.tipoEsgoto = tipoEsgoto;
    }

    public Boolean getIluminacaoPublica() {
        return iluminacaoPublica;
    }

    public void setIluminacaoPublica(Boolean iluminacaoPublica) {
        this.iluminacaoPublica = iluminacaoPublica;
    }

    public TipoDistribuicaoAgua getTipoDistribuicaoAgua() {
        return tipoDistribuicaoAgua;
    }

    public void setTipoDistribuicaoAgua(TipoDistribuicaoAgua tipoDistribuicaoAgua) {
        this.tipoDistribuicaoAgua = tipoDistribuicaoAgua;
    }

    public TipoEnergiaEletrica getTipoEnergiaEletrica() {
        return tipoEnergiaEletrica;
    }

    public void setTipoEnergiaEletrica(TipoEnergiaEletrica tipoEnergiaEletrica) {
        this.tipoEnergiaEletrica = tipoEnergiaEletrica;
    }

    public String getNumeroGuiaSepultamento() {
        return numeroGuiaSepultamento;
    }

    public void setNumeroGuiaSepultamento(String numeroGuiaSepultamento) {
        this.numeroGuiaSepultamento = numeroGuiaSepultamento;
    }

    public TipoBeneficioCedido getTipoBeneficioCedido() {
        return tipoBeneficioCedido;
    }

    public void setTipoBeneficioCedido(TipoBeneficioCedido tipoBeneficioCedido) {
        this.tipoBeneficioCedido = tipoBeneficioCedido;
    }

    public MotivoRequisicaoUmBeneficio getMotivoRequisicaoUmBeneficio() {
        return motivoRequisicaoUmBeneficio;
    }

    public void setMotivoRequisicaoUmBeneficio(MotivoRequisicaoUmBeneficio motivoRequisicaoUmBeneficio) {
        this.motivoRequisicaoUmBeneficio = motivoRequisicaoUmBeneficio;
    }

    public String getAvaliacaoPsicossocial() {
        return avaliacaoPsicossocial;
    }

    public void setAvaliacaoPsicossocial(String avaliacaoPsicossocial) {
        this.avaliacaoPsicossocial = avaliacaoPsicossocial;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public String getNomeResponsavelSolicitacaoSep() {
        return nomeResponsavelSolicitacaoSep;
    }

    public void setNomeResponsavelSolicitacaoSep(String nomeResponsavelSolicitacaoSep) {
        this.nomeResponsavelSolicitacaoSep = nomeResponsavelSolicitacaoSep;
    }

    public String getEnderecoResponsavelSolicSep() {
        return enderecoResponsavelSolicSep;
    }

    public void setEnderecoResponsavelSolicSep(String enderecoResponsavelSolicSep) {
        this.enderecoResponsavelSolicSep = enderecoResponsavelSolicSep;
    }

    public String getRgResponsavelSolicitacaoSep() {
        return rgResponsavelSolicitacaoSep;
    }

    public void setRgResponsavelSolicitacaoSep(String rgResponsavelSolicitacaoSep) {
        this.rgResponsavelSolicitacaoSep = rgResponsavelSolicitacaoSep;
    }

    public EstadoCivil getEstadoCivilResponsavelSolicSep() {
        return estadoCivilResponsavelSolicSep;
    }

    public void setEstadoCivilResponsavelSolicSep(EstadoCivil estadoCivilResponsavelSolicSep) {
        this.estadoCivilResponsavelSolicSep = estadoCivilResponsavelSolicSep;
    }

    public String getNomeResponsavelBeneficio() {
        return nomeResponsavelBeneficio;
    }

    public void setNomeResponsavelBeneficio(String nomeResponsavelBeneficio) {
        this.nomeResponsavelBeneficio = nomeResponsavelBeneficio;
    }

    public String getEnderecoResponsavelBeneficio() {
        return enderecoResponsavelBeneficio;
    }

    public void setEnderecoResponsavelBeneficio(String enderecoResponsavelBeneficio) {
        this.enderecoResponsavelBeneficio = enderecoResponsavelBeneficio;
    }

    public String getRgResponsavelBeneficio() {
        return rgResponsavelBeneficio;
    }

    public void setRgResponsavelBeneficio(String rgResponsavelBeneficio) {
        this.rgResponsavelBeneficio = rgResponsavelBeneficio;
    }

    public EstadoCivil getEstadoCivilResponsavelBenef() {
        return estadoCivilResponsavelBenef;
    }

    public void setEstadoCivilResponsavelBenef(EstadoCivil estadoCivilResponsavelBenef) {
        this.estadoCivilResponsavelBenef = estadoCivilResponsavelBenef;
    }

    public TipoBeneficioCedido getTipoBeneficioRenunciado() {
        return tipoBeneficioRenunciado;
    }

    public void setTipoBeneficioRenunciado(TipoBeneficioCedido tipoBeneficioRenunciado) {
        this.tipoBeneficioRenunciado = tipoBeneficioRenunciado;
    }

    public Date getDataHoraRequisicao() {
        return dataHoraRequisicao;
    }

    public void setDataHoraRequisicao(Date dataHoraRequisicao) {
        this.dataHoraRequisicao = dataHoraRequisicao;
    }

    public String getCemiterio() {
        return cemiterio;
    }

    public void setCemiterio(String cemiterio) {
        this.cemiterio = cemiterio;
    }

    public String getCpfResponsavelBeneficio() {
        return cpfResponsavelBeneficio;
    }

    public void setCpfResponsavelBeneficio(String cpfResponsavelBeneficio) {
        this.cpfResponsavelBeneficio = cpfResponsavelBeneficio;
    }

    public BigDecimal getRendaPerCapita() {
        BigDecimal total = BigDecimal.ZERO;
        for (ComposicaoFamiliar composicaoFamiliar : familiares) {
            total = total.add(composicaoFamiliar.getRenda());
        }

        return familiares.size() > 0 && total.compareTo(BigDecimal.ZERO) > 0 ? total.divide(BigDecimal.valueOf(familiares.size()), BigDecimal.ROUND_HALF_EVEN, 2).setScale(2, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
    }

    public BigDecimal getRendaTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ComposicaoFamiliar composicaoFamiliar : familiares) {
            total = total.add(composicaoFamiliar.getRenda());
        }
        return total;
    }

    public String getCausaObito() {
        return causaObito;
    }

    public void setCausaObito(String causaObito) {
        this.causaObito = causaObito;
    }

    public String getOcorrenciaObito() {
        return ocorrenciaObito;
    }

    public void setOcorrenciaObito(String ocorrenciaObito) {
        this.ocorrenciaObito = ocorrenciaObito;
    }

    public String getLocalObito() {
        return localObito;
    }

    public void setLocalObito(String localObito) {
        this.localObito = localObito;
    }

    public Date getDataAtendimentoObito() {
        return dataAtendimentoObito;
    }

    public void setDataAtendimentoObito(Date dataAtendimentoObito) {
        this.dataAtendimentoObito = dataAtendimentoObito;
    }

    public Date getDataVisitaDomiciliarObito() {
        return dataVisitaDomiciliarObito;
    }

    public void setDataVisitaDomiciliarObito(Date dataVisitaDomiciliarObito) {
        this.dataVisitaDomiciliarObito = dataVisitaDomiciliarObito;
    }

    public String getResponsavelVisitaObito() {
        return responsavelVisitaObito;
    }

    public void setResponsavelVisitaObito(String responsavelVisitaObito) {
        this.responsavelVisitaObito = responsavelVisitaObito;
    }

    public String getEnderecoObito() {
        return enderecoObito;
    }

    public void setEnderecoObito(String enderecoObito) {
        this.enderecoObito = enderecoObito;
    }

    public String getVinculoObito() {
        return vinculoObito;
    }

    public void setVinculoObito(String vinculoObito) {
        this.vinculoObito = vinculoObito;
    }

    public String getCpfObito() {
        return cpfObito;
    }

    public void setCpfObito(String cpfObito) {
        this.cpfObito = cpfObito;
    }

    public String getRgObito() {
        return rgObito;
    }

    public void setRgObito(String rgObito) {
        this.rgObito = rgObito;
    }

    public String getAgenteSocialObito() {
        return agenteSocialObito;
    }

    public String getNomeDeclaracaoAuxilio() {
        return nomeDeclaracaoAuxilio;
    }

    public void setNomeDeclaracaoAuxilio(String nomeDeclaracaoAuxilio) {
        this.nomeDeclaracaoAuxilio = nomeDeclaracaoAuxilio;
    }

    public String getRgDeclaracaoAuxilio() {
        return rgDeclaracaoAuxilio;
    }

    public void setRgDeclaracaoAuxilio(String rgDeclaracaoAuxilio) {
        this.rgDeclaracaoAuxilio = rgDeclaracaoAuxilio;
    }

    public String getCpfDeclaracaoAuxilio() {
        return cpfDeclaracaoAuxilio;
    }

    public void setCpfDeclaracaoAuxilio(String cpfDeclaracaoAuxilio) {
        this.cpfDeclaracaoAuxilio = cpfDeclaracaoAuxilio;
    }

    public EstadoCivil getEstadoCivilDeclaracaoAuxilio() {
        return estadoCivilDeclaracaoAuxilio;
    }

    public void setEstadoCivilDeclaracaoAuxilio(EstadoCivil estadoCivilDeclaracaoAuxilio) {
        this.estadoCivilDeclaracaoAuxilio = estadoCivilDeclaracaoAuxilio;
    }

    public String getEnderecoDeclaracaoAuxilio() {
        return enderecoDeclaracaoAuxilio;
    }

    public void setEnderecoDeclaracaoAuxilio(String enderecoDeclaracaoAuxilio) {
        this.enderecoDeclaracaoAuxilio = enderecoDeclaracaoAuxilio;
    }

    public void setAgenteSocialObito(String agenteSocialObito) {
        this.agenteSocialObito = agenteSocialObito;
    }
}
