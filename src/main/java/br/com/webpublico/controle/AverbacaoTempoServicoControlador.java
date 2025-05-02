/*
 * Codigo gerado automaticamente em Wed May 09 09:45:26 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.AverbacaoTempoServico;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.TipoDocumento;
import br.com.webpublico.enums.OrgaoEmpresa;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.rh.estudoatuarial.TipoRegimePrevidenciarioEstudoAtuarial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "averbacaoTempoServicoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAverbacaoTempoServico", pattern = "/averbacao-tempo-contribuicao/novo/", viewId = "/faces/rh/administracaodepagamento/averbacaotemposervico/edita.xhtml"),
    @URLMapping(id = "editarAverbacaoTempoServico", pattern = "/averbacao-tempo-contribuicao/editar/#{averbacaoTempoServicoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/averbacaotemposervico/edita.xhtml"),
    @URLMapping(id = "listarAverbacaoTempoServico", pattern = "/averbacao-tempo-contribuicao/listar/", viewId = "/faces/rh/administracaodepagamento/averbacaotemposervico/lista.xhtml"),
    @URLMapping(id = "verAverbacaoTempoServico", pattern = "/averbacao-tempo-contribuicao/ver/#{averbacaoTempoServicoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/averbacaotemposervico/visualizar.xhtml")
})
public class AverbacaoTempoServicoControlador extends PrettyControlador<AverbacaoTempoServico> implements Serializable, CRUD {

    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private TipoDocumentoFacade tipoDocumentoFacade;
    private ConverterGenerico converterTipoDocumento;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private ConverterAutoComplete converterAtoLegal;
    private String tempo;
    private List lista;
    private int maximoRegistrosTabela;
    private String filtro = "";

    public AverbacaoTempoServicoControlador() {
        super(AverbacaoTempoServico.class);
    }

    public AverbacaoTempoServicoFacade getFacade() {
        return averbacaoTempoServicoFacade;
    }

    @URLAction(mappingId = "novoAverbacaoTempoServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        tempo = "";
        super.novo();
        AverbacaoTempoServico averbacao = (AverbacaoTempoServico) selecionado;
        averbacao.setDataCadastro(new Date());
        averbacao.setCalcularAutomatico(Boolean.TRUE);
    }

    @URLAction(mappingId = "verAverbacaoTempoServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado = averbacaoTempoServicoFacade.recuperar(selecionado.getId());
    }

    @URLAction(mappingId = "editarAverbacaoTempoServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado = averbacaoTempoServicoFacade.recuperar(selecionado.getId());
        fixaDataInicial();
    }

    @Override
    public AbstractFacade getFacede() {
        return averbacaoTempoServicoFacade;
    }

    public List<ContratoFP> completaContratoFP(String s) {
        return contratoFPFacade.buscaContratosMatriculaFPTipoPrevidencia(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public void defineTempo() {
        tempo = "";
        AverbacaoTempoServico av = (AverbacaoTempoServico) selecionado;
        if (av.getInicioVigencia() == null || av.getFinalVigencia() == null) {
            tempo = "";
            return;
        }
        if (av.getAnos() != null && av.getMeses() != null && av.getDias() != null) {

            if (av.getAnos() > 0) {
                tempo += av.getAnos() + " anos ";
            }
            if (av.getMeses() > 0) {
                tempo += av.getMeses() + " meses ";
            }
            if (av.getDias() > 0) {
                tempo += av.getDias() + " dias ";
            }
        }
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;

    }

    public void fixaDataInicial() {
        try {
            validarDataAverbacao();
            if (selecionado.getFinalVigencia().after(selecionado.getInicioVigencia())) {
                Period periodo = new Period(new DateTime(selecionado.getInicioVigencia()), new DateTime(selecionado.getFinalVigencia()).plusDays(1), PeriodType.yearMonthDay());
                selecionado.setAnos(periodo.getYears());
                selecionado.setMeses(periodo.getMonths());
                selecionado.setDias(periodo.getDays());
            } else {
                selecionado.setAnos(0);
                selecionado.setMeses(0);
                selecionado.setDias(0);
                FacesUtil.addAtencao("A data de início de averbação é igual a data final da averbação !");
            }
            defineTempo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDataAverbacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Início da Averbação é obrigatório!");
        }
        if (selecionado.getFinalVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Fim da Averbação é obrigatório!");
        }
        if (selecionado.getInicioVigencia() != null &&
            selecionado.getFinalVigencia() != null &&
            selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
        ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início de averbação é maior que a data final da averbação!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoDocumento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDocumento object : tipoDocumentoFacade.listaTipoDocumentosMarcadosComoAverbacao()) {
              toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getOrgaoEmpresa() {
        return Util.getListSelectItem(OrgaoEmpresa.values());
    }

    public ConverterGenerico getConverterTipoDocumento() {
        if (converterTipoDocumento == null) {
            converterTipoDocumento = new ConverterGenerico(TipoDocumento.class, tipoDocumentoFacade);
        }
        return converterTipoDocumento;
    }

    public List<SelectItem> getAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtoLegal object : atoLegalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public void geraNumeroSequencial(SelectEvent evento) {
        AverbacaoTempoServico av = (AverbacaoTempoServico) selecionado;
        av.setContratoFP((ContratoFP) evento.getObject());
        if (av.getContratoFP() != null) {
            Long numeroSequencial = averbacaoTempoServicoFacade.getNumeroSequencial(av.getContratoFP());
            av.setNumero(numeroSequencial);
        }
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public void excluirSelecionado() {
        if (selecionado.getContratoFP() != null && selecionado.getContratoFP().getId() != null) {
            boolean temFichaFinanceira = folhaDePagamentoFacade.existeFolhaPorContratoData(selecionado.getContratoFP(), SistemaFacade.getDataCorrente());
            if (temFichaFinanceira) {
                logger.debug("O servidor " + selecionado.getContratoFP() + " possui ficha financeira.");
                FacesUtil.executaJavaScript("dialogo.show();");
            } else {
                excluirAverbacao();
            }
        }
    }

    public void excluirAverbacao(){
        super.excluir();
    }

    public Boolean getVerificaEdicao() {
        if (selecionado == null) {
            return false;
        }
        if (selecionado.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        AverbacaoTempoServico av = (AverbacaoTempoServico) selecionado;
        if (retorno) {
            if (av.getInicioVigencia().compareTo(new Date()) > 0) {
                FacesUtil.addMessageWarn("Formulario:tempo", "Atenção", "A data inicial de averbação não pode ser superior a data atual !");
                retorno = false;
            }

            if (av.getFinalVigencia() != null) {
                if (av.getFinalVigencia().compareTo(new Date()) > 0) {
                    FacesUtil.addMessageWarn("Formulario:tempo", "Atenção", "A data final de averbação não pode ser superior a data atual !");
                    retorno = false;
                }

                if (av.getInicioVigencia().after(av.getFinalVigencia())) {
                    FacesUtil.addMessageWarn("Atençao", "O início da vigência não pode ser maior que o final de vigência.");
                    retorno = false;
                }
                if (av.getFinalVigencia().after(av.getContratoFP().getDataNomeacao())) {
                    FacesUtil.addMessageWarn("Formulario:tempo", "Atenção", "A data do final da averbação é maior que a data de nomeção do contrato.");
                    retorno = false;
                }
            }
        }
        return retorno;
    }
    private int inicio = 0;

    public int getMaxRegistrosNaLista() {
        return AbstractFacade.MAX_RESULTADOS_NA_CONSULTA - 1;
    }

    public boolean isTemMaisResultados() {
        if (lista == null) {
            lista = listaFiltrandoX();
        }
        return lista.size() >= maximoRegistrosTabela;
    }

    public boolean isTemAnterior() {
        return inicio > 0;
    }

    public List acaoBotaoFiltrar() {
        inicio = 0;
        return listaFiltrandoX();
    }

    public void proximos() {
        inicio += maximoRegistrosTabela;
        listaFiltrandoX();
    }

    public void anteriores() {
        inicio -= maximoRegistrosTabela;
        if (inicio < 0) {
            inicio = 0;
        }
        listaFiltrandoX();
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List getLista() {
        return lista;
    }

    public void setLista(List lista) {
        this.lista = lista;
    }

    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    public int getInicioBuscaTabela() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public List listaFiltrandoX() {
        lista = averbacaoTempoServicoFacade.listaFiltrandoAgrupado(filtro, inicio, maximoRegistrosTabela);
        return lista;
    }

    public List<SelectItem> getTiposRegimesPrevidenciarioEstudoAtuarial() {
        return Util.getListSelectItem(TipoRegimePrevidenciarioEstudoAtuarial.values());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/averbacao-tempo-contribuicao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarDataAverbacao();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
