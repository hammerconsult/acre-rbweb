package br.com.webpublico.nfse.relatorio.controladores;


import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioTomadoresServico;
import br.com.webpublico.nfse.domain.dtos.RelatorioMaioresTomadoresServico;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.faces.bean.ManagedBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-maiores-tomadores",
        pattern = "/nfse/relatorio/maiores-tomadores-servico/",
        viewId = "/faces/tributario/nfse/relatorio/maiores-tomadores-servico.xhtml")
})
public class RelatorioMaioresTomadoresServicoControlador extends AbstractReport {

    private FiltroRelatorioTomadoresServico filtro;

    @URLAction(mappingId = "novo-relatorio-maiores-tomadores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioTomadoresServico();
        filtro.setDataInicial(DataUtil.getPrimeiroDiaAno(new Date()));
        filtro.setDataFinal(new Date());
    }

    public void gerarRelatorio() {
        try {
            filtro.validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("TITULO", "Municipio de Rio Branco");
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("MOSTRAR_DATAEMISSAO", filtro.getMostrarDataEmissao());
            dto.adicionarParametro("NOME_RELATORIO", "RELATÓRIO DE MAIORES TOMADORES DE SERVIÇO");
            dto.adicionarParametro("FILTROS", filtro.montarDescricaoFiltros());
            dto.setApi("tributario/maioresTomadoresServico/");
            dto.adicionarParametro("CONDICAO", montarCondicao());
            dto.setNomeRelatorio("Relatório de Maiores Tomadores de Serviço");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();

        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String montarCondicao() {
        String sql = "  where coalesce(nota.emissao, sd.emissao) between to_date('"+DataUtil.getDataFormatada(filtro.getDataInicial())+"', 'dd/mm/yyyy') and to_date('"+DataUtil.getDataFormatada(filtro.getDataInicial())+"', 'dd/mm/yyyy') ";

        if (!Strings.isNullOrEmpty(filtro.getCnpjInicial())) {
            sql += " and pj.cnpj >=  '";
            sql += StringUtil.retornaApenasNumeros(filtro.getCnpjInicial());
            sql += "'";
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjInicial())) {
            sql += " and pj.cnpj <= '";
            sql += StringUtil.retornaApenasNumeros(filtro.getCnpjFinal());
            sql += "'";
        }
        if (filtro.getSituacaoNota() != null) {
            sql += " and dec.situacao = '";
            sql += filtro.getSituacaoNota().name();
            sql += "'";
        }
        if (filtro.getTipoDocumentoNfse() != null) {
            sql += " and dec.tipodocumento = '";
            sql += filtro.getTipoDocumentoNfse().name();
            sql += "'";
        }
        if (filtro.getPessoa() != null) {
            sql += " and p.id = ";
            sql += StringUtil.retornaApenasNumeros(filtro.getPessoa().getId().toString());

        }



        return sql;
    }

    public List<SelectItem> getTiposDocumentos() {
        return Util.getListSelectItem(TipoDocumentoNfse.values());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoNota.values());
    }

    public FiltroRelatorioTomadoresServico getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioTomadoresServico filtro) {
        this.filtro = filtro;
    }
}
