package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.TributoTaxaDividasDiversas;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoTaxasDiversas;
import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoTaxasDiversasDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoCalculoDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoDamDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoCadastroTributarioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoTaxasDiversas",
        pattern = "/tributario/taxas-diversas/relacao-taxas-diversas/",
        viewId = "/faces/tributario/taxasdividasdiversas/relatorios/relacaotaxasdiversas.xhtml")})
public class RelacaoTaxasDiversasControlador {

    private FiltroRelacaoTaxasDiversas filtroRelatorio;
    @EJB
    SistemaFacade sistemaFacade;


    @URLAction(mappingId = "novaRelacaoTaxasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroRelatorio = new FiltroRelacaoTaxasDiversas();
        filtroRelatorio.setDataLancamentoInicial(DataUtil.getPrimeiroDiaAno(sistemaFacade.getExercicioCorrente().getAno()));
        filtroRelatorio.setDataLancamentoFinal(sistemaFacade.getDataOperacao());
    }

    public void gerarRelatorio() {
        try {
            filtroRelatorio.validarInformacoes();
            filtroRelatorio.montarDescricaoFiltro();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("RELATÓRIO-TAXAS-DIVERSAS");
            dto.setApi("tributario/relacao-taxas-diversas/");
            dto.adicionarParametro("FILTROS", filtroRelatorio.getFiltro().toString());
            preencherFiltroTaxas(dto);
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void preencherFiltroTaxas(RelatorioDTO relatorioDTO) {
        FiltroRelacaoTaxasDiversasDTO filtroDto = new FiltroRelacaoTaxasDiversasDTO();
        filtroDto.setDataLancamentoInicial(filtroRelatorio.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelatorio.getDataLancamentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelatorio.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelatorio.getDataVencimentoFinal());
        TipoCadastroTributario tipoCadastroTributario = filtroRelatorio.getTipoCadastroTributario();
        if (tipoCadastroTributario != null) {
            filtroDto.setTipoCadastroTributario(TipoCadastroTributarioDTO.valueOf(tipoCadastroTributario.name()));
        }
        filtroDto.setCadastroInicial(filtroRelatorio.getCadastroInicial());
        filtroDto.setCadastroFinal(filtroRelatorio.getCadastroFinal());
        filtroDto.setNomeInicial(filtroRelatorio.getNomeInicial());
        filtroDto.setNomeFinal(filtroRelatorio.getNomeFinal());
        SituacaoCalculoDTO situacaoCalculo = filtroDto.getSituacaoCalculo();
        if (situacaoCalculo != null) {
            filtroDto.setSituacaoCalculo(SituacaoCalculoDTO.valueOf(situacaoCalculo.name()));
        }
        SituacaoDamDTO situacaoDam = filtroDto.getSituacaoDAM();
        if (situacaoDam != null) {
            filtroDto.setSituacaoDAM(SituacaoDamDTO.valueOf(situacaoDam.name()));
        }
        UsuarioSistema usuarioTaxa = filtroRelatorio.getUsuarioTaxa();
        if (usuarioTaxa != null) {
            filtroDto.setUsuarioTaxa(usuarioTaxa.getId());
        }
        UsuarioSistema usuarioDAM = filtroRelatorio.getUsuarioDAM();
        if (usuarioDAM != null) {
            filtroDto.setUsuarioDAM(usuarioDAM.getId());
        }
        filtroDto.setNumeroTaxaInicial(filtroRelatorio.getNumeroTaxaInicial());
        filtroDto.setNumeroTaxaFinal(filtroRelatorio.getNumeroTaxaFinal());
        filtroDto.setFiltro(filtroRelatorio.getFiltro());
        TributoTaxaDividasDiversas tributoTaxaDividasDiversas = filtroRelatorio.getTributoTaxaDividasDiversas();
        if (tributoTaxaDividasDiversas != null) {
            filtroDto.setIdTributoTaxaDividasDiversas(tributoTaxaDividasDiversas.getId());
        }
        relatorioDTO.adicionarParametro("filtroDto", filtroDto);
    }

    public FiltroRelacaoTaxasDiversas getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public List<SelectItem> tiposDeCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public List<SelectItem> situacoesCalculo() {
        return Util.getListSelectItem(SituacaoCalculo.values());
    }

    public List<SelectItem> situacoesDAM() {
        return Util.getListSelectItem(DAM.Situacao.values());
    }

    public void processaSelecaoTipoCadastro() {
        filtroRelatorio.setCadastroInicial("");
        filtroRelatorio.setCadastroFinal("");
    }
}
