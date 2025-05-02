package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.FiltroRelatorioITBI;
import br.com.webpublico.entidadesauxiliares.VORelatorioITBI;
import br.com.webpublico.relatoriofacade.RelatorioGerencialITBIFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-gerencial-itbi", pattern = "/relatorio/itbi/", viewId = "/faces/tributario/itbi/relatorio/relatorioitbi.xhtml")
})
@ManagedBean
public class RelatorioGerencialITBIControlador extends AbstractReport implements Serializable {

    @EJB
    private RelatorioGerencialITBIFacade relatorioFacade;
    private FiltroRelatorioITBI filtroRelatorioITBI;
    private String filtros;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<List<VORelatorioITBI>> futureDados;

    public RelatorioGerencialITBIControlador() {
    }

    @URLAction(mappingId = "relatorio-gerencial-itbi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtroRelatorioITBI = new FiltroRelatorioITBI();
        filtros = "";
    }

    public void gerarRelatorio() {
        futureDados = relatorioFacade.buscarItbs(montarCondicao());
    }

    private String montarCondicao() {
        StringBuilder sb = new StringBuilder();
        String clausula = " where ";
        filtros = "";

        if (!isStringVazia(filtroRelatorioITBI.getSetorInicial())) {
            sb.append(clausula).append(" cast(setor.codigo as number) >= ").append(filtroRelatorioITBI.getSetorInicial());
            clausula = " and ";
            filtros += " Setor Inicial: " + filtroRelatorioITBI.getSetorInicial() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getSetorFinal())) {
            sb.append(clausula).append(" cast(setor.codigo as number) <= ").append(filtroRelatorioITBI.getSetorFinal());
            clausula = " and ";
            filtros += " Setor Final: " + filtroRelatorioITBI.getSetorFinal() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getQuadraInicial())) {
            sb.append(clausula).append(" cast(quadra.codigo as number) >= ").append(filtroRelatorioITBI.getQuadraInicial());
            clausula = " and ";
            filtros += " Quadra Inicial: " + filtroRelatorioITBI.getQuadraInicial() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getQuadraFinal())) {
            sb.append(clausula).append(" cast(quadra.codigo as number) <= ").append(filtroRelatorioITBI.getQuadraFinal());
            clausula = " and ";
            filtros += " Quadra Final: " + filtroRelatorioITBI.getQuadraFinal() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getLoteInicial())) {
            sb.append(clausula).append(" cast(lote.codigolote as number) >= ").append(filtroRelatorioITBI.getLoteInicial());
            clausula = " and ";
            filtros += " Lote Inicial: " + filtroRelatorioITBI.getLoteInicial() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getLoteFinal())) {
            sb.append(clausula).append(" cast(lote.codigolote as number) <= ").append(filtroRelatorioITBI.getLoteFinal());
            clausula = " and ";
            filtros += " Lote Final: " + filtroRelatorioITBI.getLoteFinal() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getInscricaoInicial())) {
            sb.append(clausula).append(" (imob.inscricaoCadastral >= ").append(filtroRelatorioITBI.getInscricaoInicial());
            sb.append(" or cast(cr.codigo as varchar(255)) >= ").append(filtroRelatorioITBI.getInscricaoInicial()).append(" )");
            clausula = " and ";
            filtros += " Inscrição Imobiliária Inicial: " + filtroRelatorioITBI.getInscricaoInicial() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getInscricaoFinal())) {
            sb.append(clausula).append(" (imob.inscricaoCadastral <= ").append(filtroRelatorioITBI.getInscricaoFinal());
            sb.append(" or cast(cr.codigo as varchar(255)) <= ").append(filtroRelatorioITBI.getInscricaoFinal()).append(" )");
            clausula = " and ";
            filtros += " Inscrição Imobiliária Final: " + filtroRelatorioITBI.getInscricaoFinal() + ", ";
        }
        if (filtroRelatorioITBI.getTransmitente() != null) {
            sb.append(clausula).append(" itbi.id in (select transmi.calculoitbi_id from TransmitentesCalculoITBI transmi ")
                .append(" inner join pessoa pes on transmi.pessoa_id = pes.id ")
                .append(" where pes.id = ").append(filtroRelatorioITBI.getTransmitente().getId()).append(")");
            clausula = " and ";
            filtros += " Transmitente: " + filtroRelatorioITBI.getTransmitente().getNomeCpfCnpj() + ", ";
        }
        if (filtroRelatorioITBI.getAdquirente() != null) {
            sb.append(clausula).append(" itbi.id in (select adquirentes.calculoitbi_id from AdquirentesCalculoITBI adquirentes ")
                .append(" inner join pessoa pes on adquirentes.adquirente_id = pes.id ")
                .append(" where pes.id = ").append(filtroRelatorioITBI.getAdquirente().getId()).append(")");
            clausula = " and ";
            filtros += " Adquirente: " + filtroRelatorioITBI.getAdquirente().getNomeCpfCnpj() + ", ";
        }
        if (!isStringVazia(filtroRelatorioITBI.getNumeroLaudo())) {
            sb.append(clausula).append(" itbi.sequencia = ").append(filtroRelatorioITBI.getNumeroLaudo());
            filtros += " Número do Laudo: " + filtroRelatorioITBI.getNumeroLaudo() + ", ";
        }
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 2);
        return sb.toString();
    }

    public void imprimirRelatorio() {
        try {
            HashMap parametros = Maps.newHashMap();
            String arquivoJasper = "RelatorioITBI.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarRegistros());
            parametros.put("SUBREPORT_DIR", getCaminhoSubReport());
            parametros.put("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("FILTROS", filtros);
            parametros.put("MODULO", "Tributário");
            gerarRelatorioComDadosEmCollection(arquivoJasper, parametros, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<VORelatorioITBI> buscarRegistros() throws ExecutionException, InterruptedException {
        List<VORelatorioITBI> dados = Lists.newArrayList();
        for (VORelatorioITBI voRelatorioITBI: futureDados.get()) {
            dados.add(voRelatorioITBI);
        }
        return dados;
    }

    private boolean isStringVazia(String campo) {
        return campo == null || campo.isEmpty();
    }

    public boolean isFutureConcluida() {
        return futureDados != null && futureDados.isDone();
    }

    public void consultarAndamentoEmissaoRelatorio() {
        if (isFutureConcluida()) {
            FacesUtil.executaJavaScript("terminarRelatorio()");
        }
    }

    public FiltroRelatorioITBI getFiltroRelatorioITBI() {
        return filtroRelatorioITBI;
    }

    public void setFiltroRelatorioITBI(FiltroRelatorioITBI filtroRelatorioITBI) {
        this.filtroRelatorioITBI = filtroRelatorioITBI;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public Future<List<VORelatorioITBI>> getFutureDados() {
        return futureDados;
    }

    public void setFutureDados(Future<List<VORelatorioITBI>> futureDados) {
        this.futureDados = futureDados;
    }
}
