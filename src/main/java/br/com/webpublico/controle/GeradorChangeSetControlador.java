package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.entidades.Menu;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.geradores.GeraChangeSet;
import br.com.webpublico.geradores.GeraDiagramaClasses;
import br.com.webpublico.geradores.ProcuraLink;
import br.com.webpublico.negocios.GrupoRecursoFacade;
import br.com.webpublico.negocios.MenuFacade;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ManagedBean(name = "geradorChangeSetControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoChangeSet", pattern = "/changeset/novo/", viewId = "/faces/tributario/cadastromunicipal/changeset/novo.xhtml"),
})
public class GeradorChangeSetControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(GeradorChangeSetControlador.class);

    private String entidades;
    private String resultado;
    private String pacotes;
    @EJB
    private GrupoRecursoFacade grupoRecursoFacade;
    @EJB
    private MenuFacade menuFacade;

    public String getEntidades() {
        return entidades;
    }

    public void setEntidades(String entidades) {
        this.entidades = entidades;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getPacotes() {
        return pacotes;
    }

    public void setPacotes(String pacotes) {
        this.pacotes = pacotes;
    }

    public void gerar() {
        resultado = new GeraChangeSet(entidades).gerar().getResultado();
    }

    public void procurarLink() {
        resultado = new ProcuraLink(entidades).procurar().getResultadoAsString();
    }

    public void gerarLinks() {
        GetLinksPorPacotes getLinksPorPacotes = new GetLinksPorPacotes(pacotes).invoke();
        verificarSeNaoTemNoMenu(getLinksPorPacotes.getLinks());
    }

    public void gerarSelectRecursosSistema() {
        List<String> resultados = new ProcuraLink(entidades).ListaTodosReflection().getResultado();
        List<String> somenteRHs = new ArrayList<>();
        List<String> recursosASalvar = new ArrayList<>();
        for (String s : resultados) {
            if (s.contains("rh/") && !s.contains("pccr/")) {
                somenteRHs.add(s);
            }
        }
        logger.debug("Total: " + resultados.size());
        logger.debug("+++++++++++++++++++++++++++++++++++++++++++++");
        logger.debug("Total RH: " + somenteRHs.size());
//        resultado = " select * from RecursoSistema where caminho not in (";
//        for (String s : somenteRHs) {
//            System.out.println(s);
//            resultado += "'" + s + "',";
//        }
//        resultado = resultado.substring(0, resultado.length() - 1);
//        resultado += ");";
        List<RecursoSistema> recursos = recuperaRecursosDoSistema();
        List<RecursoSistema> grupoRecursos = recuperarGrupoRecursosRHDoSistema();
        logger.debug("iniciando verificação");
        for (String somenteRH : somenteRHs) {
            if (!checarSeNaoTemNoRecurso(grupoRecursos, somenteRH)) {
                String linha = somenteRH + "" + System.lineSeparator();
                if (!resultado.contains(linha)) {
                    System.out.println("um não encontrado: " + somenteRH);
                    resultado += linha;
                    recursosASalvar.add(somenteRH);
                }
            }

        }
        verificarEAssociarRecursos(recursosASalvar);
        verificarSeNaoTemNoMenu(somenteRHs);
    }

    private void verificarSeNaoTemNoMenu(List<String> links) {
        logger.debug("Links {}", links);
        Set<String> caminhosNaoRegistradosNoMenu = Sets.newHashSet();
        Set<String> caminhosListaNaoRegistradosNoMenu = Sets.newHashSet();
        List<Menu> menus = menuFacade.lista();
        for (String link : links) {
            if (naoEstaNoMenu(link, menus)) {
                if (link.contains("lista")) {
                    caminhosListaNaoRegistradosNoMenu.add(link);
                } else {
                    caminhosNaoRegistradosNoMenu.add(link);
                }
            }
        }
        logger.debug("==========Caminhos não registrados no Menu==============");
        for (String s : caminhosNaoRegistradosNoMenu) {
            logger.debug(s);
        }
        logger.debug("==========Caminhos em LISTA não registrados no Menu==============");
        for (String s : caminhosListaNaoRegistradosNoMenu) {
            logger.debug(s);
        }
    }

    private boolean naoEstaNoMenu(String link, List<Menu> menus) {
        for (Menu menu : menus) {
            if (menu.getCaminho() != null) {
                if (menu.getCaminho().contains(link)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void verificarEAssociarRecursos(List<String> recursosASalvar) {
        GrupoRecurso grupo = getGrupoRecursoRH();
        List<RecursoSistema> novosRecuros = new ArrayList<>();
        for (String recurso : recursosASalvar) {
            RecursoSistema re = inserirRecursoSistema(recurso);
            if (re != null) {
                novosRecuros.add(re);
            }
        }

        for (RecursoSistema novoRecurso : novosRecuros) {
            logger.debug("recurso novo salvo: " + novoRecurso);
            if (!grupo.getRecursos().contains(novoRecurso)) {
                grupo.getRecursos().add(novoRecurso);
            }
        }
        logger.debug("+++++++++++++++++++++ inicio novos ++++++++++++++++++++++");
        for (RecursoSistema recursoSistema : grupo.getRecursos()) {
            logger.debug(recursoSistema + "");
        }
        logger.debug("+++++++++++++++++++++ fim novos ++++++++++++++++++++++");
        grupoRecursoFacade.salvar(grupo);
        resultado += "Registros inseridos com sucesso.";
    }

    private boolean checarSeNaoTemNoRecurso(List<RecursoSistema> recursos, String somenteRH) {
        for (RecursoSistema recurso : recursos) {
            if (somenteRH.equals(recurso.getCaminho())) {
                return true;
            }
        }
        return false;
    }


    public void geraScriptAjustesLinks() {
        Map<String, List<RecursoSistema>> mapa = montaMapaDeRecursos();
        resultado = "--Duplicados\n";
        List<String> remover = Lists.newArrayList();
        for (String s : mapa.keySet()) {
            if (mapa.get(s).size() > 1) {
                remover.add(s);
                RecursoSistema rec = mapa.get(s).remove(0);
                for (RecursoSistema recs : mapa.get(s)) {
                    resultado += "update RECURSOSUSUARIO set RECURSOSISTEMA_ID = " + rec.getId() + " where RECURSOSISTEMA_ID = " + recs.getId() + ";  \n";

                    resultado += "INSERT INTO GRUPORECURSOSISTEMA " +
                        "SELECT " + rec.getId() + ", gs.GRUPORECURSO_ID " +
                        "FROM GRUPORECURSOSISTEMA gs where gs.RECURSOSISTEMA_ID = " + recs.getId() + "  " +
                        "and not exists (SELECT * " +
                        "FROM GRUPORECURSOSISTEMA gp where gp.RECURSOSISTEMA_ID = " + rec.getId() + " and gp.GRUPORECURSO_ID = gs.GRUPORECURSO_ID);\n" +
                        "delete GRUPORECURSOSISTEMA where RECURSOSISTEMA_ID =  " + rec.getId() + ";\n";

                    resultado += "INSERT INTO FASERECURSOSISTEMA " +
                        "SELECT gs.FASE_ID, " + rec.getId() + " " +
                        "FROM FASERECURSOSISTEMA gs where gs.RECURSOSISTEMA_ID = " + recs.getId() + " " +
                        "and not exists (SELECT * " +
                        "FROM FASERECURSOSISTEMA gp where gp.RECURSOSISTEMA_ID = " + rec.getId() + " and gp.FASE_ID = gs.FASE_ID);\n" +
                        "delete FASERECURSOSISTEMA where RECURSOSISTEMA_ID =  " + rec.getId() + ";\n";

                    resultado += "delete RECURSOSISTEMA where ID = " + recs.getId() + "; \n";
                }
            }
        }
        for (String s : remover) {
            mapa.remove(s);
        }

        resultado += "--Novos\n";
        for (String s : mapa.keySet()) {
            if (mapa.get(s).isEmpty()) {
                String nome = s.replaceAll("/faces/", "").replaceAll(".xhtml", "").replaceAll("/", " ");
                resultado += "insert INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO)" +
                    " VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'" + nome + "','" + s + "',0);\n";

            }
        }
    }

    private Map<String, List<RecursoSistema>> montaMapaDeRecursos() {
        List<String> resultados = new ProcuraLink(entidades).ListaTodos().getResultado();
        List<RecursoSistema> recursos = recuperaRecursosDoSistema();
        Map<String, List<RecursoSistema>> mapa = Maps.newHashMap();
        for (String s : resultados) {
            if (!mapa.containsKey(s)) {
                mapa.put(s, new ArrayList<RecursoSistema>());
            }

            for (RecursoSistema rec : recursos) {
                if (rec.comparaUri(s)) {
                    if (!mapa.get(s).contains(rec)) {
                        mapa.get(s).add(rec);
                    }
                }
            }
        }
        return mapa;
    }

    private List<RecursoSistema> recuperaRecursosDoSistema() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        SingletonRecursosSistema singleton = (SingletonRecursosSistema) ap.getBean("singletonRecursosSistema");
        singleton.getRecursos().clear();
        //System.out.println("recursos " + singleton.getRecursos().size());
        return singleton.getRecursos();
    }

    private List<RecursoSistema> recuperarGrupoRecursosRHDoSistema() {
        return grupoRecursoFacade.getRecursoSistemaFacade().buscarRecusosPorGrupoRecurso(getGrupoRecursoRH());
    }


    private RecursoSistema inserirRecursoSistema(String uri) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        RecursoSistemaService recursoSistemaService = (RecursoSistemaService) ap.getBean("recursoSistemaService");
        return recursoSistemaService.verificaAdicionaRecursoNaoCadastrado(uri);
    }


    private GrupoRecurso getGrupoRecursoRH() {
        return grupoRecursoFacade.recuperar(8757023L);
    }

    private class GetLinksPorPacotes {
        private String pacotes;
        private List<String> resultados;
        private List<String> links;

        public GetLinksPorPacotes(String pacotes) {
            this.pacotes = pacotes;
        }

        public List<String> getResultados() {
            return resultados;
        }

        public List<String> getLinks() {
            return links;
        }

        public GetLinksPorPacotes invoke() {
            resultados = new ProcuraLink(entidades).ListaTodosReflection().getResultado();
            links = new ArrayList<>();
            if (pacotes != null && !pacotes.trim().isEmpty()) {
                List<String> pacotesPorVirgula = Lists.newArrayList(this.pacotes.split(","));
                logger.debug("pacotesPorVirgula  {}", pacotesPorVirgula);
                logger.debug("resultados {}", resultados);
                for (String s : resultados) {
                    for (String pacote : pacotesPorVirgula) {
                        if (s.contains(pacote)) {
                            links.add(s);
                        }
                    }
                }
            }
            return this;
        }
    }

    public void gerarDiagramas() {
        GeraDiagramaClasses.gerarDiagramas();
    }
}
