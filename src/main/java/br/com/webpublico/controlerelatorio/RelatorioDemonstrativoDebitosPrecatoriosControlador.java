package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.DemonstrativoDebitosPrecatorios;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.NaturezaDebito;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioDemonstrativoDebitosPrecatoriosFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 17/05/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-debitos-precatorios", pattern = "/relatorio/demonstrativo-debitos-precatorios/", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-debitos-precatorios.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoDebitosPrecatoriosControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private RelatorioDemonstrativoDebitosPrecatoriosFacade facade;
    private String numeroProcesso;
    private NaturezaDebito naturezaDebito;

    @URLAction(mappingId = "relatorio-debitos-precatorios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        numeroProcesso = "";
        naturezaDebito = null;
    }

    public List<SelectItem> getNaturezasDebitos() {
        return Util.getListSelectItem(NaturezaDebito.values());
    }

    @Override
    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() throws IOException, JRException {
        try {
            validarDatas();
            String arquivoJasper = "RelatorioDemonstrativoDebitosPrecatorios.jasper";
            HashMap parameters = getParametrosPadrao();
            parameters.put("NOMERELATORIO", getNomeRelatorio());
            JRBeanCollectionDataSource bean = new JRBeanCollectionDataSource(recuperarRelatorio());
            parameters.putAll(getFiltrosPadrao());
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), arquivoJasper, parameters, bean);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<DemonstrativoDebitosPrecatorios> recuperarRelatorio() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":dataInicial", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataInicial), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":dataFinal", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataFinal), null, 0, true));

        filtros = "";
        if (numeroProcesso != null && !numeroProcesso.isEmpty()) {
            filtros += " Número do Processo " + numeroProcesso + " -";
            parametros.add(new ParametrosRelatorios(" divida.numeroDocContProc ", ":numeroProcesso", null, OperacaoRelatorio.LIKE, numeroProcesso, null, 1, false));
        }
        if (pessoa != null) {
            filtros += " Pessoa/Credor: " + pessoa.getNome() + " -";
            parametros.add(new ParametrosRelatorios(" p.id ", ":pessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
        }
        if (naturezaDebito != null) {
            filtros += " Natureza da Despesa: " + naturezaDebito.toString() + " -";
            parametros.add(new ParametrosRelatorios(" divida.naturezaDebito ", ":naturezaDebito", null, OperacaoRelatorio.IGUAL, naturezaDebito.name(), null, 1, false));
        }
        return facade.buscarDados(parametros);
    }

    @Override
    public String getNomeRelatorio() {
        return "Demonstrativo de Débitos de Precatórios";
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public NaturezaDebito getNaturezaDebito() {
        return naturezaDebito;
    }

    public void setNaturezaDebito(NaturezaDebito naturezaDebito) {
        this.naturezaDebito = naturezaDebito;
    }
}
