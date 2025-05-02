
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioContratosPorLotacaoAdmistrativa", pattern = "/relatorio/contratos-por-lotacao-administrativa/novo/", viewId = "/faces/rh/relatorios/relatoriocontratosporlotacaoadministrativa.xhtml")
})
public class RelatorioContratosPorLotacaoAdministrativaControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Date dataReferencia;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private TipoPesquisa tipoPesquisa;


    public RelatorioContratosPorLotacaoAdministrativaControlador() {
        tipoPesquisa = TipoPesquisa.ORGAO;
        geraNoDialog = Boolean.TRUE;
    }

    @URLAction(mappingId = "novoRelatorioServidoresPorOrgao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        dataReferencia = null;
    }

    public void limparUnidade() {
        hierarquiaOrganizacionalSelecionada = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);

            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICIPIO DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("dataReferencia", dataReferencia);
            dto.adicionarParametro("REFERENCIA", DataUtil.getDataFormatada(dataReferencia));
            if(TipoPesquisa.LOTACAO_ADMINISTRATIVA.equals(tipoPesquisa)){
                dto.adicionarParametro("LOTACAO", hierarquiaOrganizacionalSelecionada.getCodigo());
            } else{
                dto.adicionarParametro("LOTACAO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
            }
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.setNomeRelatorio("RELATÓRIO-CONTRATOS-POR-LOTACAO-ADMINISTRATIVA");
            dto.setApi("rh/contratos-por-lotacao-administrativa/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência deve ser informado.");
        }

        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Lotação deve ser informado.");
        }
        ve.lancarException();
    }

    public void validarDataReferencia() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência deve ser informado.");
        }
        ve.lancarException();
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        if(dataReferencia == null){
            FacesUtil.addWarn("Atenção!", "Preencha uma data para listar as lotações vigentes na data de referência.");
            return new ArrayList<>();
        }

        if (TipoPesquisa.ORGAO.equals(tipoPesquisa)) {
            return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(),TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),dataReferencia );
        }
        if (TipoPesquisa.LOTACAO_ADMINISTRATIVA.equals(tipoPesquisa)) {
            try {
                return hierarquiaOrganizacionalFacade.listaTodasHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), dataReferencia);
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addError("Erro: ", ex.getMessage());
            } catch (Exception e) {
                FacesUtil.addError("Erro: ", e.getMessage());
            }
        }

        return new ArrayList<>();
    }

    public enum TipoPesquisa {

        ORGAO("Órgão"),
        LOTACAO_ADMINISTRATIVA("Lotação Administrativa");

        TipoPesquisa(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

    }

    public TipoPesquisa getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(TipoPesquisa tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public List<TipoPesquisa> getTiposDePesquisa() {
        List<TipoPesquisa> toReturn = Lists.newArrayList();
        for (TipoPesquisa tipoPesquisa : TipoPesquisa.values()) {
            toReturn.add(tipoPesquisa);
        }
        return toReturn;
    }

}
