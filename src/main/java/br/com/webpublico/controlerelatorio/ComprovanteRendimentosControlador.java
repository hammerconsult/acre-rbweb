package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.AssistenteComprovanteRendimentos;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ComprovanteRendimentosFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peixe
 * on 17/08/2015.
 */

@ManagedBean(name = "comprovanteRendimentosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "comprovanteRendimentos", pattern = "/comprovante-rendimentos/",
        viewId = "/faces/rh/relatorios/comprovanteRendimentos.xhtml")
})
public class ComprovanteRendimentosControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ComprovanteRendimentosControlador.class);
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private VinculoFP vinculoFP;
    private Exercicio anoCalendario;
    @EJB
    private ComprovanteRendimentosFacade comprovanteRendimentosFacade;
    private List<VinculoFP> vinculos;

    public List<VinculoFP> buscarVinculos(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    private AssistenteComprovanteRendimentos preencherAssistente(){
        AssistenteComprovanteRendimentos assistente = new AssistenteComprovanteRendimentos();
        assistente.setAnoCalendario(anoCalendario);
        assistente.getVinculos().addAll(vinculos);
        return assistente;
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            vinculos = comprovanteRendimentosFacade.buscarVinculosPessoaVigentePorAno(vinculoFP.getMatriculaFP().getPessoa(), anoCalendario);
            if (vinculos != null && !vinculos.isEmpty()) {
                RelatorioDTO dto = comprovanteRendimentosFacade.montarRelatorioDTO(preencherAssistente());
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("USUARIO", sistemaFacade.getLogin());
                dto.setNomeRelatorio("RELATÓRIO-COMPROVANTE-RENDIMENTOS");
                dto.setApi("rh/comprovante-rendimentos/");
                ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível encontrar registro da dirf para o servidor(a) " + vinculoFP + " para o ano " + anoCalendario.getAno());
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível encontrar registro da dirf para o servidor(a) " + vinculoFP + " para o ano " + anoCalendario.getAno());
            logger.error("gerarRelatorio {} " + e);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (vinculoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo servidor deve ser selecionado");
        }
        if (anoCalendario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo exercício deve ser selecionado");
        }
        ve.lancarException();
    }

    public List<SelectItem> getExerciciosDirfCodigo() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Exercicio exercicio : comprovanteRendimentosFacade.listaExerciciosPorDirfCodigo()) {
            retorno.add(new SelectItem(exercicio, exercicio.getAno().toString()));
        }
        return retorno;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Exercicio getAnoCalendario() {
        return anoCalendario;
    }

    public void setAnoCalendario(Exercicio anoCalendario) {
        this.anoCalendario = anoCalendario;
    }
}
