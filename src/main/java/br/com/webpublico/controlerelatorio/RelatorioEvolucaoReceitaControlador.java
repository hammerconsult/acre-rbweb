/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.LogoRelatorio;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-evolucao-receita", pattern = "/relatorio/lei4320/evolucao-receita/", viewId = "/faces/financeiro/relatorio/relatorioevolucaoreceita.xhtml")
})
public class RelatorioEvolucaoReceitaControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    private LogoRelatorio logoRelatorio;
    private Boolean mostrarUsuario;

    public RelatorioEvolucaoReceitaControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-evolucao-receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mostrarUsuario = Boolean.FALSE;
        logoRelatorio = LogoRelatorio.PREFEITURA;
        portalTipoAnexo = PortalTipoAnexo.EVOLUCAO_RECEITA;
        super.limparCampos();
    }

    public List<SelectItem> getLogosRelatorio() {
        return Util.getListSelectItemSemCampoVazio(LogoRelatorio.values());
    }

    public LogoRelatorio getLogoRelatorio() {
        return logoRelatorio;
    }

    public void setLogoRelatorio(LogoRelatorio logoRelatorio) {
        this.logoRelatorio = logoRelatorio;
    }

    public Boolean getMostrarUsuario() {
        return mostrarUsuario;
    }

    public void setMostrarUsuario(Boolean mostrarUsuario) {
        this.mostrarUsuario = mostrarUsuario;
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Exercicio exercicioMenosUm = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        Exercicio exercicioMenosDois = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 2);
        Exercicio exercicioMenosTres = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 3);
        Exercicio exercicioMenosQuatro = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 4);
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Evolucao da Receita", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("USER", mostrarUsuario ? "Emitido por: " + getSistemaFacade().getUsuarioCorrente().getNome() : "");
        dto.adicionarParametro("isCamara", LogoRelatorio.CAMARA.equals(logoRelatorio));
        dto.adicionarParametro("MUNICIPIO", LogoRelatorio.PREFEITURA.equals(logoRelatorio) ? "MUNICÍPIO DE RIO BRANCO" : "CÂMARA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("ANO_ATUAL", exercicio.getAno());
        dto.adicionarParametro("ANO-1", exercicioMenosUm.getAno());
        dto.adicionarParametro("ANO-2", exercicioMenosDois.getAno());
        dto.adicionarParametro("ANO-3", exercicioMenosTres.getAno());
        dto.adicionarParametro("ANO-4", exercicioMenosQuatro.getAno());
        dto.adicionarParametro("VARIACAO_1", exercicioMenosTres.getAno() + "/" + exercicioMenosQuatro.getAno());
        dto.adicionarParametro("VARIACAO_2", exercicioMenosDois.getAno() + "/" + exercicioMenosTres.getAno());
        dto.adicionarParametro("VARIACAO_3", exercicioMenosUm.getAno() + "/" + exercicioMenosDois.getAno());
        dto.adicionarParametro("VARIACAO_4", exercicio.getAno() + "/" + exercicioMenosUm.getAno());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio(getNomeArquivo());
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioEvolucaoReceita";
    }

    @Override
    public String getApi() {
        return "contabil/evolucao-receita/";
    }
}
