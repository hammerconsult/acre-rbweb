package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PatrimonioDoLote;
import br.com.webpublico.enums.tributario.*;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AssistenteRelatorioCadastroImobiliario implements Serializable {

    private AbstractReport abstractReport;

    private String lote;
    private String quadra;
    private String setor;
    private String cadastroInicial;
    private String cadastroFinal;
    private String cep;
    private String codigoFace;

    private StringBuilder condicoes;
    private StringBuilder ordem;
    private StringBuilder filtros;

    private Boolean imune;
    private Boolean isento;
    private Boolean detalhado;
    private Boolean imovelSemProprietario;
    private Boolean imovelSemCompromissario;
    private Boolean filtrarProprietariosCpfCnpjInvalido;
    private Boolean filtrarCompromissariosCpfCnpjInvalido;
    private Boolean imovelSemLogradouro;
    private Boolean imovelSemBairro;
    private Boolean cepInvalido;
    private Boolean cepNulo;
    private Boolean imovelSemMetragemTerreno;
    private Boolean imovelSemAreaTerreno;
    private Boolean imovelSemValorVenalTerreno;
    private Boolean imovelSemValorVenalImovel;
    private Boolean imovelSemValorVenalExcedente;
    private Boolean imovelSemAliquiota;
    private Boolean imovelSemValorVenalConstrucao;

    private Date dataBCI;

    private BigDecimal aliquota;
    private BigDecimal metragemInicialTerreno;
    private BigDecimal metragemFinalTerreno;
    private BigDecimal areaInicialTerreno;
    private BigDecimal areaFinalTerreno;
    private BigDecimal valorVenalInicialTerreno;
    private BigDecimal valorVenalFinalTerreno;
    private BigDecimal valorVenalInicialImovel;
    private BigDecimal valorVenalFinalImovel;
    private BigDecimal valorVenalInicialExcedente;
    private BigDecimal valorVenalFinalExcedente;
    private BigDecimal valorInicialAreaConstruida;
    private BigDecimal valorFinalAreaConstruida;
    private BigDecimal valorVenalInicialConstrucao;
    private BigDecimal valorVenalFinalConstrucao;
    private BigDecimal larguraInicial;
    private BigDecimal larguraFinal;
    private BigDecimal metragemInicialTestada;
    private BigDecimal metragemFinalTestada;

    private Atributo atributo;

    private TipoIsencao tipoIsencao;
    private TipoCadastroTerreno tipoCadastro;
    private TipoRelatorioCadastroImobiliario tipoRelatorioCadastroImobiliario;
    private SituacaoCadastroImobiliario situacaoCadastroImobiliario;
    private CodigoFaceSimNao codigoFaceSimNao;
    private CodigoFaceSimNao codigoFaceTestaPrincipal;
    private CodigoFaceSimNao possuiColetaLixo;
    private CodigoFaceSimNao possuiServicoUrbano;
    private TipoColetaLixo tipoColetaLixo;
    private List<PatrimonioDoLote> patrimoniosDoLote;
    private List<ValorPossivel> tiposConstrucao;

    private Bairro bairro;
    private Pessoa proprietario;
    private Pessoa compromissario;
    private Logradouro logradouro;
    private CategoriaIsencaoIPTU categoria;
    private Loteamento loteamento;
    private Condominio condominio;
    private PatrimonioDoLote patrimonioDoLote;

    private ValorPossivel tipoConstrucao;
    private ValorPossivel tipoImovel;
    private ValorPossivel qualidadeConstrucao;
    private ValorPossivel comercial;
    private ValorPossivel padraoResidencial;

    private ValorPossivel topografia;
    private ValorPossivel pedologia;
    private ValorPossivel situacao;
    private ValorPossivel patrimonio;


    public AssistenteRelatorioCadastroImobiliario() {
        this.abstractReport = AbstractReport.getAbstractReport();
        this.condicoes = new StringBuilder();
        this.ordem = new StringBuilder();
        this.filtros = new StringBuilder();
        this.situacaoCadastroImobiliario = SituacaoCadastroImobiliario.ATIVO;

        this.patrimoniosDoLote = Lists.newArrayList();
        this.tiposConstrucao = Lists.newArrayList();

        this.imune = Boolean.FALSE;
        this.isento = Boolean.FALSE;
        this.detalhado = Boolean.FALSE;
        this.imovelSemProprietario = Boolean.FALSE;
        this.imovelSemCompromissario = Boolean.FALSE;
        this.filtrarProprietariosCpfCnpjInvalido = Boolean.FALSE;
        this.filtrarCompromissariosCpfCnpjInvalido = Boolean.FALSE;
        this.imovelSemLogradouro = Boolean.FALSE;
        this.imovelSemBairro = Boolean.FALSE;
        this.cepInvalido = Boolean.FALSE;
        this.cepNulo = Boolean.FALSE;
        this.imovelSemMetragemTerreno = Boolean.FALSE;
        this.imovelSemAreaTerreno = Boolean.FALSE;
        this.imovelSemValorVenalTerreno = Boolean.FALSE;
        this.imovelSemValorVenalImovel = Boolean.FALSE;
        this.imovelSemValorVenalExcedente = Boolean.FALSE;
        this.imovelSemAliquiota = Boolean.FALSE;
        this.imovelSemValorVenalConstrucao = Boolean.FALSE;
    }

    public AbstractReport getAbstractReport() {
        return abstractReport;
    }

    public void setAbstractReport(AbstractReport abstractReport) {
        this.abstractReport = abstractReport;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCodigoFace() {
        return codigoFace;
    }

    public void setCodigoFace(String codigoFace) {
        this.codigoFace = codigoFace;
    }

    public StringBuilder getCondicoes() {
        return condicoes;
    }

    public void setCondicoes(StringBuilder condicoes) {
        this.condicoes = condicoes;
    }

    public StringBuilder getFiltros() {
        return filtros;
    }

    public void setFiltros(StringBuilder filtros) {
        this.filtros = filtros;
    }

    public StringBuilder getOrdem() {
        return ordem;
    }

    public void setOrdem(StringBuilder ordem) {
        this.ordem = ordem;
    }

    public Boolean getImune() {
        return imune;
    }

    public void setImune(Boolean imune) {
        this.imune = imune;
    }

    public Boolean getIsento() {
        return isento;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
    }

    public Boolean getImovelSemProprietario() {
        return imovelSemProprietario;
    }

    public void setImovelSemProprietario(Boolean imovelSemProprietario) {
        this.imovelSemProprietario = imovelSemProprietario;
    }

    public Boolean getImovelSemCompromissario() {
        return imovelSemCompromissario;
    }

    public void setImovelSemCompromissario(Boolean imovelSemCompromissario) {
        this.imovelSemCompromissario = imovelSemCompromissario;
    }

    public Boolean getFiltrarProprietariosCpfCnpjInvalido() {
        return filtrarProprietariosCpfCnpjInvalido;
    }

    public void setFiltrarProprietariosCpfCnpjInvalido(Boolean filtrarProprietariosCpfCnpjInvalido) {
        this.filtrarProprietariosCpfCnpjInvalido = filtrarProprietariosCpfCnpjInvalido;
    }

    public Boolean getFiltrarCompromissariosCpfCnpjInvalido() {
        return filtrarCompromissariosCpfCnpjInvalido;
    }

    public void setFiltrarCompromissariosCpfCnpjInvalido(Boolean filtrarCompromissariosCpfCnpjInvalido) {
        this.filtrarCompromissariosCpfCnpjInvalido = filtrarCompromissariosCpfCnpjInvalido;
    }

    public Boolean getImovelSemLogradouro() {
        return imovelSemLogradouro;
    }

    public void setImovelSemLogradouro(Boolean imovelSemLogradouro) {
        this.imovelSemLogradouro = imovelSemLogradouro;
    }

    public Boolean getImovelSemBairro() {
        return imovelSemBairro;
    }

    public void setImovelSemBairro(Boolean imovelSemBairro) {
        this.imovelSemBairro = imovelSemBairro;
    }

    public Boolean getCepInvalido() {
        return cepInvalido;
    }

    public void setCepInvalido(Boolean cepInvalido) {
        this.cepInvalido = cepInvalido;
    }

    public Boolean getCepNulo() {
        return cepNulo;
    }

    public void setCepNulo(Boolean cepNulo) {
        this.cepNulo = cepNulo;
    }

    public Boolean getImovelSemMetragemTerreno() {
        return imovelSemMetragemTerreno;
    }

    public void setImovelSemMetragemTerreno(Boolean imovelSemMetragemTerreno) {
        this.imovelSemMetragemTerreno = imovelSemMetragemTerreno;
    }

    public Boolean getImovelSemAreaTerreno() {
        return imovelSemAreaTerreno;
    }

    public void setImovelSemAreaTerreno(Boolean imovelSemAreaTerreno) {
        this.imovelSemAreaTerreno = imovelSemAreaTerreno;
    }

    public Boolean getImovelSemValorVenalTerreno() {
        return imovelSemValorVenalTerreno;
    }

    public void setImovelSemValorVenalTerreno(Boolean imovelSemValorVenalTerreno) {
        this.imovelSemValorVenalTerreno = imovelSemValorVenalTerreno;
    }

    public Boolean getImovelSemValorVenalImovel() {
        return imovelSemValorVenalImovel;
    }

    public void setImovelSemValorVenalImovel(Boolean imovelSemValorVenalImovel) {
        this.imovelSemValorVenalImovel = imovelSemValorVenalImovel;
    }

    public Boolean getImovelSemValorVenalExcedente() {
        return imovelSemValorVenalExcedente;
    }

    public void setImovelSemValorVenalExcedente(Boolean imovelSemValorVenalExcedente) {
        this.imovelSemValorVenalExcedente = imovelSemValorVenalExcedente;
    }

    public Boolean getImovelSemAliquiota() {
        return imovelSemAliquiota;
    }

    public void setImovelSemAliquiota(Boolean imovelSemAliquiota) {
        this.imovelSemAliquiota = imovelSemAliquiota;
    }

    public Boolean getImovelSemValorVenalConstrucao() {
        return imovelSemValorVenalConstrucao;
    }

    public void setImovelSemValorVenalConstrucao(Boolean imovelSemValorVenalConstrucao) {
        this.imovelSemValorVenalConstrucao = imovelSemValorVenalConstrucao;
    }

    public Date getDataBCI() {
        return dataBCI;
    }

    public void setDataBCI(Date dataBCI) {
        this.dataBCI = dataBCI;
    }

    public BigDecimal getMetragemInicialTerreno() {
        return metragemInicialTerreno;
    }

    public void setMetragemInicialTerreno(BigDecimal metragemInicialTerreno) {
        this.metragemInicialTerreno = metragemInicialTerreno;
    }

    public BigDecimal getMetragemFinalTerreno() {
        return metragemFinalTerreno;
    }

    public void setMetragemFinalTerreno(BigDecimal metragemFinalTerreno) {
        this.metragemFinalTerreno = metragemFinalTerreno;
    }

    public BigDecimal getAreaInicialTerreno() {
        return areaInicialTerreno;
    }

    public void setAreaInicialTerreno(BigDecimal areaInicialTerreno) {
        this.areaInicialTerreno = areaInicialTerreno;
    }

    public BigDecimal getAreaFinalTerreno() {
        return areaFinalTerreno;
    }

    public void setAreaFinalTerreno(BigDecimal areaFinalTerreno) {
        this.areaFinalTerreno = areaFinalTerreno;
    }

    public BigDecimal getValorVenalInicialTerreno() {
        return valorVenalInicialTerreno;
    }

    public void setValorVenalInicialTerreno(BigDecimal valorVenalInicialTerreno) {
        this.valorVenalInicialTerreno = valorVenalInicialTerreno;
    }

    public BigDecimal getValorVenalFinalTerreno() {
        return valorVenalFinalTerreno;
    }

    public void setValorVenalFinalTerreno(BigDecimal valorVenalFinalTerreno) {
        this.valorVenalFinalTerreno = valorVenalFinalTerreno;
    }

    public BigDecimal getValorVenalInicialImovel() {
        return valorVenalInicialImovel;
    }

    public void setValorVenalInicialImovel(BigDecimal valorVenalInicialImovel) {
        this.valorVenalInicialImovel = valorVenalInicialImovel;
    }

    public BigDecimal getValorVenalFinalImovel() {
        return valorVenalFinalImovel;
    }

    public void setValorVenalFinalImovel(BigDecimal valorVenalFinalImovel) {
        this.valorVenalFinalImovel = valorVenalFinalImovel;
    }

    public BigDecimal getValorVenalInicialExcedente() {
        return valorVenalInicialExcedente;
    }

    public void setValorVenalInicialExcedente(BigDecimal valorVenalInicialExcedente) {
        this.valorVenalInicialExcedente = valorVenalInicialExcedente;
    }

    public BigDecimal getValorVenalFinalExcedente() {
        return valorVenalFinalExcedente;
    }

    public void setValorVenalFinalExcedente(BigDecimal valorVenalFinalExcedente) {
        this.valorVenalFinalExcedente = valorVenalFinalExcedente;
    }

    public BigDecimal getValorInicialAreaConstruida() {
        return valorInicialAreaConstruida;
    }

    public void setValorInicialAreaConstruida(BigDecimal valorInicialAreaConstruida) {
        this.valorInicialAreaConstruida = valorInicialAreaConstruida;
    }

    public BigDecimal getValorFinalAreaConstruida() {
        return valorFinalAreaConstruida;
    }

    public void setValorFinalAreaConstruida(BigDecimal valorFinalAreaConstruida) {
        this.valorFinalAreaConstruida = valorFinalAreaConstruida;
    }

    public BigDecimal getValorVenalInicialConstrucao() {
        return valorVenalInicialConstrucao;
    }

    public void setValorVenalInicialConstrucao(BigDecimal valorVenalInicialConstrucao) {
        this.valorVenalInicialConstrucao = valorVenalInicialConstrucao;
    }

    public BigDecimal getValorVenalFinalConstrucao() {
        return valorVenalFinalConstrucao;
    }

    public void setValorVenalFinalConstrucao(BigDecimal valorVenalFinalConstrucao) {
        this.valorVenalFinalConstrucao = valorVenalFinalConstrucao;
    }

    public BigDecimal getLarguraInicial() {
        return larguraInicial;
    }

    public void setLarguraInicial(BigDecimal larguraInicial) {
        this.larguraInicial = larguraInicial;
    }

    public BigDecimal getLarguraFinal() {
        return larguraFinal;
    }

    public void setLarguraFinal(BigDecimal larguraFinal) {
        this.larguraFinal = larguraFinal;
    }

    public BigDecimal getMetragemInicialTestada() {
        return metragemInicialTestada;
    }

    public void setMetragemInicialTestada(BigDecimal metragemInicialTestada) {
        this.metragemInicialTestada = metragemInicialTestada;
    }

    public BigDecimal getMetragemFinalTestada() {
        return metragemFinalTestada;
    }

    public void setMetragemFinalTestada(BigDecimal metragemFinalTestada) {
        this.metragemFinalTestada = metragemFinalTestada;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public TipoIsencao getTipoIsencao() {
        return tipoIsencao;
    }

    public void setTipoIsencao(TipoIsencao tipoIsencao) {
        this.tipoIsencao = tipoIsencao;
    }

    public TipoCadastroTerreno getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTerreno tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public TipoRelatorioCadastroImobiliario getTipoRelatorioCadastroImobiliario() {
        return tipoRelatorioCadastroImobiliario;
    }

    public void setTipoRelatorioCadastroImobiliario(TipoRelatorioCadastroImobiliario tipoRelatorioCadastroImobiliario) {
        this.tipoRelatorioCadastroImobiliario = tipoRelatorioCadastroImobiliario;
    }

    public SituacaoCadastroImobiliario getSituacaoCadastroImobiliario() {
        return situacaoCadastroImobiliario;
    }

    public void setSituacaoCadastroImobiliario(SituacaoCadastroImobiliario situacaoCadastroImobiliario) {
        this.situacaoCadastroImobiliario = situacaoCadastroImobiliario;
    }

    public CodigoFaceSimNao getCodigoFaceSimNao() {
        return codigoFaceSimNao;
    }

    public void setCodigoFaceSimNao(CodigoFaceSimNao codigoFaceSimNao) {
        this.codigoFaceSimNao = codigoFaceSimNao;
    }

    public CodigoFaceSimNao getCodigoFaceTestaPrincipal() {
        return codigoFaceTestaPrincipal;
    }

    public void setCodigoFaceTestaPrincipal(CodigoFaceSimNao codigoFaceTestaPrincipal) {
        this.codigoFaceTestaPrincipal = codigoFaceTestaPrincipal;
    }

    public CodigoFaceSimNao getPossuiColetaLixo() {
        return possuiColetaLixo;
    }

    public void setPossuiColetaLixo(CodigoFaceSimNao possuiColetaLixo) {
        this.possuiColetaLixo = possuiColetaLixo;
    }

    public CodigoFaceSimNao getPossuiServicoUrbano() {
        return possuiServicoUrbano;
    }

    public void setPossuiServicoUrbano(CodigoFaceSimNao possuiServicoUrbano) {
        this.possuiServicoUrbano = possuiServicoUrbano;
    }

    public TipoColetaLixo getTipoColetaLixo() {
        return tipoColetaLixo;
    }

    public void setTipoColetaLixo(TipoColetaLixo tipoColetaLixo) {
        this.tipoColetaLixo = tipoColetaLixo;
    }

    public List<PatrimonioDoLote> getPatrimoniosDoLote() {
        return patrimoniosDoLote;
    }

    public void setPatrimoniosDoLote(List<PatrimonioDoLote> patrimoniosDoLote) {
        this.patrimoniosDoLote = patrimoniosDoLote;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Pessoa getProprietario() {
        return proprietario;
    }

    public void setProprietario(Pessoa proprietario) {
        this.proprietario = proprietario;
    }

    public Pessoa getCompromissario() {
        return compromissario;
    }

    public void setCompromissario(Pessoa compromissario) {
        this.compromissario = compromissario;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public CategoriaIsencaoIPTU getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaIsencaoIPTU categoria) {
        this.categoria = categoria;
    }

    public Loteamento getLoteamento() {
        return loteamento;
    }

    public void setLoteamento(Loteamento loteamento) {
        this.loteamento = loteamento;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public PatrimonioDoLote getPatrimonioDoLote() {
        return patrimonioDoLote;
    }

    public void setPatrimonioDoLote(PatrimonioDoLote patrimonioDoLote) {
        this.patrimonioDoLote = patrimonioDoLote;
    }

    public List<ValorPossivel> getTiposConstrucao() {
        return tiposConstrucao;
    }

    public void setTiposConstrucao(List<ValorPossivel> tiposConstrucao) {
        this.tiposConstrucao = tiposConstrucao;
    }

    public ValorPossivel getTipoConstrucao() {
        return tipoConstrucao;
    }

    public void setTipoConstrucao(ValorPossivel tipoConstrucao) {
        this.tipoConstrucao = tipoConstrucao;
    }

    public ValorPossivel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(ValorPossivel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public ValorPossivel getQualidadeConstrucao() {
        return qualidadeConstrucao;
    }

    public void setQualidadeConstrucao(ValorPossivel qualidadeConstrucao) {
        this.qualidadeConstrucao = qualidadeConstrucao;
    }

    public ValorPossivel getComercial() {
        return comercial;
    }

    public void setComercial(ValorPossivel comercial) {
        this.comercial = comercial;
    }

    public ValorPossivel getPadraoResidencial() {
        return padraoResidencial;
    }

    public void setPadraoResidencial(ValorPossivel padraoResidencial) {
        this.padraoResidencial = padraoResidencial;
    }

    public ValorPossivel getTopografia() {
        return topografia;
    }

    public void setTopografia(ValorPossivel topografia) {
        this.topografia = topografia;
    }

    public ValorPossivel getPedologia() {
        return pedologia;
    }

    public void setPedologia(ValorPossivel pedologia) {
        this.pedologia = pedologia;
    }

    public ValorPossivel getSituacao() {
        return situacao;
    }

    public void setSituacao(ValorPossivel situacao) {
        this.situacao = situacao;
    }

    public ValorPossivel getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(ValorPossivel patrimonio) {
        this.patrimonio = patrimonio;
    }

    public void limparCampos() {
        this.setor = null;
        this.quadra = null;
        this.lote = null;
        this.aliquota = null;
        this.tipoCadastro = null;
        this.cadastroInicial = "1";
        this.cadastroFinal = "999999999999999";
        this.categoria = null;
        this.proprietario = null;
        this.compromissario = null;
        this.logradouro = null;
        this.bairro = null;

    }
}
