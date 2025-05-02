/*
 * Codigo gerado automaticamente em Tue Feb 14 09:57:29 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;


import br.com.webpublico.consultaentidade.TipoURLConsultaEntidade;
import br.com.webpublico.entidades.ConcessaoFeriasLicenca;
import br.com.webpublico.enums.rh.TipoAuditoriaRH;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.jdbc.AuditoriaJDBC;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.envers.NotAudited;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@ManagedBean(name = "pesquisaAuditoriaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarAuditoria",
        pattern = "/auditoria/listar/",
        viewId = "/faces/comum/auditoria/lista.xhtml"),

    @URLMapping(id = "verAuditoriaPorEntidade",
        pattern = "/auditoria/listar-por-entidade/#{pesquisaAuditoriaControlador.id}/#{pesquisaAuditoriaControlador.className}/",
        viewId = "/faces/comum/auditoria/lista.xhtml"),

    @URLMapping(id = "verAuditoria",
        pattern = "/auditoria/ver/#{pesquisaAuditoriaControlador.rev}/#{pesquisaAuditoriaControlador.id}/#{pesquisaAuditoriaControlador.className}/",
        viewId = "/faces/comum/auditoria/visualizar.xhtml"),

    @URLMapping(id = "detalharAuditoria",
        pattern = "/auditoria/detalhar/#{pesquisaAuditoriaControlador.id}/#{pesquisaAuditoriaControlador.className}/",
        viewId = "/faces/comum/auditoria/detalhar.xhtml"),
})
public class PesquisaAuditoriaControlador {

    private static Integer TAMANHO_INPUT = 70;
    private List<DeParaAuditoria> dePara = Lists.newLinkedList();

    private Map<AuditoriaJDBC.ClasseAuditada, List<AuditoriaJDBC.ClasseAuditada>> listaAuditorias = Maps.newLinkedHashMap();
    private Map<AuditoriaJDBC.ClasseAuditada, AuditoriaJDBC.FiltroClasseAuditada> totalRegistroPorClasse = Maps.newLinkedHashMap();
    private Converter converterClasse;
    private Set<Class<?>> todasEntidades;
    private Long id, rev;
    private String className;
    private AuditoriaJDBC auditoriaJDBC;
    private AuditoriaJDBC.FiltroClasseAuditada filtroClasseAuditada;
    private TreeNode rootTree;
    private TreeNode nodeSelecionado;
    private Boolean exibirSomenteAlteracoes = Boolean.FALSE;

    @PostConstruct
    public void init() {
        Reflections reflections = new Reflections("br.com.webpublico.entidades");
        todasEntidades = reflections.getTypesAnnotatedWith(Entity.class);
        Reflections reflectionsNota = new Reflections("br.com.webpublico.nfse.domain");
        todasEntidades.addAll(reflectionsNota.getTypesAnnotatedWith(Entity.class));
        auditoriaJDBC = (AuditoriaJDBC) Util.getSpringBeanPeloNome("auditoriaJDBC");
    }

    public TreeNode getNodeSelecionado() {
        return nodeSelecionado;
    }

    public void setNodeSelecionado(TreeNode nodeSelecionado) {
        this.nodeSelecionado = nodeSelecionado;
    }

    public TreeNode getRootTree() {
        return rootTree;
    }

    public void setRootTree(TreeNode rootTree) {
        this.rootTree = rootTree;
    }

    public AuditoriaJDBC.FiltroClasseAuditada getFiltroClasseAuditada() {
        return filtroClasseAuditada;
    }

    public List<DeParaAuditoria> getDePara() {
        return dePara;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRev() {
        return rev;
    }

    public void setRev(Long rev) {
        this.rev = rev;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getExibirSomenteAlteracoes() {
        return exibirSomenteAlteracoes;
    }

    public void setExibirSomenteAlteracoes(Boolean exibirSomenteAlteracoes) {
        this.exibirSomenteAlteracoes = exibirSomenteAlteracoes;
    }

    @URLAction(mappingId = "verAuditoriaPorEntidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAuditoriaPorEntidade() {
        filtroClasseAuditada = new AuditoriaJDBC.FiltroClasseAuditada();
        filtroClasseAuditada.setClasse(getClassByName(className));
        listarAuditoria();

    }


    @URLAction(mappingId = "verAuditoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAuditoriaPorIdRevisao() {
        filtroClasseAuditada = new AuditoriaJDBC.FiltroClasseAuditada();
        filtroClasseAuditada.setClasse(getClassByName(className));
        buscarAuditoria();

    }

    @URLAction(mappingId = "detalharAuditoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void detalharAuditoria() {
        filtroClasseAuditada = new AuditoriaJDBC.FiltroClasseAuditada();
        filtroClasseAuditada.setClasse(getClassByName(className));
        LinkedList<AuditoriaJDBC.ClasseAuditada> revisoes = auditoriaJDBC.listarAuditoria(filtroClasseAuditada, id, 2, null);
        for (int i = 0; i < revisoes.size(); i++) {
            DeParaAuditoria dePara = new DeParaAuditoria();
            dePara.para = revisoes.get(i);
            dePara.de = (i + 1) < revisoes.size() ? revisoes.get(i + 1) : null;
            this.dePara.add(dePara);
        }
    }

    @URLAction(mappingId = "listarAuditoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        filtroClasseAuditada = new AuditoriaJDBC.FiltroClasseAuditada();
    }


    public List<String> buscarAtributosDaClasse(AuditoriaJDBC.ClasseAuditada classeAuditada) {
        if (classeAuditada != null) {
            ArrayList<String> atributos = Lists.newArrayList(classeAuditada.getAtributos().keySet());
            Collections.sort(atributos);
            return atributos;
        }
        return null;
    }

    public AuditoriaJDBC.FiltroClasseAuditada buscarQuantidadeRegistroDaClasse(AuditoriaJDBC.ClasseAuditada classeAuditada) {
        if (classeAuditada != null) {
            return totalRegistroPorClasse.get(classeAuditada);
        }
        return null;
    }

    public boolean isExibirAtributo(AuditoriaJDBC.ClasseAuditada de,
                                  AuditoriaJDBC.ClasseAuditada para,
                                  String atributo) {
        return !exibirSomenteAlteracoes || isValorAtributoDiferente(de, para, atributo);
    }

    public boolean isValorAtributoDiferente(AuditoriaJDBC.ClasseAuditada de,
                                            AuditoriaJDBC.ClasseAuditada para,
                                            String atributo) {
        try {
            Object valorDe = de.getAtributos().get(atributo);
            Object valorPara = para.getAtributos().get(atributo);
            return !valorDe.equals(valorPara);
        } catch (Exception e) {
            return false;
        }
    }

    public Object buscarValorDoAtributo(AuditoriaJDBC.ClasseAuditada classeAuditada, String atributo) {
        if (classeAuditada != null && !Strings.isNullOrEmpty(atributo)) {
            return classeAuditada.getAtributos().get(atributo);
        }
        return null;
    }

    public Object buscarValorDoAtributoReduzido(AuditoriaJDBC.ClasseAuditada classeAuditada, String atributo) {
        if (classeAuditada != null && !Strings.isNullOrEmpty(atributo)) {
            Object objeto = classeAuditada.getAtributos().get(atributo);
            if (objeto != null) {
                return objeto.toString().length() >= TAMANHO_INPUT ? objeto.toString().substring(0, TAMANHO_INPUT) : objeto.toString();
            }
        }
        return null;
    }

    public boolean hasValorAtributoMaiorPermitido(AuditoriaJDBC.ClasseAuditada classeAuditada, String atributo) {
        if (classeAuditada != null && !Strings.isNullOrEmpty(atributo)) {
            Object objeto = classeAuditada.getAtributos().get(atributo);
            if (objeto != null && objeto.toString().length() > TAMANHO_INPUT) {
                return true;
            }
        }
        return false;
    }

    public List<Class<?>> completarEntidades(String parte) {
        if (!Strings.isNullOrEmpty(parte)) {
            List filtrados = Lists.newLinkedList();
            for (Class<?> entidade : todasEntidades) {
                if (entidade.getSimpleName().toLowerCase().contains(parte.toLowerCase())
                    || getNomeClasse(entidade).toLowerCase().contains(parte.toLowerCase())) {
                    filtrados.add(entidade);
                }
            }
            return filtrados;
        }
        return Lists.newLinkedList(todasEntidades);
    }

    public Converter getConverterClasse() {
        if (converterClasse == null) {
            converterClasse = new Converter() {
                @Override
                public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                    Object entidade1 = getClassByName(s);
                    if (entidade1 != null) return entidade1;
                    return null;
                }

                @Override
                public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                    return o == null ? null : ((Class<?>) o).getSimpleName();
                }
            };
        }
        return converterClasse;
    }

    private Class<?> getClassByName(String s) {
        for (Class<?> entidade : todasEntidades) {
            if (entidade.getSimpleName().equalsIgnoreCase(Util.removerCaracteresEspeciais(s))) {
                return entidade;
            }
        }
        return null;
    }

    public void limpaCamposDaView() {
        FacesUtil.redirecionamentoInterno("/auditoria/listar/");
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroClasseAuditada.getNivel() == null || filtroClasseAuditada.getNivel() < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o nível de busca, ele deve ser maior ou igual a 0");
        }
        if (filtroClasseAuditada.getMaxResult() == null || filtroClasseAuditada.getMaxResult() < 1) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade máxima de registros a serem listados, ele deve ser maior que 0");
        }
        if (filtroClasseAuditada.getClasse() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe qual a classe de entidade que deseja realizar a consulta de auditoria");
        }
        if (filtroClasseAuditada.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial que deseja realizar a consulta de auditoria");
        }
        if (filtroClasseAuditada.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final que deseja realizar a consulta de auditoria");
        }

        ve.lancarException();
    }

    public void listarAuditoria() {
        try {
            validarFiltros();
            listaAuditorias = Maps.newLinkedHashMap();
            totalRegistroPorClasse = Maps.newHashMap();
            filtroClasseAuditada.setTotalRegistros(auditoriaJDBC.contarAuditoria(filtroClasseAuditada.getClasse(), id, null));

            LinkedList<AuditoriaJDBC.ClasseAuditada> auditorias = auditoriaJDBC.listarAuditoria(filtroClasseAuditada, id, filtroClasseAuditada.getNivel(), null);
            montarTabelaPorClasse(auditorias, filtroClasseAuditada);

            rootTree = new DefaultTreeNode("", null);
            rootTree.setExpanded(true);

            DefaultTreeNode treeFilho = new DefaultTreeNode(new ArvoreDependencias(filtroClasseAuditada.getClasse(), Lists.<Long>newArrayList(id)), rootTree);
            treeFilho.setExpanded(true);


            List<ArvoreDependencias> filhos = getFilhosDaClasse(filtroClasseAuditada.getClasse(), Lists.newArrayList(id));
            montarArvoreDependenciasFilhos(filhos, filtroClasseAuditada.getNivel(), treeFilho);


        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorGenerico(e);
        }
    }


    public void listarAuditoriaDoFilho() {
        ArvoreDependencias dependencia = (ArvoreDependencias) nodeSelecionado.getData();
        AuditoriaJDBC.FiltroClasseAuditada filtroFilho = AuditoriaJDBC.FiltroClasseAuditada.copy(filtroClasseAuditada, dependencia.getClasse());
        listaAuditorias = Maps.newLinkedHashMap();
        totalRegistroPorClasse = Maps.newHashMap();
        Integer totalRegistros = 0;
        for (Long id : dependencia.getIds()) {
            totalRegistros += auditoriaJDBC.contarAuditoria(dependencia.getClasse(), id, null);
            LinkedList<AuditoriaJDBC.ClasseAuditada> auditorias = auditoriaJDBC.listarAuditoria(filtroFilho, id, filtroFilho.getNivel(), null);
            montarTabelaPorClasse(auditorias, filtroFilho);

        }
        filtroClasseAuditada.setTotalRegistros(totalRegistros);

    }

    private void montarArvoreDependenciasFilhos(List<ArvoreDependencias> filhos, final int nivel, TreeNode treePai) {
        int meuNivel = new Integer(nivel) - 1;
        for (ArvoreDependencias filho : filhos) {
            DefaultTreeNode treeFilho = new DefaultTreeNode(filho, treePai);
            treeFilho.setExpanded(true);
            if (nivel > 0) {
                List<ArvoreDependencias> filhosDoFilho = getFilhosDaClasse(filho.getClasse(), filho.getIds());
                montarArvoreDependenciasFilhos(filhosDoFilho, meuNivel, treeFilho);
            }
        }
    }


    private List<ArvoreDependencias> getFilhosDaClasse(Class classe, List<Long> ids) {
        Map<Class, List<Long>> filhosNaoOrdenados = Maps.newHashMap();
        for (Field field : Persistencia.getAtributos(classe)) {
            if (field.isAnnotationPresent(OneToMany.class) && !Strings.isNullOrEmpty(field.getAnnotation(OneToMany.class).mappedBy()) && !field.isAnnotationPresent(NotAudited.class)) {
                String mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
                Class genericTypeFromCollection = auditoriaJDBC.getGenericTypeFromCollection(field, classe);
                List<Long> idsFilhos = Lists.newArrayList();

                for (List<Long> lista : Lists.partition(ids, 1000)) {
                    String idsString = "(";
                    for (Long id : lista) {
                        if (lista.indexOf(id) == 0) {
                            idsString += id;
                        } else {
                            idsString += "," + id;
                        }
                    }
                    idsString += ")";
                    idsFilhos.addAll(auditoriaJDBC.buscarIdsDosFilhos(genericTypeFromCollection, mappedBy + "_id in " + idsString));
                }
                if (!idsFilhos.isEmpty()) {
                    filhosNaoOrdenados.put(genericTypeFromCollection, idsFilhos);
                }
            }
        }

        return ordenarFilhosEGrupoDeFilhosPorDataEId(filhosNaoOrdenados);
    }

    private List<ArvoreDependencias> ordenarFilhosEGrupoDeFilhosPorDataEId(Map<Class, List<Long>> filhosNaoOrdenados){
        List<ArvoreDependencias> filhosOrdenados = Lists.newArrayList();

        for (Map.Entry<Class, List<Long>> entry : filhosNaoOrdenados.entrySet()) {
            Class tipoFilho = entry.getKey();
            List<Long> idsFilhos = entry.getValue();

            Map<Long, Date> datasPorId = auditoriaJDBC.buscarDatasMaisRecentesDosFilhos(tipoFilho, idsFilhos);

            idsFilhos.sort((idAtual, idProximo) -> {
                Date dataAtual = datasPorId.get(idAtual);
                Date dataProxima = datasPorId.get(idProximo);

                if (dataAtual == null && dataProxima == null) return idProximo.compareTo(idAtual);
                if (dataAtual == null) return 1;
                if (dataProxima == null) return -1;

                int comparacaoPorData = dataProxima.compareTo(dataAtual);
                if (comparacaoPorData != 0) return comparacaoPorData;

                return idProximo.compareTo(idAtual);
            });

            filhosOrdenados.add(new ArvoreDependencias(tipoFilho, idsFilhos));
        }

        filhosOrdenados.sort((grupoAtual, grupoProximo) -> {
            Date dataMaisRecenteAtual = auditoriaJDBC.buscarDataMaisRecente(grupoAtual.getClasse(),grupoAtual.getIds().get(0));
            Date dataMaisRecenteProxima = auditoriaJDBC.buscarDataMaisRecente(grupoProximo.getClasse(),grupoProximo.getIds().get(0));

            if (dataMaisRecenteAtual == null && dataMaisRecenteProxima == null) {
                return Long.compare(grupoProximo.getIds().get(0), grupoAtual.getIds().get(0));
            }
            if (dataMaisRecenteAtual == null) return 1;
            if (dataMaisRecenteProxima == null) return -1;

            int comparacaoPorData = dataMaisRecenteProxima.compareTo(dataMaisRecenteAtual);
            if (comparacaoPorData != 0) return comparacaoPorData;

            return Long.compare(grupoProximo.getIds().get(0), grupoAtual.getIds().get(0));
        });
        return filhosOrdenados;
    }

    private void montarTabelaPorClasse(List<AuditoriaJDBC.ClasseAuditada> auditorias, AuditoriaJDBC.FiltroClasseAuditada filtro) {
        for (AuditoriaJDBC.ClasseAuditada classe : auditorias) {
            if (!listaAuditorias.containsKey(classe)) {
                listaAuditorias.put(classe, Lists.<AuditoriaJDBC.ClasseAuditada>newArrayList());
            }
            if (!totalRegistroPorClasse.containsKey(classe)) {
                totalRegistroPorClasse.put(classe, filtro);
            }
            listaAuditorias.get(classe).add(classe);
        }
    }

    public List<AuditoriaJDBC.ClasseAuditada> getListaClassesPesquisa() {
        return Lists.newArrayList(listaAuditorias.keySet());
    }

    public List<AuditoriaJDBC.ClasseAuditada> listarAuditoriasDaClasse(AuditoriaJDBC.ClasseAuditada classe) {
        return listaAuditorias.get(classe);
    }

    public void buscarAuditoria() {
        dePara = Lists.newLinkedList();
        List<AuditoriaJDBC.ClasseAuditada> revisoesAtuais = auditoriaJDBC.carregarAuditoria(filtroClasseAuditada.getClasse(), id, rev, 2);
        List<AuditoriaJDBC.ClasseAuditada> revisoesAnteriores = Lists.newArrayList();

        Long revisaoAnterior = auditoriaJDBC.buscarPrimeiraRevisaoAnterior(filtroClasseAuditada.getClasse(), id, rev);
        if (revisaoAnterior != null) {
            revisoesAnteriores = auditoriaJDBC.carregarAuditoria(filtroClasseAuditada.getClasse(), id, revisaoAnterior, 2);
        }

        for (AuditoriaJDBC.ClasseAuditada anterior : revisoesAnteriores) {
            DeParaAuditoria dePara = new DeParaAuditoria();
            dePara.de = anterior;
            this.dePara.add(dePara);
        }
        for (AuditoriaJDBC.ClasseAuditada atual : revisoesAtuais) {
            boolean achou = false;
            for (DeParaAuditoria anterior : this.dePara) {
                if (anterior.de != null && atual.getAtributos() != null && atual.getAtributos().get("Id") != null
                    && atual.getAtributos().get("Id").equals(anterior.de.getAtributos().get("Id"))) {
                    anterior.para = atual;
                    achou = true;
                }
            }
            if (!achou) {
                DeParaAuditoria dePara = new DeParaAuditoria();
                dePara.para = atual;
                this.dePara.add(dePara);
            }
        }
    }

    public String getNomeClasse(Class classe) {
        if (ConcessaoFeriasLicenca.class.equals(classe) && TipoAuditoriaRH.CONCESSAO_LICENCA.equals(Web.pegaDaSessao("CONCESSAO"))) {
            Web.poeNaSessao("CONCESSAO", TipoAuditoriaRH.CONCESSAO_LICENCA);
            return TipoAuditoriaRH.CONCESSAO_LICENCA.getDescricao();
        }
        return auditoriaJDBC.getNomeEntidade(classe);
    }


    public void verRevisao(AuditoriaJDBC.ClasseAuditada classeAuditada) {
        Object id = classeAuditada.getAtributos().get("Id");
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", PrettyContext.getCurrentInstance().getRequestURL().toString());
        FacesUtil.redirecionamentoInterno("/auditoria/ver/" + classeAuditada.getAtributos().get("Revisão") + "/" + id + "/" + classeAuditada.getClasse().getSimpleName() + "/");
    }

    public static class ArvoreDependencias {
        private Class classe;
        private List<Long> ids;

        public ArvoreDependencias(Class classe, List<Long> ids) {
            this.classe = classe;
            this.ids = ids;
        }

        public Class getClasse() {
            return classe;
        }

        public List<Long> getIds() {
            return ids;
        }

        @Override
        public String toString() {
            return AuditoriaJDBC.getNomeEntidade(classe) + "  (" + ids.size() + ")";
        }
    }

    public static class DeParaAuditoria {
        private AuditoriaJDBC.ClasseAuditada de;
        private AuditoriaJDBC.ClasseAuditada para;

        public AuditoriaJDBC.ClasseAuditada getDe() {
            return de;
        }

        public AuditoriaJDBC.ClasseAuditada getPara() {
            return para;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeParaAuditoria that = (DeParaAuditoria) o;

            if (de != null ? !de.equals(that.de) : that.de != null) return false;
            return para != null ? para.equals(that.para) : that.para == null;
        }

        @Override
        public int hashCode() {
            int result = de != null ? de.hashCode() : 0;
            result = 31 * result + (para != null ? para.hashCode() : 0);
            return result;
        }

        public void copiar() {
            this.de = new AuditoriaJDBC.ClasseAuditada(this.para.getClasse(), this.para.getNome(), new LinkedHashMap<String, Object>());
            for (String s : para.getAtributos().keySet()) {
                de.getAtributos().put(s, " - ");
            }
        }
    }

    public void voltar() throws IOException {
        String url = (String) Web.getSessionMap().get("pagina-anterior-auditoria");
        if (url == null) {
            url = (String) Web.getSessionMap().get(TipoURLConsultaEntidade.URL_RETORNO.getKey());
        }
        if (url != null) {
            FacesUtil.redirecionamentoInterno(url);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
        }
    }

    public void voltarParaListar() throws IOException {
        String url = (String) Web.getSessionMap().get("pagina-anterior-auditoria-listar");
        if (url != null) {
            FacesUtil.redirecionamentoInterno(url);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
        }
    }

    public void atribuirValoresUrl(String nomeClasse, Long id, Long rev) {
        this.className = nomeClasse;
        this.id = id;
        this.rev = rev;
        if (this.className != null && this.id != null && this.rev != null) {
            verAuditoriaPorIdRevisao();
        }
    }
}
