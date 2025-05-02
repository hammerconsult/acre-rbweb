package br.com.webpublico.controle;

import br.com.webpublico.entidades.ComparadorFolha;
import br.com.webpublico.entidades.ComparadorWeb;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemComparadorWeb;
import br.com.webpublico.entidadesauxiliares.ItensResultado;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.entidadesauxiliares.ObjetoResultado;
import br.com.webpublico.entidadesauxiliares.rh.ResultadoLancamento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ComparadorDeFollhasFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "comparador-lancamento-webpublico-turmalina", pattern = "/comparador/lancamentofp/", viewId = "/faces/rh/estatisticas/comparador/comparadorlancamento.xhtml")

})
public class ComparadorDeLancamentoFPControlador extends SuperControladorCRUD implements Serializable {
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;
    private ObjetoResultado objetoResultado;
    private ObjetoPesquisa objetoPesquisa;
    private List<ResultadoLancamento> objetoResultados;
    private List<ItensResultado> itensResultados;
    private ConverterAutoComplete converterEventoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ComparadorWeb comparadorWeb;


    private List<ComparadorFolha> comparadorFolhas = new LinkedList<>();

    @URLAction(mappingId = "comparador-lancamento-webpublico-turmalina", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {

        objetoPesquisa = new ObjetoPesquisa();
        objetoResultados = new LinkedList<>();
        itensResultados = new LinkedList<>();
        comparadorWeb = new ComparadorWeb();

    }


    @Override
    public AbstractFacade getFacede() {
        return comparadorDeFollhasFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void iniciarComparacao() {
        if (validarCampos()) {
            logger.debug("Iniciando busca..");
            objetoResultados = comparadorDeFollhasFacade.iniciarComparacaoLancamento(objetoPesquisa);
            /*if (objetoResultados.size() > 1) {
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
            }*/
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
                logger.debug(e.getMessage());
            }
        }
    }

    private boolean validarCampos() {
        if (objetoPesquisa.getMes() == null) {
            FacesUtil.addWarn("Campo Mês obrigatório preenchimento", "");
            return false;
        }
        if (objetoPesquisa.getAno() == null) {
            FacesUtil.addWarn("Campo Ano obrigatório preenchimento", "");
            return false;
        }
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public List<EventoFP> eventoFPs(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
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

    public List<ResultadoLancamento> getObjetoResultados() {
        return objetoResultados;
    }

    public void setObjetoResultados(List<ResultadoLancamento> objetoResultados) {
        this.objetoResultados = objetoResultados;
    }

    public List<ComparadorFolha> getComparadorFolhas() {
        return comparadorFolhas;
    }

    public void setComparadorFolhas(List<ComparadorFolha> comparadorFolhas) {
        this.comparadorFolhas = comparadorFolhas;
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
          /*  for (ResultadoLancamento obj : objetoResultados) {
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
            }*/
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
}
