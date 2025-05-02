/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo3", pattern = "/relatorio/rreo/anexo3/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo03.xhtml")})
public class RelatorioRREOAnexo03Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioRREOAnexo03Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo3", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO3_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Date dataFinal = DataUtil.montaData(1, bimestre.getMesFinal().getNumeroMes() - 1, exercicio.getAno()).getTime();
        RelatoriosItemDemonst relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("idExAnterior", getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1).getId());
        dto.adicionarParametro("MES", bimestre.getMesFinal().getDescricao().toUpperCase().substring(0, 3) + "/" + DataUtil.getDataFormatada(dataFinal).substring(6, 10));
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getNumeroMesString());
        String data = DataUtil.getDataFormatada(dataFinal, "MM/yyyy");
        for (int i = 1; i <= 11; i++) {
            data = alteraMes(data);
            dto.adicionarParametro("MES-" + i, Mes.getMesToInt(Integer.parseInt(data.substring(0, 2))).getDescricao().toUpperCase().substring(0, 3) + "/" + data.substring(3, 7));
        }
        dto.adicionarParametro("DATAINICIAL", Mes.getMesToInt(Integer.parseInt(data.substring(0, 2))).getDescricao().toUpperCase() + "/" + data.substring(3, 7));
        dto.setNomeRelatorio("RelatorioRREOAnexo03");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo03";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo3/";
    }

    private String alteraMes(String data) {
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 1) {
            mes -= 1;
        } else {
            mes = 12;
            ano -= 1;
        }
        String toReturn;
        if (mes < 10) {
            toReturn = "0" + mes + "/" + ano;
        } else {
            toReturn = mes + "/" + ano;
        }
        return toReturn;
    }
}
