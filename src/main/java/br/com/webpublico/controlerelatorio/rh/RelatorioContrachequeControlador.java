package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistenteContraCheque;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContraChequeFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.ws.model.WSFichaFinanceira;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Mailson on 22/02/2017.
 */
@ManagedBean(name = "relatorioContrachequeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioContraCheque", pattern = "/relatorio/contracheque/novo/", viewId = "/faces/rh/relatorios/relatoriocontracheque-novo.xhtml"),
    @URLMapping(id = "relatorioContraChequeAntigo", pattern = "/relatorio/contracheque/antigo/", viewId = "/faces/rh/relatorios/relatoriocontracheque.xhtml"),

})
public class RelatorioContrachequeControlador extends RelatorioPagamentoRH {

    private static final String ARQUIVO_JASPER = "ContraChequeNovo.jasper";

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioContrachequeControlador.class);

    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private VinculoFP vinculoFPSelecionado;
    private List<FichaFinanceiraFP> fichasFinanceiras;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContraChequeFacade contraChequeFacade;


    public VinculoFP getVinculoFPSelecionado() {
        return vinculoFPSelecionado;
    }

    public void setVinculoFPSelecionado(VinculoFP vinculoFPSelecionado) {
        this.vinculoFPSelecionado = vinculoFPSelecionado;
    }

    @URLAction(mappingId = "novoRelatorioFichaFinanceira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioFichaFinanceira() {
        limparCampos();
    }

    @URLAction(mappingId = "relatorioContraChequeAntigo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void relatorioContraChequeAntigo() {
        limparCampos();
    }

    public void limparCampos() {
        setMes(null);
        setAno(null);
        vinculoFPSelecionado = null;
        fichasFinanceiras = Lists.newArrayList();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            fichasFinanceiras = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesesAnos(vinculoFPSelecionado, getMes(), getMes(), getAno(), getTipoFolhaDePagamento());
            validarCamposFichaFinanceira();
            AssistenteContraCheque assistente = preencherAssistenteContraCheque();
            RelatorioDTO dto = fichaFinanceiraFPFacade.montarRelatorioDTO(assistente, fichasFinanceiras);
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            contraChequeFacade.validarChaveAutenticidadeFicha(fichasFinanceiras);
            dto.setApi("rh/contra-cheque/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorioFicha() {
        fichasFinanceiras = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesesAnos(vinculoFPSelecionado, getMes(), getMes(), getAno(), getTipoFolhaDePagamento());
        try {
            validarCamposFichaFinanceira();
            String arquivoJasper = "ContraCheque.jasper";
            HashMap parameters = new HashMap();
            setGeraNoDialog(true);
            parameters.put("IMAGEM", getCaminhoImagem() + NOME_IMAGEM_BRASAO_PREFEITURA);
            parameters.put("USUARIO", sistemaFacade.getUsuarioCorrente().getUsername());
            parameters.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("SECRETARIA", "DEPARTAMENTO DE GESTÃO DE PESSOAS");
            parameters.put("NOMERELATORIO", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
            parameters.put("FICHA_ID", montaIdFichas());
            parameters.put("SUBREPORT_DIR", getCaminhoSubReport());
            gerarRelatorio(arquivoJasper, parameters);
        } catch (ValidacaoException val) {
            logger.error("Campos Obrigatórios: ", val.getMessage());
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Ocorreu um erro ao gerar o relatorio: " + e);
        }
    }

    public void gerarRelatorioFichaNovo() {
        try {
            fichasFinanceiras = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesesAnos(vinculoFPSelecionado, getMes(), getMes(), getAno(), getTipoFolhaDePagamento());
            validarCamposFichaFinanceira();
            AssistenteContraCheque assistente = preencherAssistenteContraCheque();
            ByteArrayOutputStream byteRelatorio = fichaFinanceiraFPFacade.gerarContrachequeNovo(assistente);
            byte[] array = byteRelatorio.toByteArray();
            AbstractReport report = AbstractReport.getAbstractReport();
            report.setGeraNoDialog(true);
            report.escreveNoResponse(ARQUIVO_JASPER, array);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Ocorreu um erro ao gerar o relatorio: " + e);
        }
    }

    private AssistenteContraCheque preencherAssistenteContraCheque() {
        List<WSFichaFinanceira> wsFichaFinanceiras = WSFichaFinanceira.convertFichaFinanceiraToWSFichaFinanceiraList(fichasFinanceiras);
        AssistenteContraCheque assistente = new AssistenteContraCheque();
        assistente.setWsFichaFinanceira(wsFichaFinanceiras);
        return assistente;
    }


    private String montaIdFichas() {
        String retorno = " and ficha.id in (";
        for (FichaFinanceiraFP financeiraFP : fichasFinanceiras) {
            retorno += financeiraFP.getId() + ",";
        }
        return retorno.substring(0, retorno.length() - 1) + ")";
    }


    private void validarCamposFichaFinanceira() {
        ValidacaoException ve = new ValidacaoException();
        if (getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês Inicial é obrigatório.");
        }
        if (getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês Final é obrigatório.");
        }
        if (getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (vinculoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor é obrigatório.");
        }
        if (getMes() != null && (getMes() < 1 || getMes() > 12)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um Mês Inicial entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (fichasFinanceiras == null || fichasFinanceiras.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma ficha financeira encontrada para os filtros informados.");
        }
        ve.lancarException();
    }

}
