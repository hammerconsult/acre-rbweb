package br.com.webpublico.controle.rh.provadevida;

import br.com.webpublico.entidadesauxiliares.ConsultaBeneficiarioProvaDeVidaVO;
import br.com.webpublico.entidadesauxiliares.rh.FiltroProvaDeVida;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-consulta-dados-prova-vida", pattern = "/consulta-dados-prova-vida/", viewId = "/faces/rh/consulta-provadevida/edita.xhtml"),
})
public class ConsultaDadosProvaDeVidaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaDadosProvaDeVidaControlador.class);

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private FiltroProvaDeVida filtroProvaDeVida;
    private List<ConsultaBeneficiarioProvaDeVidaVO> beneficiarioProvaDeVidaVOList;
    private List<ConsultaBeneficiarioProvaDeVidaVO> filtroBeneficiarioProvaDeVidaVO;

    public ConsultaDadosProvaDeVidaControlador() {
        filtroProvaDeVida = new FiltroProvaDeVida();
    }

    @URLAction(mappingId = "novo-consulta-dados-prova-vida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroProvaDeVida = new FiltroProvaDeVida();
    }

    public void gerarProvaDeVida() {
        try {
            filtroProvaDeVida.validarGeracao();
            beneficiarioProvaDeVidaVOList = vinculoFPFacade.buscarDadosBeneficiariosProvaDeVida(filtroProvaDeVida.getTipoFolhaDePagamento(), filtroProvaDeVida.getTipoBeneficiario(), filtroProvaDeVida.getAno(), Integer.parseInt(filtroProvaDeVida.getMes()), filtroProvaDeVida.isApenasAniversariantes());
            if (beneficiarioProvaDeVidaVOList.isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("Não há beneficiários disponíveis com os filtros informados.");
            }
            filtroBeneficiarioProvaDeVidaVO = null;
            FacesUtil.atualizarComponente("tabela-inativos");
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } else {
                FacesUtil.addOperacaoNaoRealizada(ve.getMessage());
            }
        } catch (Exception ex) {
            logger.error("Erro ao consultar dados de prova de vida! " + ex);
            FacesUtil.addOperacaoNaoRealizada("Erro ao consultar dados de prova de vida! " + ex.getMessage());
        }
    }

    public FiltroProvaDeVida getFiltroProvaDeVida() {
        return filtroProvaDeVida;
    }

    public void setFiltroProvaDeVida(FiltroProvaDeVida filtroProvaDeVida) {
        this.filtroProvaDeVida = filtroProvaDeVida;
    }

    public List<SelectItem> getTipoFolhaDePagamentos() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<ConsultaBeneficiarioProvaDeVidaVO> getBeneficiarioProvaDeVidaVOList() {
        return beneficiarioProvaDeVidaVOList;
    }

    public void setBeneficiarioProvaDeVidaVOList(List<ConsultaBeneficiarioProvaDeVidaVO> beneficiarioProvaDeVidaVOList) {
        this.beneficiarioProvaDeVidaVOList = beneficiarioProvaDeVidaVOList;
    }

    public List<ConsultaBeneficiarioProvaDeVidaVO> getFiltroBeneficiarioProvaDeVidaVO() {
        return filtroBeneficiarioProvaDeVidaVO;
    }

    public void setFiltroBeneficiarioProvaDeVidaVO(List<ConsultaBeneficiarioProvaDeVidaVO> filtroBeneficiarioProvaDeVidaVO) {
        this.filtroBeneficiarioProvaDeVidaVO = filtroBeneficiarioProvaDeVidaVO;
    }
}
