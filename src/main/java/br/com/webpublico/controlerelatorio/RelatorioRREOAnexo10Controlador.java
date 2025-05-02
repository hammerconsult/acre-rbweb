/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author major
 */
@ManagedBean(name = "relatorioRREOAnexo10Controlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-RREO-anexo10", pattern = "/relatorio/rreo/anexo10/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo10.xhtml"),})
public class RelatorioRREOAnexo10Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioRREOAnexo10Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-RREO-anexo10", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO10_RREO;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("EXERCICIO", exercicio.getId());
        dto.adicionarParametro("MENORDATA_1", exercicio.getAno());
        dto.adicionarParametro("MENORDATA", getProjecaoAtuarialFacade().retornaMenorData(exercicio));
        dto.adicionarParametro("MAIORDATA", getProjecaoAtuarialFacade().retornaMaiorData(exercicio));
        dto.adicionarParametro("NOTA", getProjecaoAtuarialFacade().retornaNotasProjecao(exercicio));
        dto.setNomeRelatorio("RelatorioRREOAnexo10");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo10";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo10/";
    }
}
