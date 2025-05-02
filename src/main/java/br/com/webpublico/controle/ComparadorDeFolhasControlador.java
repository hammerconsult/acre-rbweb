package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItensResultado;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.entidadesauxiliares.ObjetoResultado;
import br.com.webpublico.entidadesauxiliares.rh.ItemDirfCorrecao;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.*;
import br.com.webpublico.singletons.SingletonFolhaDePagamento;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "comparador-webpublico-turmalina", pattern = "/comparador/webpublico-turmalina/", viewId = "/faces/rh/estatisticas/comparador/comparadordefolhas.xhtml"),
    @URLMapping(id = "incosistencias-cadastrais", pattern = "/comparador/inconsistencia/", viewId = "/faces/rh/estatisticas/comparador/inconsistencias.xhtml"),
    @URLMapping(id = "dirf-inconsistencias", pattern = "/dirf/inconsistencia/", viewId = "/faces/rh/estatisticas/comparador/dirfcorrecaoaposentados.xhtml"),
    @URLMapping(id = "pagina-administrativa-rh", pattern = "/rh/administracao/", viewId = "/faces/rh/estatisticas/admin/administracaorh.xhtml"),
    @URLMapping(id = "servidor-avancado", pattern = "/comparador/avancado/", viewId = "/faces/rh/estatisticas/informacoes/comparacao.xhtml")

})
public class ComparadorDeFolhasControlador extends SuperControladorCRUD implements Serializable {
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;
    private ObjetoResultado objetoResultado;
    private ObjetoPesquisa objetoPesquisa;
    private List<ObjetoResultado> objetoResultados;
    private Map<RecursoFP, List<RecursoDoVinculoFP>> recursos;
    private Map<VinculoFP, List<RecursoDoVinculoFP>> vinculos;
    private List<ItensResultado> itensResultados;
    private ConverterAutoComplete converterEventoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ComparadorWeb comparadorWeb;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SingletonFolhaDePagamento singletonFolhaDePagamento;
    @EJB
    private SistemaFacade sistemaFacade;


    private List<ComparadorFolha> comparadorFolhas = new LinkedList<>();
    private List<ItemDirfCorrecao> itensDirf;
    private ItemDirfCorrecao[] itensDirfExclusao;
    private boolean mostrarTodosItens;

    @URLAction(mappingId = "comparador-webpublico-turmalina", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        objetoPesquisa = new ObjetoPesquisa();
        objetoResultados = new LinkedList<>();
        itensResultados = new LinkedList<>();
        comparadorWeb = new ComparadorWeb();
        recursos = Maps.newHashMap();
        vinculos = Maps.newHashMap();
    }

