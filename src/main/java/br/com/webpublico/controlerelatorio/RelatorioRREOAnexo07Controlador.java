/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo7", pattern = "/relatorio/rreo/anexo7/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo07.xhtml")})
public class RelatorioRREOAnexo07Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    private List<String> nomeDosItens;

    public RelatorioRREOAnexo07Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo7", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        nomeDosItens = Lists.newArrayList();
        nomeDosItens.add("RESTOS A PAGAR (EXCETO INTRA-ORÇAMENTÁRIOS) (I)");
        nomeDosItens.add("EXECUTIVO");
        nomeDosItens.add("LEGISLATIVO");
        nomeDosItens.add("RESTOS A PAGAR (INTRA-ORÇAMENTÁRIOS) (II)");
        portalTipoAnexo = PortalTipoAnexo.ANEXO7_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ANO", exercicio.getAno().toString());
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("ANOBIMESTREANTERIOR", (exercicio.getAno() - 1));
        dto.adicionarParametro("nomeDosItens", nomeDosItens);
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getNumeroMesString());
        dto.setNomeRelatorio("RelatorioRREOAnexo07");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo07";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo7/";
    }
}
