
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.CaracterizadorDeBemImovel;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@GrupoDiagrama(nome = "Patrimonial")
@Entity
@Audited
public class Bem extends SuperEntidade implements Comparable<Bem> {

    private static final Logger logger = LoggerFactory.getLogger(Bem.class);
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static HashMap<BigDecimal, Bem> bensCache = new HashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Registro Patrimonial")
    private String identificacao;

    @Tabelavel
    @Etiqueta("Registro Anterior")
    private String registroAnterior;

    @Obrigatorio
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Aquisição")
    private Date dataAquisicao;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    @Tabelavel
    @Etiqueta("Marca")
    private String marca;

    @Tabelavel
    @Etiqueta("Modelo")
    private String modelo;

    @ManyToOne
    private Garantia garantia;

    @ManyToOne
    private Seguradora seguradora;

    @ManyToOne
    private ObjetoCompra objetoCompra;

    @ManyToOne
    private Pessoa fornecedor;

    @ManyToOne
    private CondicaoDeOcupacao condicaoDeOcupacao;

    private String observacao;

    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorOrigemRecurso detentorOrigemRecurso;

    @Etiqueta("Número de Registro")
    private String numeroRegistro;

    @Etiqueta("BCI")
    private String bci;

    @OrderBy("dataOperacao")
    @OneToMany(mappedBy = "bem")
    private List<EventoBem> eventosBem;