    public void bloquearFolha() {
        singletonFolhaDePagamento.setCalculandoFolha(true);
        singletonFolhaDePagamento.setDataInicioCalculo(sistemaFacade.getDataOperacao());
        singletonFolhaDePagamento.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    public void liberarFolha() {
        singletonFolhaDePagamento.setCalculandoFolha(false);
        singletonFolhaDePagamento.setDataInicioCalculo(null);
        singletonFolhaDePagamento.setUsuarioSistema(null);
    }

    public String getStatusAtualFolha() {
        return (singletonFolhaDePagamento.isCalculandoFolha() ? "<b>CALCULANDO FOLHA DESDE</b> " + singletonFolhaDePagamento.getDataInicioCalculo() + " USER:  " + singletonFolhaDePagamento.getUsuarioSistema() : "NENHUM CALCULO EM EXECUÇAO ");
    }


    @URLAction(mappingId = "incosistencias-cadastrais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaChecagem() {
        novo();
    }

    @URLAction(mappingId = "dirf-inconsistencias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaCorrecao() {
        novo();
        itensDirf = Lists.newArrayList();
        itensDirfExclusao = null;
    }


    @Override
    public AbstractFacade getFacede() {
        return comparadorDeFollhasFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "servidor-avancado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoComparadorAvancado() {
        comparadorFolhas = new LinkedList<>();
        comparadorFolhas = comparadorDeFollhasFacade.listaComparadores();
    }

    public void buscarInconsistenciaDirf() {
        itensDirf = Lists.newArrayList();
        List<ItemDirfCorrecao> itemDirfCorrecaos = comparadorDeFollhasFacade.buscarContratosAposentadosNoRbprev();
        if (mostrarTodosItens) {
            itensDirf = itemDirfCorrecaos;
        } else {
            for (ItemDirfCorrecao itemDirfCorrecao : itemDirfCorrecaos) {
                if (!itemDirfCorrecao.isNaoDeveExcluirLotacao() || !itemDirfCorrecao.isNaoDeveExcluirRecurso()) {
                    itensDirf.add(itemDirfCorrecao);
                }
            }

        }
    }

    public void excluirLotacoesErradas() {
        if (itensDirfExclusao != null) {
            comparadorDeFollhasFacade.excluirLotacoesErradas(Arrays.asList(itensDirfExclusao));
            for (ItemDirfCorrecao item : itensDirfExclusao) {
                if (itensDirf.contains(item)) {
                    itensDirf.remove(item);
                }
            }
            itensDirfExclusao = null;
            FacesUtil.addOperacaoRealizada("Itens Excluídos com Sucesso.");
        }
    }


    public void checarInconsistencia() {
        buscarRecursosFP();
        buscarServidoresSemRecursoFpVigente();
    }

    public void buscarRecursosFP() {
        recursos = Maps.newHashMap();
        DateTime referencia = getReferencia();
        List<RecursoFP> recursosVinculo = recursoFPFacade.buscarRecursosPorMesAndAnoFolha(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno(), false);

        for (RecursoFP recursoFP : recursosVinculo) {

            if (!grupoRecursoFPFacade.existeAgrupamentoParaRecursoFP(recursoFP, Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno())) {

                List<RecursoDoVinculoFP> recurosDoVinculoFPs = recursoDoVinculoFPFacade.buscarRecursosDoVinculoPorRecurso(recursoFP, referencia.toDate());
                if (!recurosDoVinculoFPs.isEmpty()) {
                    logger.debug("Recuso sem Grupo {}", recursoFP);
                    recursos.put(recursoFP, recurosDoVinculoFPs);
                }
            }
        }
        ordenar(recursos);
    }

    private DateTime getReferencia() {
        DateTime referencia = DataUtil.criarDataComMesEAno(objetoPesquisa.getMes(), objetoPesquisa.getAno());
        referencia = referencia.withDayOfMonth(referencia.dayOfMonth().getMaximumValue());
        return referencia;
    }

    private void buscarServidoresSemRecursoFpVigente() {
        //buscar todos os servidores da folha de pagamento.
        DateTime referencia = getReferencia();
        logger.debug("buscando servidores...");
        List<VinculoFP> vinculos = vinculoFPFacade.buscarVinculoPorMesAndAnoCalculados(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno());
        for (VinculoFP vinculo : vinculos) {
            if (recursoDoVinculoFPFacade.recuperarRecursosDoVinculoByVinculo(vinculo, referencia.toDate()).isEmpty()) {
                RecursoDoVinculoFP recursoDoVinculoFP = recursoDoVinculoFPFacade.recuperarRecursosDoVinculoByVinculoUltimoVigente(vinculo);
                this.vinculos.put(vinculo, Lists.newArrayList(recursoDoVinculoFP));
            }
        }
    }


    private void ordenar(Map<RecursoFP, List<RecursoDoVinculoFP>> recursos) {
        Map<RecursoFP, List<RecursoDoVinculoFP>> outro = new TreeMap<RecursoFP, List<RecursoDoVinculoFP>>(
            new Comparator<RecursoFP>() {
                @Override
                public int compare(RecursoFP o1, RecursoFP o2) {
                    Date o1Fim = o1.getFinalVigencia() != null ? o1.getFinalVigencia() : new Date();
                    Date o2Fim = o2.getFinalVigencia() != null ? o2.getFinalVigencia() : new Date();
                    Integer comparacaoData = o2Fim.compareTo(o1Fim);
                    if (comparacaoData == 0) {
                        return o1.getCodigo().compareTo(o2.getCodigo());
                    }
                    return o2Fim.compareTo(o1Fim);
                }
            }
        );
        outro.putAll(recursos);
        this.recursos = outro;
    }


    public void iniciarComparacao() {
        if (validaCampo()) {
            if (objetoPesquisa.isConsignado()) {
                if (objetoPesquisa.getEventosFPList() == null) {
                    objetoPesquisa.setEventosFPList(new ArrayList<EventoFP>());
                }
                objetoPesquisa.getEventosFPList().addAll(eventoFPFacade.listarEventosConsignados());
            }
            logger.debug("eventos: " + objetoPesquisa.getEventosFPList());
            List<VinculoFP> rejeitados = montaListaRejeitados();
            objetoResultados = comparadorDeFollhasFacade.iniciarComparacao(objetoPesquisa, rejeitados);
            if (objetoResultados.size() > 1) {
                ComparadorFolha c = new ComparadorFolha();
                c.setDataRegistro(new Date());
                c.setAno(objetoPesquisa.getAno());
                c.setMes(Mes.getMesToInt(objetoPesquisa.getMes()));
                for (ObjetoResultado resultado : objetoResultados) {
                    ItemComparadorFolha item = new ItemComparadorFolha();
                    if (resultado.getHierarquiaOrganizacional() != null) {
                        item.setCodigoHierarquia(resultado.getHierarquiaOrganizacional().getCodigoNo());
                        item.setUnidade(resultado.getHierarquiaOrganizacional().getSubordinada().getDescricao());
                    }
                    item.setVinculoFP(resultado.getVinculoFP());
                    item.setComparadorFolha(c);
                    for (ItensResultado itensResultado : resultado.getItensResultados()) {
                        DetalheComparador detalhe = new DetalheComparador();
                        detalhe.setItemComparadorFolha(item);
                        detalhe.setEventoFP(itensResultado.getEvento());
                        detalhe.setValorWeb(itensResultado.getValorweb());
                        detalhe.setValorTurmalina(itensResultado.getValorTurma());
                        item.getDetalheComparador().add(detalhe);
                    }
                    c.getItemComparadorFolhas().add(item);
                }
                comparadorDeFollhasFacade.salvarCamparadorFolha(c);
            }
        }
    }

    private List<VinculoFP> montaListaRejeitados() {
        List<VinculoFP> vinculos = new ArrayList<>();
        if (comparadorWeb != null && comparadorWeb.getRejeitados() != null) {
            for (ItemComparadorWeb itemComparadorWeb : comparadorWeb.getRejeitados()) {
                vinculos.add(itemComparadorWeb.getVinculoFP());
            }
        }
        return vinculos;
    }

    public void recuperarComparador() {
        if (validaObjetoPesquisa())
            comparadorWeb = comparadorDeFollhasFacade.
                recuperarCamparadorWeb(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno());
        System.out.println("Comparador: " + comparadorWeb);
    }

    private boolean validaObjetoPesquisa() {
        if (objetoPesquisa == null) return false;
        if (objetoPesquisa.getAno() == null) return false;
        if (objetoPesquisa.getMes() == null) return false;
        return true;
    }

    public void addItemComparadorWeb(ObjetoResultado resultado) {
        if (resultado != null) {
            try {
                ItemComparadorWeb item = new ItemComparadorWeb();
                item.setComparadorWeb(comparadorWeb);
                item.setVinculoFP(resultado.getVinculoFP());
                item.setCritica(resultado.getItensResultados() + "");
                item.setComentario(resultado.getComentario());
                item.setMatricula(resultado.getVinculoFP().getMatriculaFP().getMatricula());
                item.setContrato(resultado.getVinculoFP().getNumero());
                comparadorWeb.getRejeitados().add(item);
                comparadorDeFollhasFacade.salvarCamparadorWeb(comparadorWeb);
                objetoResultados.remove(resultado);
                FacesUtil.addInfo("", "Matricula rejeitada adicionada com sucesso!");
            } catch (Exception e) {
                FacesUtil.addError("", "Problema ao inserir nos rejeitados!" + e);
            }
        }
    }

    public void removerRejeitado(ItemComparadorWeb item) {
        if (item != null) {
            try {
                comparadorWeb.getRejeitados().remove(item);
                comparadorDeFollhasFacade.salvarCamparadorWeb(comparadorWeb);
                FacesUtil.addInfo("", "Registro removido");
            } catch (Exception e) {
                FacesUtil.addError("", "Problema ao remover dos rejeitados!" + e);
                logger.error(e.getMessage());
            }
        }
    }

    private boolean validaCampo() {
        if (objetoPesquisa.getMes() == null) {
            FacesUtil.addWarn("Campo Mês obrigatório preenchimento", "");
            return false;
        }
        if (objetoPesquisa.getAno() == null) {
            FacesUtil.addWarn("Campo Ano obrigatório preenchimento", "");
            return false;
        }
        if (objetoPesquisa.getTipoFolhaDePagamentoWeb() == null) {
            FacesUtil.addWarn("Campo Tipo de Folha obrigatório preenchimento", "");
            return false;
        }
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public List<EventoFP> eventoFPs(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public boolean isMostrarTodosItens() {
        return mostrarTodosItens;
    }

    public void setMostrarTodosItens(boolean mostrarTodosItens) {
        this.mostrarTodosItens = mostrarTodosItens;
    }

    public ComparadorWeb getComparadorWeb() {
        return comparadorWeb;
    }

    public void setComparadorWeb(ComparadorWeb comparadorWeb) {
        this.comparadorWeb = comparadorWeb;
    }

    public ObjetoResultado getObjetoResultado() {
        return objetoResultado;
    }

    public void setObjetoResultado(ObjetoResultado objetoResultado) {
        this.objetoResultado = objetoResultado;
    }

    public ObjetoPesquisa getObjetoPesquisa() {
        return objetoPesquisa;
    }

    public void setObjetoPesquisa(ObjetoPesquisa objetoPesquisa) {
        this.objetoPesquisa = objetoPesquisa;
    }

    public List<ObjetoResultado> getObjetoResultados() {
        return objetoResultados;
    }

    public void setObjetoResultados(List<ObjetoResultado> objetoResultados) {
        this.objetoResultados = objetoResultados;
    }

    public List<ComparadorFolha> getComparadorFolhas() {
        return comparadorFolhas;
    }

    public void setComparadorFolhas(List<ComparadorFolha> comparadorFolhas) {
        this.comparadorFolhas = comparadorFolhas;
    }

    public List<ItemDirfCorrecao> getItensDirf() {
        return itensDirf;
    }

    public void setItensDirf(List<ItemDirfCorrecao> itensDirf) {
        this.itensDirf = itensDirf;
    }

    public ItemDirfCorrecao[] getItensDirfExclusao() {
        return itensDirfExclusao;
    }

    public void setItensDirfExclusao(ItemDirfCorrecao[] itensDirfExclusao) {
        this.itensDirfExclusao = itensDirfExclusao;
    }

    public Map<RecursoFP, List<RecursoDoVinculoFP>> getRecursos() {
        return recursos;
    }

    public void setRecursos(Map<RecursoFP, List<RecursoDoVinculoFP>> recursos) {
        this.recursos = recursos;
    }

    public List<RecursoFP> getRecursosList() {
        return Lists.newArrayList(recursos.keySet());
    }

    public List<VinculoFP> getVinculosKeySet() {
        return Lists.newArrayList(vinculos.keySet());
    }

    public Map<VinculoFP, List<RecursoDoVinculoFP>> getVinculos() {
        return vinculos;
    }

    public void setVinculos(Map<VinculoFP, List<RecursoDoVinculoFP>> vinculos) {
        this.vinculos = vinculos;
    }

    public List<ItensResultado> getItensResultados() {
        return itensResultados;
    }

    public void setItensResultados(List<ItensResultado> itensResultados) {
        this.itensResultados = itensResultados;
    }

    public void gerarComparadorPDF() {
        Util.geraPDF("Comparador_Folha_da_Prefeitura_Municipal_de_Rio_Branco", gerarConteudoPDF(), FacesContext.getCurrentInstance());
    }

    public String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/Brasao_de_Rio_Branco.gif";
        return imagem;
    }


    public String gerarConteudoPDF() {
        if (objetoResultados != null && !objetoResultados.isEmpty()) {
            String content = "";
            content += "<?xml version='1.0' encoding='iso-8859-1'?>\n";
            content += "<!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";
            content += "<html>";
            content += " <style type=\"text/css\">@page{size: A4 landscape;}</style>";
            content += "<style type=\"text/css\">";
            content += "    table, th, td {";
//            content += "        border: 1px solid black;";
            content += "        border-collapse: collapse;";
            content += "    }body{font-family: Arial, \"Helvetica Neue\", Helvetica, sans-serif;}";
            content += "</style>";
            content += "<div style='border: 1px solid black;text-align: left'>";
            content += " <table style=\"border: none!important;\">";
            content += " <tr><td style=\"border: none!important; text-align: center!important;\"><img src=\"" + getCaminhoBrasao() + "\" alt=\"Smiley face\" height=\"90\" width=\"73\" /></td>";
            content += " <td style=\"border: none!important;\">";
            content += " <td style=\"border: none!important;\"><b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>";
            content += " SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO<br/>";
            content += " DEPARTAMENTO DE RECURSOS HUMANOS</b></td>";
            content += " </tr> ";
            content += "</table>";
            content += "</div>";
            content += "<div style='border: 1px solid black; text-align: center'>";
            content += "<b>Planilha Comparador de Folha</b>";
            content += "</div>";
            content += "<table border= 1px>";
            content += "<tr>";
            content += "<th width=250 align= left><b>Órgão</b></th>";
            content += "<th width=250 align= middle><b>Servidor</b></th>";
            content += "<th width=210 align= center><b>Evento / ValorWeb / ValorTurma</b></th>";
            for (ObjetoResultado obj : objetoResultados) {
                content += "</tr>";
                content += "<tr>";
                content += "<td width=250 align= left><h style=\"font-family: Arial\">" + obj.getHierarquiaOrganizacional().getCodigo() + "<br/>" + obj.getHierarquiaOrganizacional().getSubordinada() + "</h></td>";
                content += "<td width=250 align= middle><h style=\"font-family: Arial\">" + obj.getVinculoFP() + "</h></td>";
                content += "<td>";
                content += "<table border= 1px>";
                for (ItensResultado item : obj.getItensResultados()) {
                    content += "<tr>";
                    content += "<td width=210 align= left><h style=\"font-family: Arial\">" + item.getEvento().getCodigo() + " - " + item.getEvento().getDescricao() + "</h></td>";
                    content += "<td width=100 align= right><h style=\"font-family: Arial\">" + item.getValorweb() + "</h></td>";
                    content += "<td width=100 align= right><h style=\"font-family: Arial\">" + item.getValorTurma() + "</h></td>";
                    content += "</tr>";
                }
                content += "</table>";
                content += "</td>";
            }
            content += "</tr>";
            content += "</table>";
            content += "</tr>";
            content += "</html>";
            content += "";
            return content;
        } else {
            return "Nenhum arquivo encontrado.";
        }
    }

    public void definirRecursoFPEmFichaFinanceira() {
        comparadorDeFollhasFacade.definirRecursoFPEmFichaFinanceira();
    }

    public void definirLotacaoEmFichaFinanceira() {
        comparadorDeFollhasFacade.definirLotacaoEmFichaFinanceira();
    }
}
