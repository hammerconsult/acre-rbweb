/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroCreditoReceberTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoCreditoReceberTributario;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioCreditoReceber",
        pattern = "/tributario/relatorios/credito/",
        viewId = "/faces/tributario/relatorio/creditoreceber.xhtml"),
})

public class RelatorioCreditoReceberTributarioControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioCreditoReceberTributarioControlador.class);

    @EJB
    private AgrupadorRelatorioCreditoFacade agrupadorRelatorioCreditoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    private FiltroCreditoReceberTributario filtro;

    public FiltroCreditoReceberTributario getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroCreditoReceberTributario filtro) {
        this.filtro = filtro;
    }

    @URLAction(mappingId = "relatorioCreditoReceber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroCreditoReceberTributario();
    }

    public void limparDividas() {
        filtro.getDividas().clear();
    }

    public List<TipoCreditoReceberTributario> getTodosTiposCredito() {
        return ordenarTiposCredito(TipoCreditoReceberTributario.buscarTiposAgrupadorRelatorioCredito().toArray(new TipoCreditoReceberTributario[0]));
    }

    private List<TipoCreditoReceberTributario> ordenarTiposCredito(TipoCreditoReceberTributario[] tiposCreditos) {
        List<TipoCreditoReceberTributario> values = Lists.newArrayList(tiposCreditos);

        Collections.sort(values, new Comparator<TipoCreditoReceberTributario>() {
            @Override
            public int compare(TipoCreditoReceberTributario o1, TipoCreditoReceberTributario o2) {
                return o1.getOrdemApresentacao().compareTo(o2.getOrdemApresentacao());
            }
        });
        return values;
    }

    public List<SelectItem> getListaDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Divida divida : dividaFacade.lista()) {
            toReturn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return toReturn;
    }

    public List<AgrupadorRelatorioCredito> completarCreditos(String filtro) {
        return agrupadorRelatorioCreditoFacade.listaFiltrando(filtro.trim(), "descricao");
    }

    public List<Divida> completarDividas(String filtro) {
        return dividaFacade.listaFiltrandoDividas(filtro.trim(), "descricao");
    }

    public List<SelectItem> getListaTributos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Tributo tributo : tributoFacade.lista()) {
            toReturn.add(new SelectItem(tributo, tributo.getDescricao()));
        }
        return toReturn;
    }

    public void adicionarDivida() {
        try {
            Divida.validarDividaParaAdicaoEmLista(filtro.getDivida(), filtro.getDividas());
            filtro.getDividas().add(filtro.getDivida());
            filtro.setDivida(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removerDivida(Divida divida) {
        if (filtro.getDividas().contains(divida)) {
            filtro.getDividas().remove(divida);
        }
    }


    public void adicionarTributo() {
        try {
            Tributo.validarTributoParaAdicaoEmLista(filtro.getTributo(), filtro.getTributos());
            filtro.getTributos().add(filtro.getTributo());
            filtro.setTributo(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removerTributo(Tributo tributo) {
        if (filtro.getTributos().contains(tributo)) {
            filtro.getTributos().remove(tributo);
        }
    }

    public void adicionarConta() {
        try {
            validarConta();
            filtro.getContas().add(filtro.getContaReceita());
            filtro.setContaReceita(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removerConta(ContaReceita conta) {
        if (filtro.getContas().contains(conta)) {
            filtro.getContas().remove(conta);
        }
    }

    private void validarConta() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getContaReceita() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma conta para adicionar");
        } else if (filtro.getContas().contains(filtro.getContaReceita())) {
            ve.adicionarMensagemDeCampoObrigatorio("Essa Conta já foi selecionada.");
        }
        ve.lancarException();
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getTiposCredito() == null || filtro.getTiposCredito().length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos um tipo de crédito");
        }
        ve.lancarException();
    }

    public List<Conta> buscarContaReceita(String parte) {
        return contaFacade.listaFiltrandoContaReceitaPorExercicio(parte, tributoFacade.getExercicioFacade().getExercicioCorrente());
    }

    public void gerarRelatorio() {
        try {
            validarRelatorio();
            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("CREDITO-RECEBER-TRIBUTARIO");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", usuarioCorrente.getNome(), false);
            dto.adicionarParametro("UNIDADE", SistemaService.getInstance().getOrcamentariaCorrente().getDescricao());
            dto.adicionarParametro("WHERE", filtro.getWhere());
            dto.adicionarParametro("FILTROS", filtro.getFiltrosUtilizados());
            dto.adicionarParametro("REFERENCIAFINAL", DateUtils.getDataFormatada(filtro.getReferenciaFinal()));
            dto.adicionarParametro("AGRUPARENTIDADE", filtro.getAgruparEntidade());
            dto.adicionarParametro("DETALHAR", filtro.getDetalhar());
            dto.setApi("tributario/credito-receber/");
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }
}
