/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CabecalhoRelatorio;
import br.com.webpublico.enums.TargetListaRelatorio;
import br.com.webpublico.enums.TipoFolha;
import br.com.webpublico.enums.TipoFonteRelatorio;
import br.com.webpublico.enums.TipoRelatorio;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Romanini
 */
@Entity

@Audited
@Etiqueta("Configuração de Relatório")
public class RelatorioPesquisaGenerico implements Serializable {

    public static final String SUMMARY = "Não foi possível salvar!";
    public static final int PROFUNDIDADE_BUSCA_RELATORIO = 1;
    public static final int TAMANHO_FONTE = 15;
    public static final int TAMANHO_DE_CARACTERES_CONDICAO = 255;
    public static final int TAMANHO_DE_CARACTERES_LABEL = 70;
    public static final String COR_FUNDO_TITULO_TABELA = "6E95A6";
    public static final String COR_FUNDO_ZEBRADO_1 = "d7e3e9";
    public static final String COR_FUNDO_ZEBRADO_2 = "FFFFFF";
    public static final String COR_BRANCO = "FFFFFF";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta(value = "Código")
    @Pesquisavel
    @Tabelavel
    private Long codigo;
    @Etiqueta(value = "Nome")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String nomeRelatorio;
    @Etiqueta(value = "Título")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String titulo;
    @OneToMany(mappedBy = "relatorioPesquisaGenerico", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Where(clause = "targetListaRelatorio = 'SOURCE'")
    @OrderBy(clause = "ordemExibicao asc")
    private List<AtributoRelatorioGenerico> source;
    @OneToMany(mappedBy = "relatorioPesquisaGenerico", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Where(clause = "targetListaRelatorio = 'TARGET'")
    @OrderBy(clause = "ordemExibicao asc")
    private List<AtributoRelatorioGenerico> target;
    @ManyToOne
    private CabecalhoRelatorio cabecalhoRelatorio;
    @Etiqueta(value = "Padrão?")
    @Pesquisavel
    @Tabelavel
    private Boolean padrao;
    @Etiqueta(value = "Colorido")
    @Pesquisavel
    @Tabelavel
    private Boolean colorido;
    @Etiqueta(value = "Mostrar Rodapé")
    @Pesquisavel
    @Tabelavel
    private Boolean mostrarRodape;
    @Etiqueta(value = "Profundidade")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer profundidade;
    @Etiqueta(value = "Classe")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String classe;
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo de Folha")
    @Pesquisavel
    @Tabelavel
    private TipoFolha tipoFolha;
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo de Fonte")
    @Pesquisavel
    @Tabelavel
    private TipoFonteRelatorio tipoFonteRelatorio;
    @Etiqueta(value = "Tamanho da Fonte")
    @Pesquisavel
    @Tabelavel
    private Integer tamanhoDaFonte;
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo de Relatório")
    @Pesquisavel
    @Tabelavel
    private TipoRelatorio tipoRelatorio;
    @Etiqueta(value = "Agrupador")
    @Pesquisavel
    @Tabelavel
    private Boolean agrupador;
    @Etiqueta(value = "Mostrar Totalizador")
    @Pesquisavel
    @Tabelavel
    private Boolean mostrarTotalizador;
    @Etiqueta(value = "Mostrar Detalhes")
    @Pesquisavel
    @Tabelavel
    private Boolean mostrarDetalhes;
    private Boolean imprimirTodosRegistros;
    private String corFundoTituloTabela;
    private String corFundoZebrado1;
    private String corFundoZebrado2;
    private String corFundoAgrupador;
    private String corFundoTotalizador;
    private String observacao;
    @Transient
    private String cabecalhoMesclado;
    @Transient
    private EntidadeMetaData entidadeMetaData;
    @Transient
    private Object objetoSelecionadoRelatorio;
    @Transient
    private AtributoRelatorioGenerico[] atributosSelecionadosRelatorioTabelaSource;
    @Transient
    private AtributoRelatorioGenerico[] atributosSelecionadosRelatorioTabelaTarget;
    @Transient
    private Long criadoEm;

    public RelatorioPesquisaGenerico() {
        criadoEm = System.nanoTime();
        imprimirTodosRegistros = Boolean.FALSE;
    }

    public RelatorioPesquisaGenerico(Object objeto, EntidadeMetaData entidadeMetaData) throws Exception {
        this.objetoSelecionadoRelatorio = objeto;
        this.entidadeMetaData = entidadeMetaData;
        recuperarAtributos();
    }

    public void recuperarAtributos() throws Exception {
        recuperarEntidadeMetaData();
        this.titulo = "Relatório de " + this.getEntidadeMetaData().getNomeEntidade();
        this.profundidade = PROFUNDIDADE_BUSCA_RELATORIO;
        this.tamanhoDaFonte = TAMANHO_FONTE;
        this.classe = entidadeMetaData.getEntidade().getName();
        this.nomeRelatorio = entidadeMetaData.getNomeEntidade();
        this.colorido = false;
        this.mostrarRodape = true;
        this.padrao = true;
        this.imprimirTodosRegistros = Boolean.FALSE;
        this.tipoFolha = TipoFolha.PAISAGEM;
        this.agrupador = false;
        this.mostrarDetalhes = true;
        this.mostrarTotalizador = false;
        this.atributosSelecionadosRelatorioTabelaSource = null;
        this.atributosSelecionadosRelatorioTabelaTarget = null;
        //this.profundidade = (Integer.valueOf(this.PROFUNDIDADE_BUSCA_RELATORIO));
        alterarCoresParaPadrao();
        this.setTipoRelatorio(TipoRelatorio.TABELA);
        recuperarAtributosSourceTarget();

    }

    public void recuperarEntidadeMetaData() {
        if (classe != null) {
            Class classeRecuperada = getClasse(this.classe.replace("class ", ""));
            this.setEntidadeMetaData(new EntidadeMetaData(classeRecuperada));
        }
    }

    public Class getClasse(String classe) {
        try {
            return Class.forName(classe.replace("sistemasprefeitura", "webpublico"));
        } catch (Exception e) {
            return null;
        }
    }

    public void recuperarAtributosSourceTarget() throws Exception {
        //try {
        if (entidadeMetaData == null) {
            recuperarEntidadeMetaData();
        }
        this.source = buscaAtributosObjetoRelatorioGenericoSource(entidadeMetaData);
        this.target = buscaAtributosObjetoRelatorioGenericoTarget(entidadeMetaData, profundidade);
        /*} catch (Exception e) {
            FacesUtil.addError("Erro!", e.getMessage());
        }*/
    }

    public Boolean getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(Boolean agrupador) {
        this.agrupador = agrupador;
    }

    public AtributoRelatorioGenerico[] getAtributosSelecionadosRelatorioTabelaSource() {
        return atributosSelecionadosRelatorioTabelaSource;
    }

    public void setAtributosSelecionadosRelatorioTabelaSource(AtributoRelatorioGenerico[] atributosSelecionadosRelatorioTabelaSource) {
        this.atributosSelecionadosRelatorioTabelaSource = atributosSelecionadosRelatorioTabelaSource;
    }

    public AtributoRelatorioGenerico[] getAtributosSelecionadosRelatorioTabelaTarget() {
        return atributosSelecionadosRelatorioTabelaTarget;
    }

    public void setAtributosSelecionadosRelatorioTabelaTarget(AtributoRelatorioGenerico[] atributosSelecionadosRelatorioTabelaTarget) {
        this.atributosSelecionadosRelatorioTabelaTarget = atributosSelecionadosRelatorioTabelaTarget;
    }

    public String getNomeRelatorio() {
        return nomeRelatorio;
    }

    public void setNomeRelatorio(String nomeRelatorio) {
        this.nomeRelatorio = nomeRelatorio;
    }

    public Object getObjetoSelecionadoRelatorio() {
        return objetoSelecionadoRelatorio;
    }

    public void setObjetoSelecionadoRelatorio(Object objetoSelecionadoRelatorio) {
        this.objetoSelecionadoRelatorio = objetoSelecionadoRelatorio;
    }

    public CabecalhoRelatorio getCabecalhoRelatorio() {
        return cabecalhoRelatorio;
    }

    public void setCabecalhoRelatorio(CabecalhoRelatorio cabecalhoRelatorio) {
        this.cabecalhoRelatorio = cabecalhoRelatorio;
    }

    public String getCabecalhoMesclado() {
        return cabecalhoMesclado;
    }

    public void setCabecalhoMesclado(String cabecalhoMesclado) {
        this.cabecalhoMesclado = cabecalhoMesclado;
    }

    public EntidadeMetaData getEntidadeMetaData() {
        if (entidadeMetaData == null) {
            recuperarEntidadeMetaData();
        }
        return entidadeMetaData;
    }

    public void setEntidadeMetaData(EntidadeMetaData entidadeMetaData) {
        this.entidadeMetaData = entidadeMetaData;
    }

    public Boolean getColorido() {
        return colorido;
    }

    public void setColorido(Boolean colorido) {
        this.colorido = colorido;
    }

    public TipoFolha getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(TipoFolha tipoFolha) {
        this.tipoFolha = tipoFolha;
    }

    public Boolean getMostrarRodape() {
        return mostrarRodape;
    }

    public void setMostrarRodape(Boolean mostrarRodape) {
        this.mostrarRodape = mostrarRodape;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public List<AtributoRelatorioGenerico> getSource() {
        return source;
    }

    public void setSource(List<AtributoRelatorioGenerico> source) {
        this.source = source;
    }

    public List<AtributoRelatorioGenerico> getTarget() {
        return target;
    }

    public void setTarget(List<AtributoRelatorioGenerico> target) {
        this.target = target;
    }

    public Integer getProfundidade() {
        return profundidade;
    }

    public void setProfundidade(Integer profundidade) {
        this.profundidade = profundidade;
    }

    public Boolean getMostrarTotalizador() {
        return mostrarTotalizador;
    }

    public void setMostrarTotalizador(Boolean mostrarTotalizador) {
        this.mostrarTotalizador = mostrarTotalizador;
    }

    public Boolean getMostrarDetalhes() {
        return mostrarDetalhes;
    }

    public void setMostrarDetalhes(Boolean mostrarDetalhes) {
        this.mostrarDetalhes = mostrarDetalhes;
    }

    public TipoFonteRelatorio getTipoFonteRelatorio() {
        return tipoFonteRelatorio;
    }

    public void setTipoFonteRelatorio(TipoFonteRelatorio tipoFonteRelatorio) {
        this.tipoFonteRelatorio = tipoFonteRelatorio;
    }

    public Integer getTamanhoDaFonte() {
        return tamanhoDaFonte;
    }

    public void setTamanhoDaFonte(Integer tamanhoDaFonte) {
        this.tamanhoDaFonte = tamanhoDaFonte;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCorFundoTituloTabela() {
        return corFundoTituloTabela;
    }

    public void setCorFundoTituloTabela(String corFundoTituloTabela) {
        this.corFundoTituloTabela = corFundoTituloTabela;
    }

    public String getCorFundoZebrado1() {
        return corFundoZebrado1;
    }

    public void setCorFundoZebrado1(String corFundoZebrado1) {
        this.corFundoZebrado1 = corFundoZebrado1;
    }

    public String getCorFundoZebrado2() {
        return corFundoZebrado2;
    }

    public void setCorFundoZebrado2(String corFundoZebrado2) {
        this.corFundoZebrado2 = corFundoZebrado2;
    }

    public String getCorFundoAgrupador() {
        return corFundoAgrupador;
    }

    public void setCorFundoAgrupador(String corFundoAgrupador) {
        this.corFundoAgrupador = corFundoAgrupador;
    }

    public String getCorFundoTotalizador() {
        return corFundoTotalizador;
    }

    public void setCorFundoTotalizador(String corFundoTotalizador) {
        this.corFundoTotalizador = corFundoTotalizador;
    }

    public Boolean getPadrao() {
        return padrao;
    }

    public void setPadrao(Boolean padrao) {
        this.padrao = padrao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getImprimirTodosRegistros() {
        return imprimirTodosRegistros;
    }

    public void setImprimirTodosRegistros(Boolean imprimirTodosRegistros) {
        this.imprimirTodosRegistros = imprimirTodosRegistros;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void atribuiAtributoDoRelatorioProRelatorio() {

        int indiceSource = 0;
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : source) {
            atributoRelatorioGenerico.setTargetListaRelatorio(TargetListaRelatorio.SOURCE);
            atributoRelatorioGenerico.setRelatorioPesquisaGenerico(this);
            atributoRelatorioGenerico.setOrdemExibicao(indiceSource);
            indiceSource++;
        }

        int indiceTarget = 0;
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : target) {
            atributoRelatorioGenerico.setTargetListaRelatorio(TargetListaRelatorio.TARGET);
            atributoRelatorioGenerico.setRelatorioPesquisaGenerico(this);
            atributoRelatorioGenerico.setOrdemExibicao(indiceTarget);
            indiceTarget++;
        }
    }

    private List<AtributoRelatorioGenerico> buscaAtributosObjetoRelatorioGenericoSource(EntidadeMetaData emd) {
        return getAtributosTabelaveisQueNaoPodeAdicionar(emd);
    }

    private List<AtributoRelatorioGenerico> buscaAtributosObjetoRelatorioGenericoTarget(EntidadeMetaData emd, Integer profundidade) throws Exception {
        List<AtributoRelatorioGenerico> target = getAtributosTabelaveisQuePodeAdicionar(emd);
        int nivel = 1;

        List<AtributoRelatorioGenerico> atributosDeEntidade = new ArrayList<>();
        List<AtributoRelatorioGenerico> atributosParaRemover = new ArrayList<>();
        for (int i = 0; i < target.size(); i++) {
            AtributoRelatorioGenerico atributo = target.get(i);
            atributo.setTargetListaRelatorio(TargetListaRelatorio.TARGET);
            if (atributo.getAtributoDeEntidade() && !atributo.getAtributoDeLista()) {
                Class c = null;
                try {
                    c = Class.forName(atributo.getClasse());
                } catch (ClassNotFoundException ex) {
                    //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro !", "Classe não encontrada : " + atributo.getClasse()));
                }
                if (c == null) {
                    throw new Exception("Classe " + atributo.getClasse() + " não foi possível ser instanciada.");
                }
                EntidadeMetaData em = new EntidadeMetaData(c);
                List<AtributoRelatorioGenerico> atributosImprimiveis = getAtributosTabelaveisQuePodeAdicionar(em);
                for (AtributoRelatorioGenerico atributoRelatorioPesquisaGenerico : atributosImprimiveis) {
                    atributoRelatorioPesquisaGenerico.setCondicao(atributo.getCondicao() + "." + atributoRelatorioPesquisaGenerico.getCondicao());
                    atributoRelatorioPesquisaGenerico.setLabel(atributo.getLabel() + "." + atributoRelatorioPesquisaGenerico.getLabel());
                }
                atributosDeEntidade.addAll(atributosImprimiveis);
                if (!atributosImprimiveis.isEmpty()) {
                    atributosParaRemover.add(atributo);
                }
            }
            if (i == target.size() - 1) {
                if (nivel != profundidade) {
                    target.addAll(atributosDeEntidade);
                    atributosDeEntidade.clear();

                    target.removeAll(atributosParaRemover);
                    atributosParaRemover.clear();
                    nivel++;
                }
                if (nivel == profundidade) {
                    break;
                }
            }
        }
        return target;
    }

    public List<AtributoRelatorioGenerico> getAtributosTabelaveisQueNaoPodeAdicionar(EntidadeMetaData entidadeMetaData) {
        List<AtributoRelatorioGenerico> retorno = new ArrayList<>();
        List<AtributoMetadata> atributosTabelaveis = entidadeMetaData.getAtributosTabelaveis();
        for (AtributoMetadata atributoMetadata : atributosTabelaveis) {
            boolean podeAdicionar = false;
            if (atributoMetadata.getAtributo().isAnnotationPresent(Tabelavel.class)) {
                Tabelavel annotation = atributoMetadata.getAtributo().getAnnotation(Tabelavel.class);
                if (annotation.campoSelecionado()) {
                    podeAdicionar = true;
                }
            }
            if (!podeAdicionar) {
                AtributoRelatorioGenerico atributo = new AtributoRelatorioGenerico().instanciaAtributoRelatorioGenerico(atributoMetadata);
                atributo.setTargetListaRelatorio(TargetListaRelatorio.TARGET);
                retorno.add(atributo);
            }
        }
        return retorno;
    }

    public List<AtributoRelatorioGenerico> getAtributosTabelaveisQuePodeAdicionar(EntidadeMetaData entidadeMetaData) {
        List<AtributoRelatorioGenerico> retorno = new ArrayList<>();
        List<AtributoMetadata> atributosTabelaveis = entidadeMetaData.getAtributosTabelaveis();
        for (AtributoMetadata atributoMetadata : atributosTabelaveis) {
            boolean podeAdicionar = false;
            if (atributoMetadata.getAtributo().isAnnotationPresent(Tabelavel.class)) {
                Tabelavel annotation = atributoMetadata.getAtributo().getAnnotation(Tabelavel.class);
                if (annotation.campoSelecionado()) {
                    podeAdicionar = true;
                }
            }
            if (podeAdicionar) {
                AtributoRelatorioGenerico atributo = new AtributoRelatorioGenerico().instanciaAtributoRelatorioGenerico(atributoMetadata);
                atributo.setTargetListaRelatorio(TargetListaRelatorio.TARGET);
                retorno.add(atributo);
            }
        }
        return retorno;
    }

    public void alteraCampoParaCima() {
        for (AtributoRelatorioGenerico atributoSelecionado : this.getAtributosSelecionadosRelatorioTabelaTarget()) {
            try {
                int indiceAtual = this.getTarget().indexOf(atributoSelecionado);
                if (indiceAtual != 0) {
                    this.getTarget().remove(atributoSelecionado);
                    this.getTarget().add(indiceAtual - 1, atributoSelecionado);
                }

            } catch (Exception e) {
            }
        }
    }

    public void alteraCampoParaBaixo() {
        for (AtributoRelatorioGenerico atributoSelecionado : this.getAtributosSelecionadosRelatorioTabelaTarget()) {
            try {
                int indiceAtual = this.getTarget().indexOf(atributoSelecionado);
                if (indiceAtual != this.getTarget().size() - 1) {
                    this.getTarget().remove(atributoSelecionado);
                    this.getTarget().add(indiceAtual + 1, atributoSelecionado);
                }

            } catch (Exception e) {
            }
        }
    }

    public void alteraCampoPrimeiroDaLista() {
        for (AtributoRelatorioGenerico atributoSelecionado : this.getAtributosSelecionadosRelatorioTabelaTarget()) {
            try {
                int indiceAtual = this.getTarget().indexOf(atributoSelecionado);
                if (indiceAtual != 0) {
                    this.getTarget().remove(atributoSelecionado);
                    this.getTarget().add(0, atributoSelecionado);
                }
            } catch (Exception e) {
            }
        }
    }

    public void removerAtributosTabela() {
        if (atributosSelecionadosRelatorioTabelaTarget != null) {
            for (int i = 0; i < this.getAtributosSelecionadosRelatorioTabelaTarget().length; i++) {
                this.getTarget().remove(this.getAtributosSelecionadosRelatorioTabelaTarget()[i]);
                this.getAtributosSelecionadosRelatorioTabelaTarget()[i].setTargetListaRelatorio(TargetListaRelatorio.SOURCE);
                this.getAtributosSelecionadosRelatorioTabelaTarget()[i].setId(null);
                this.getSource().add(this.getAtributosSelecionadosRelatorioTabelaTarget()[i]);
            }
            this.setAtributosSelecionadosRelatorioTabelaTarget(null);
        }
    }

    public void adicionarAtributosTabela() {
        if (atributosSelecionadosRelatorioTabelaSource != null) {
            for (int i = 0; i < this.getAtributosSelecionadosRelatorioTabelaSource().length; i++) {
                this.getSource().remove(this.getAtributosSelecionadosRelatorioTabelaSource()[i]);
                this.getAtributosSelecionadosRelatorioTabelaSource()[i].setTargetListaRelatorio(TargetListaRelatorio.TARGET);
                this.getAtributosSelecionadosRelatorioTabelaSource()[i].setId(null);
                this.getTarget().add(this.getAtributosSelecionadosRelatorioTabelaSource()[i]);
            }
            this.setAtributosSelecionadosRelatorioTabelaSource(null);
        }
    }

    public void removerTodosAtributosTabela() {
        this.getSource().addAll(this.getTarget());
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : this.getSource()) {
            atributoRelatorioGenerico.setTargetListaRelatorio(TargetListaRelatorio.SOURCE);
        }
        this.getTarget().clear();
    }

    public void adicionarTodosAtributosTabela() {
        this.getTarget().addAll(this.getSource());
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : this.getTarget()) {
            atributoRelatorioGenerico.setTargetListaRelatorio(TargetListaRelatorio.TARGET);
        }
        this.getSource().clear();
    }

    public void alteraCampoUltimoDaLista() {
        int i = this.getTarget().size() - 1;
        for (AtributoRelatorioGenerico atributoSelecionado : this.getAtributosSelecionadosRelatorioTabelaTarget()) {
            try {
                int indiceAtual = this.getTarget().indexOf(atributoSelecionado);
                if (indiceAtual != i) {
                    this.getTarget().remove(atributoSelecionado);
                    this.getTarget().add(i, atributoSelecionado);
                    i--;
                }
            } catch (Exception e) {
            }
        }
    }

    public List<String> validarTamanhoCondicaoLabel() {
        List<String> mensagens = new ArrayList<>();
        boolean deuCerto = true;
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : this.getTarget()) {
            if (atributoRelatorioGenerico.getCondicao().length() > RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_CONDICAO) {
                deuCerto = false;
                mensagens.add("A Condição do Campo <b> " + atributoRelatorioGenerico.getLabel() + "</b> na lista de Campos <b> Selecionados</b> não pode ser maior que <b> " + RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_CONDICAO + " caracteres. </b>");
            }
            if (atributoRelatorioGenerico.getLabel().length() > RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_LABEL) {
                deuCerto = false;
                mensagens.add("O Label do Campo <b> " + atributoRelatorioGenerico.getLabel() + "</b> na lista de Campos <b>Selecionados</b> não pode ser maior que <b>" + RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_LABEL + " caracteres.</b>");
            }
        }
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : this.getSource()) {
            if (atributoRelatorioGenerico.getCondicao().length() > RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_CONDICAO) {
                deuCerto = false;
                mensagens.add("A Condição do Campo <b> " + atributoRelatorioGenerico.getLabel() + "</b> na lista de Campos <b>Disponíveis</b> não pode ser maior que <b>" + RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_CONDICAO + " caracteres.</b>");
            }
            if (atributoRelatorioGenerico.getLabel().length() > RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_LABEL) {
                deuCerto = false;
                mensagens.add("O Label do Campo <b> " + atributoRelatorioGenerico.getLabel() + "</b> na lista de Campos <b>Disponíveis</b> não pode ser maior que <b>" + RelatorioPesquisaGenerico.TAMANHO_DE_CARACTERES_LABEL + " caracteres.</b>");
            }
        }
        if (!deuCerto) {
            throw new ExcecaoNegocioGenerica(SUMMARY);
        }
        return mensagens;
    }

    public void alterarCoresParaPadrao() {
        if (!this.colorido) {
            this.corFundoTituloTabela = COR_BRANCO;
            this.corFundoZebrado1 = COR_BRANCO;
            this.corFundoZebrado2 = COR_BRANCO;
            this.corFundoAgrupador = COR_BRANCO;
            this.corFundoTotalizador = COR_BRANCO;
        } else {
            this.corFundoTituloTabela = COR_FUNDO_TITULO_TABELA;
            this.corFundoZebrado1 = COR_FUNDO_ZEBRADO_1;
            this.corFundoZebrado2 = COR_FUNDO_ZEBRADO_2;
            this.corFundoAgrupador = COR_FUNDO_TITULO_TABELA;
            this.corFundoTotalizador = COR_FUNDO_TITULO_TABELA;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return codigo + " - " + titulo + " - " + nomeRelatorio;
    }
}
