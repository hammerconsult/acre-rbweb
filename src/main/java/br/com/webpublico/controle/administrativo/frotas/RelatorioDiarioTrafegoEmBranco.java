package br.com.webpublico.controle.administrativo.frotas;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Veiculo;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 15/02/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioDiarioTrafego", pattern = "/diario-trafego-sem-informacoes/", viewId = "/faces/administrativo/frota/relatorios/relatoriodiariotrafego.xhtml")
})
public class RelatorioDiarioTrafegoEmBranco {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDiarioTrafegoEmBranco.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Exercicio exercicio;
    private Veiculo veiculo;
    private Mes mes;

    @URLAction(mappingId = "novoRelatorioDiarioTrafego", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparFiltros();
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) throws JRException, IOException {
        try {
            validarImpressaoDiarioTrafego();
            String nomeRelatorio = "BOLETIM DIÁRIO DE TRAFEGO - BDT";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            if (veiculo == null) {
                dto.adicionarParametro("SECRETARIA_NOMERELATORIO", "BOLETIM DIÁRIO DE TRAFEGO - BDT");
            } else {
                HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), veiculo.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
                if (hierarquiaOrganizacional != null) {
                    dto.adicionarParametro("SECRETARIA_NOMERELATORIO", hierarquiaOrganizacional.toString());
                }
                dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            }
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
            dto.adicionarParametro("ANO", exercicio.getAno());
            dto.adicionarParametro("MES", mes.getDescricao());
            if (veiculo != null) {
                dto.adicionarParametro("VEICULO", veiculo.getBem() != null ? veiculo.getBem().getDescricao() : veiculo.getDescricao());
                dto.adicionarParametro("PLACA", veiculo.getPlaca());
            }
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/diario-trafego-em-branco/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarImpressaoDiarioTrafego() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Mes m : Mes.values()) {
            retorno.add(new SelectItem(m, m.getDescricao()));
        }
        return retorno;
    }

    public void limparFiltros() {
        exercicio = null;
        veiculo = null;
        mes = null;
    }
}
