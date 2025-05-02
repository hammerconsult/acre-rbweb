package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 22/03/2017.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "distribuicao-contribuicao-sindical", pattern = "/relatorio/distribuicao-contribuicao-sindical/", viewId = "/faces/rh/relatorios/relatoriodistribuicaocontribuicaosindical.xhtml")
})
public class RelatorioDistribuicaoContribuicaoSindicalControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDistribuicaoContribuicaoSindicalControlador.class);
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Mes mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Integer versao;
    private HierarquiaOrganizacional orgao;

    public RelatorioDistribuicaoContribuicaoSindicalControlador() {
    }

    @URLAction(mappingId = "distribuicao-contribuicao-sindical", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = Mes.JANEIRO;
        ano = sistemaControlador.getExercicioCorrenteAsInteger();
        tipoFolhaDePagamento = null;
        versao = null;
        orgao = null;
    }

    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            return Util.getListSelectItem(folhaDePagamentoFacade.recuperarVersao(mes, ano, tipoFolhaDePagamento));
        }
        return retorno;
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/distribuicao-contribuicao-sindical/");
            dto.setNomeRelatorio("distribuicao-contribuicao-sindical");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("DESCRICAOMES", mes.getDescricao() + " - " + ano);
            dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.name());
            dto.adicionarParametro("MODULO", "RH");
            dto.adicionarParametro("VERSAO", versao);
            dto.adicionarParametro("codigoOrgao", orgao != null ? orgao.getCodigoSemZerosFinais() : "");
            dto.adicionarParametro("mes", mes.getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("ano", ano);
            getUsers(dto);
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório RelatorioDistribuicaoContribuicaoSindical", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void getUsers(RelatorioDTO dto) {
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório.");
        }
        ve.lancarException();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public HierarquiaOrganizacional getOrgao() {
        return orgao;
    }

    public void setOrgao(HierarquiaOrganizacional orgao) {
        this.orgao = orgao;
    }
}