    @OneToMany(mappedBy = "bem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BemNotaFiscal> notasFiscais;

    @NotAudited
    @OneToMany(mappedBy = "bem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentoBloqueioBem> movimentosBloqueioBem;

    /* Atributos Transients para lista*/
    @Transient
    @Tabelavel
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;

    @Transient
    @Tabelavel
    @Etiqueta("Grupo Objeto de Compra")
    private GrupoObjetoCompra grupoObjetoCompra;

    @Transient
    @Tabelavel
    @Etiqueta("Unidade Administrativa")
    private String administrativa;

    @Transient
    private EventoBem eventoBem;

    @Tabelavel
    @Etiqueta("Unidade Orçamentária")
    @Transient
    private String orcamentaria;

    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta("Tipo de Aquisição")
    @Enumerated(EnumType.STRING)
    @Transient
    private TipoAquisicaoBem tipoAquisicaoBem;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta("Estado de Conservação")
    @Enumerated(EnumType.STRING)
    private EstadoConservacaoBem estadoBem;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta("Situação de Conservação")
    @Enumerated(EnumType.STRING)
    private SituacaoConservacaoBem situacaoConservacaoBem;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta("Tipo Grupo")
    @Enumerated(EnumType.STRING)
    private TipoGrupo tipoGrupo;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor Original")
    @Monetario
    private BigDecimal valorOriginal;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor da Depreciação")
    private BigDecimal valorAcumuladoDaDepreciacao;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor da Amortização")
    private BigDecimal valorAcumuladoDaAmortizacao;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor da Exaustão")
    private BigDecimal valorAcumuladoDaExaustao;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor de Ajuste de Perda")
    private BigDecimal valorAcumuladoDeAjuste;

    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor Líquido")
    private BigDecimal valorLiquido;

    @Transient
    @Etiqueta("Localização")
    private String localizacao;

    @Transient
    private Long idUltimoEstado;

    @Transient
    private EstadoBem ultimoEstado;

    @Transient
    private EstadoBem estadoInicial;

    @Transient
    private TipoReducao tipoReducao;

    /* Campos utilizado para ajustar valor do bem no (Original, Depreciação e Amortização) nas telas de ajuste do bem*/
    @Transient
    @Monetario
    private BigDecimal valorAjuste;

    @Transient
    @Monetario
    private BigDecimal valorAjustadoBem;

    public Bem() {
        movimentosBloqueioBem = Lists.newArrayList();
        inicializarValoresAcumulados();
    }

    public Bem(CaracterizadorDeBemImovel cdb) {
        super();
        this.descricao = cdb.getDescricaoDoBem();
        this.identificacao = cdb.getCodigoPatrimonio();
        this.valorOriginal = cdb.getValorDoBem();
        this.dataAquisicao = cdb.getDataLancamento();
        this.marca = cdb.getMarca();
        this.modelo = cdb.getModelo();
        this.observacao = cdb.getObservacao();
        this.detentorOrigemRecurso = cdb.getDetentorOrigemRecurso();
        this.condicaoDeOcupacao = cdb.getCondicaoDeOcupacao();
        this.fornecedor = cdb.getFornecedor();
        this.numeroRegistro = cdb.getNumeroRegistro();
        this.bci = cdb.getBci();
        popularNotasFiscais(cdb);
        inicializarValoresAcumulados();
    }

    public Bem(CaracterizadorDeBemMovel cdb) {
        super();
        this.descricao = cdb.getDescricaoDoBem();
        this.identificacao = cdb.getCodigoPatrimonio();
        this.valorOriginal = cdb.getValorDoBem();
        this.dataAquisicao = cdb.getDataLancamento();
        this.marca = cdb.getMarca();
        this.modelo = cdb.getModelo();
        this.objetoCompra = cdb.getObjetoCompra();
        this.registroAnterior = cdb.getRegistroAnterior();
        this.observacao = cdb.getObservacao();
        this.detentorOrigemRecurso = cdb.getDetentorOrigemRecurso();
        this.fornecedor = cdb.getFornecedor();
        popularNotasFiscais(cdb);
        inicializarValoresAcumulados();
    }

    /* Construtor para lista */
    public Bem(Bem bem, EstadoBem estado, String administrativa, String orcamentaria) {
        this.setId(bem.getId());
        preencherDadosDoBem(bem);
        atribuirValorOriginalEAcumulados(estado);
        this.preencherDadosTrasientsDoBem(this, estado);
        this.administrativa = administrativa;
        this.orcamentaria = orcamentaria;
    }

    public Bem(Long id) {
        this.id = id;
    }

    public static List<Bem> preencherListaDeBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] bem : objetosDaConsulta) {
            BigDecimal id = (BigDecimal) bem[0];
            Bem b = bensCache.get(id);
            if (b == null) {
                b = new Bem();
                bensCache.put(id, b);
            }
            b.setId(id.longValue());
            b.setIdentificacao((String) bem[1]);
            b.setDescricao((String) bem[2]);
            b.setValorOriginal((BigDecimal) bem[3]);
            b.setValorAcumuladoDaAmortizacao((BigDecimal) bem[4]);
            b.setValorAcumuladoDaDepreciacao((BigDecimal) bem[5]);
            b.setValorAcumuladoDaExaustao((BigDecimal) bem[6]);
            b.setValorAcumuladoDeAjuste((BigDecimal) bem[7]);
            b.setIdUltimoEstado(((BigDecimal) bem[8]).longValue());
            b.setDataAquisicao(((Date) bem[10]));
            retornaBens.add(b);
        }
        return retornaBens;
    }

    private void popularNotasFiscais(CaracterizadorDeBemImovel cdb) {
        List<BemNotaFiscal> notasFiscais = cdb.getNotasFiscais();
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            for (BemNotaFiscal bemNotaFiscal : notasFiscais) {
                bemNotaFiscal.setBem(this);
            }
        }
        this.setNotasFiscais(notasFiscais);
    }

    private void popularNotasFiscais(CaracterizadorDeBemMovel cdb) {
        List<BemNotaFiscal> notasFiscais = cdb.getNotasFiscais();
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            for (BemNotaFiscal bemNotaFiscal : notasFiscais) {
                bemNotaFiscal.setBem(this);
            }
        }
        this.setNotasFiscais(notasFiscais);
    }

    private void inicializarValoresAcumulados() {
        this.valorAcumuladoDaAmortizacao = BigDecimal.ZERO;
        this.valorAcumuladoDaDepreciacao = BigDecimal.ZERO;
        this.valorAcumuladoDaExaustao = BigDecimal.ZERO;
        this.valorAcumuladoDeAjuste = BigDecimal.ZERO;
    }

    public BigDecimal getValorOriginal() {
        if (valorOriginal == null) {
            valorOriginal = BigDecimal.ZERO;
        }
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorAcumuladoDeAjuste() {
        if (valorAcumuladoDeAjuste == null) {
            valorAcumuladoDeAjuste = BigDecimal.ZERO;
        }
        return valorAcumuladoDeAjuste;
    }

    public void setValorAcumuladoDeAjuste(BigDecimal valorAcumuladoDeAjuste) {
        this.valorAcumuladoDeAjuste = valorAcumuladoDeAjuste;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste != null ? valorAjuste : BigDecimal.ZERO;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<EventoBem> getEventosBem() {
        if (eventosBem != null && !eventosBem.isEmpty()) {
            Collections.sort(eventosBem);
        }
        return eventosBem;
    }

    public void setEventosBem(List<EventoBem> eventosBem) {
        this.eventosBem = eventosBem;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public String getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(String administrativa) {
        this.administrativa = administrativa;
    }

    public EventoBem getEventoBem() {
        return eventoBem;
    }

    public void setEventoBem(EventoBem eventoBem) {
        this.eventoBem = eventoBem;
    }

    public String getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(String orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public EstadoConservacaoBem getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(EstadoConservacaoBem estadoBem) {
        this.estadoBem = estadoBem;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public BigDecimal getValorAcumuladoDaDepreciacao() {
        return valorAcumuladoDaDepreciacao;
    }

    public void setValorAcumuladoDaDepreciacao(BigDecimal valorAcumuladoDaDepreciacao) {
        this.valorAcumuladoDaDepreciacao = valorAcumuladoDaDepreciacao;
    }

    public BigDecimal getValorAcumuladoDaAmortizacao() {
        return valorAcumuladoDaAmortizacao;
    }

    public void setValorAcumuladoDaAmortizacao(BigDecimal valorAcumuladoDaAmortizacao) {
        this.valorAcumuladoDaAmortizacao = valorAcumuladoDaAmortizacao;
    }

    public BigDecimal getValorAcumuladoDaExaustao() {
        return valorAcumuladoDaExaustao;
    }

    public void setValorAcumuladoDaExaustao(BigDecimal valorAcumuladoDaExaustao) {
        this.valorAcumuladoDaExaustao = valorAcumuladoDaExaustao;
    }

    public Long getIdUltimoEstado() {
        return idUltimoEstado;
    }

    public void setIdUltimoEstado(Long idUltimoEstado) {
        this.idUltimoEstado = idUltimoEstado;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Garantia getGarantia() {
        return garantia;
    }

    public void setGarantia(Garantia garantia) {
        this.garantia = garantia;
    }

    public Seguradora getSeguradora() {
        return seguradora;
    }

    public void setSeguradora(Seguradora seguradora) {
        this.seguradora = seguradora;
    }

    public EstadoBem getUltimoEstado() {
        return ultimoEstado;
    }

    public void setUltimoEstado(EstadoBem ultimoEstado) {
        this.ultimoEstado = ultimoEstado;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }

    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return detentorOrigemRecurso;
    }

    public void setDetentorOrigemRecurso(DetentorOrigemRecurso detentorOrigemRecurso) {
        this.detentorOrigemRecurso = detentorOrigemRecurso;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public CondicaoDeOcupacao getCondicaoDeOcupacao() {
        return condicaoDeOcupacao;
    }

    public void setCondicaoDeOcupacao(CondicaoDeOcupacao condicaoDeOcupacao) {
        this.condicaoDeOcupacao = condicaoDeOcupacao;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getBci() {
        return bci;
    }

    public void setBci(String bci) {
        this.bci = bci;
    }

    public TipoReducao getTipoReducao() {
        return tipoReducao;
    }

    public void setTipoReducao(TipoReducao tipoReducao) {
        this.tipoReducao = tipoReducao;
    }

    public List<MovimentoBloqueioBem> getMovimentosBloqueioBem() {
        return movimentosBloqueioBem;
    }

    public void setMovimentosBloqueioBem(List<MovimentoBloqueioBem> movimentosBloqueioBem) {
        this.movimentosBloqueioBem = movimentosBloqueioBem;
    }

    @Override
    public String toString() {
        return identificacao + (registroAnterior != null ? " - " + registroAnterior : "") + " - " + descricao;
    }

    public String getDescricaoParaAutoComplete() {
        String texto = "";
        if (identificacao != null && !"".equals(identificacao)) {
            texto = identificacao;
        }
        if (descricao != null && !"".equals(descricao)) {
            texto += " - " + descricao.substring(0, descricao.length() > 85 ? 85 : descricao.length());
        }
        return texto;
    }

    public List<BemNotaFiscal> getNotasFiscais() {
        return notasFiscais;
    }

    public void setNotasFiscais(List<BemNotaFiscal> notasFiscais) {
        this.notasFiscais = notasFiscais;
    }

    private BigDecimal getValorReduzivel(TipoReducao tipoReducao) {
        BigDecimal valorResidual = this.valorOriginal.multiply(tipoReducao.getValorResidual()).divide(CEM, 4, RoundingMode.HALF_UP);
        return this.valorOriginal.subtract(valorResidual);
    }

    public BigDecimal getValorDaReducao(TipoReducao tipoReducao) {
        /*
         *FÓRMULA DA REDUÇÃO DE VALOR MENSAL
         *((Valor Original - Valor Residual) x (100 / Vida Útil) / 100) / 12 (meses do ano)
         */

        BigDecimal taxaReducaoAnual = CEM.divide(tipoReducao.getVidaUtilEmAnos(), 4, RoundingMode.HALF_UP);

        BigDecimal valorReducaoMensal = getValorReduzivel(tipoReducao).multiply(taxaReducaoAnual).divide(CEM, 4, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);

        return valorReducaoMensal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularReducaoValorBem(EstadoBem estadoResultante, TipoReducao tipoReducao) {
        BigDecimal valorDaReducao = getValorDaReducao(tipoReducao);
        if (tipoReducao != null && tipoReducao.getValorResidual() != null) {

            BigDecimal valorMinimoRedutivel = getValorMinimoRedutivel(tipoReducao);

            if (getValorLiquido().subtract(valorDaReducao).compareTo(valorMinimoRedutivel) < 0) {
                valorDaReducao = getValorLiquido().subtract(valorMinimoRedutivel);
            }
        }
        BigDecimal novoValorAcumuladoDaReducao = getNovoValorAcumuladoDaReducao(tipoReducao.getTipoReducaoValorBem(), valorDaReducao, false);
        estadoResultante.atualizarValorAcumuladoDaReducao(tipoReducao.getTipoReducaoValorBem(), novoValorAcumuladoDaReducao);
        return valorDaReducao;
    }

    public BigDecimal getValorMinimoRedutivel(TipoReducao tipoReducao) {
        BigDecimal taxaReducao = tipoReducao.getValorResidual().divide(CEM, 4, RoundingMode.HALF_EVEN);
        return valorOriginal.multiply(taxaReducao).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getNovoValorAcumuladoDaReducao(TipoReducaoValorBem tipoReducaoValorBem, BigDecimal valorDaReducao, boolean estorno) {
        switch (tipoReducaoValorBem) {
            case AMORTIZACAO:
                return estorno ? valorAcumuladoDaAmortizacao.subtract(valorDaReducao) : valorAcumuladoDaAmortizacao.add(valorDaReducao);
            case DEPRECIACAO:
                return estorno ? valorAcumuladoDaDepreciacao.subtract(valorDaReducao) : valorAcumuladoDaDepreciacao.add(valorDaReducao);
            case EXAUSTAO:
                return estorno ? valorAcumuladoDaExaustao.subtract(valorDaReducao) : valorAcumuladoDaExaustao.add(valorDaReducao);
            default:
                throw new ExcecaoNegocioGenerica("Nenhum valor acumulado encontrado para o tipo de redução " + tipoReducaoValorBem.getDescricao());
        }
    }

    public BigDecimal getValorDosAjustes() {
        BigDecimal ajustes = BigDecimal.ZERO;

        ajustes = ajustes.add(this.getValorAcumuladoDaAmortizacao() != null ? this.getValorAcumuladoDaAmortizacao() : BigDecimal.ZERO);
        ajustes = ajustes.add(this.getValorAcumuladoDaDepreciacao() != null ? this.getValorAcumuladoDaDepreciacao() : BigDecimal.ZERO);
        ajustes = ajustes.add(this.getValorAcumuladoDaExaustao() != null ? this.getValorAcumuladoDaExaustao() : BigDecimal.ZERO);
        ajustes = ajustes.add(this.getValorAcumuladoDeAjuste() != null ? this.getValorAcumuladoDeAjuste() : BigDecimal.ZERO);

        return ajustes;
    }

    public void estornarReducao(BigDecimal valor, TipoReducaoValorBem tipoReducaoValorBem, EstadoBem estadoResultante) {
        atribuirValorOriginalEAcumulados(estadoResultante);
        estadoResultante.atualizarValorAcumuladoDaReducao(tipoReducaoValorBem, getNovoValorAcumuladoDaReducao(tipoReducaoValorBem, valor, true));
    }

    public BigDecimal getValorLiquido() {
        valorLiquido = BigDecimal.ZERO;
        valorLiquido = valorLiquido.add(valorOriginal != null ? valorOriginal : BigDecimal.ZERO);

        valorLiquido = valorLiquido.subtract(valorAcumuladoDaAmortizacao != null ? valorAcumuladoDaAmortizacao : BigDecimal.ZERO);
        valorLiquido = valorLiquido.subtract(valorAcumuladoDaDepreciacao != null ? valorAcumuladoDaDepreciacao : BigDecimal.ZERO);
        valorLiquido = valorLiquido.subtract(valorAcumuladoDaExaustao != null ? valorAcumuladoDaExaustao : BigDecimal.ZERO);

        valorLiquido = valorLiquido.subtract(valorAcumuladoDeAjuste != null ? valorAcumuladoDeAjuste : BigDecimal.ZERO);

        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public void atribuirValorOriginalEAcumulados(EstadoBem estadoInicial) {
        setValorAcumuladoDaAmortizacao(estadoInicial.getValorAcumuladoDaAmortizacao());
        setValorAcumuladoDaDepreciacao(estadoInicial.getValorAcumuladoDaDepreciacao());
        setValorAcumuladoDaExaustao(estadoInicial.getValorAcumuladoDaExaustao());
        setValorAcumuladoDeAjuste(estadoInicial.getValorAcumuladoDeAjuste());
        setValorOriginal(estadoInicial.getValorOriginal());
    }

    public static void preencherDadosTrasientsDoBem(Bem b, EstadoBem estadoBem) {
        b.setValorAcumuladoDaAmortizacao(estadoBem.getValorAcumuladoDaAmortizacao());
        b.setValorAcumuladoDaDepreciacao(estadoBem.getValorAcumuladoDaDepreciacao());
        b.setValorAcumuladoDaExaustao(estadoBem.getValorAcumuladoDaExaustao());
        b.setValorAcumuladoDeAjuste(estadoBem.getValorAcumuladoDeAjuste());
        b.setIdUltimoEstado(estadoBem.getId());
        b.setUltimoEstado(estadoBem);
        b.setGrupoBem(estadoBem.getGrupoBem());
        b.setSituacaoConservacaoBem(estadoBem.getSituacaoConservacaoBem());
        b.setEstadoBem(estadoBem.getEstadoBem());
        b.setTipoAquisicaoBem(estadoBem.getTipoAquisicaoBem());
        b.setGrupoObjetoCompra(estadoBem.getGrupoObjetoCompra());
        b.setTipoGrupo(estadoBem.getTipoGrupo());
        b.setValorLiquido(estadoBem.getValorLiquido());
        b.setLocalizacao(estadoBem.getLocalizacao());
        b.setValorOriginal(estadoBem.getValorOriginal());
    }

    public void preencherDadosDoBem(Bem b) {
        this.setIdentificacao(b.getIdentificacao());
        this.setDataAquisicao(b.getDataAquisicao());
        this.setDescricao(b.getDescricao());
        this.setMarca(b.getMarca());
        this.setModelo(b.getModelo());
        this.setSeguradora(b.getSeguradora());
        this.setGarantia(b.getGarantia());
        this.setObjetoCompra(b.getObjetoCompra());
        this.setRegistroAnterior(b.getRegistroAnterior());
        this.setObservacao(b.getObservacao());
        this.setDetentorOrigemRecurso(b.getDetentorOrigemRecurso());
        this.setFornecedor(b.getFornecedor());
        this.setCondicaoDeOcupacao(b.getCondicaoDeOcupacao());
        this.setBci(b.getBci());
        this.setNumeroRegistro(b.getNumeroRegistro());
    }

    @Override
    public int compareTo(Bem o) {
        return Long.valueOf(this.getIdentificacao()).compareTo(Long.valueOf(o.getIdentificacao()));
    }

    public EstadoBem getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoBem estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public BigDecimal getValorAjustadoBem() {
        return valorAjustadoBem;
    }

    public void setValorAjustadoBem(BigDecimal valorAjustadoBem) {
        this.valorAjustadoBem = valorAjustadoBem;
    }

    public BigDecimal getValorBem(TipoAjusteBemMovel tipoAjusteBemMovel) {
        if (tipoAjusteBemMovel.isOriginal()) {
            return getValorOriginal();

        } else if (tipoAjusteBemMovel.isDepreciacao()) {
            return getValorAcumuladoDaDepreciacao();

        } else {
            return getValorAcumuladoDaAmortizacao();
        }
    }

    public boolean validarDataLancamentoPosteriorAProximoMovimentacao(Date dataLancamento, EventoBem ultimoEvento) {
        if (DataUtil.dataSemHorario(dataLancamento).before(DataUtil.dataSemHorario(ultimoEvento.getDataLancamento()))) {
            return false;
        }
        return true;
    }
}
