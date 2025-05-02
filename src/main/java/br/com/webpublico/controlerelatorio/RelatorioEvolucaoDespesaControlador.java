/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.ItemDemonstrativoComponenteDTO;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UsuarioSistema;
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

/**
 * @author major
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-evolucao-despesa", pattern = "/relatorio/lei4320/evolucao-despesa/", viewId = "/faces/financeiro/relatorio/relatorioevolucaodespesa.xhtml")
})
public class RelatorioEvolucaoDespesaControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    private LogoRelatorio logoRelatorio;
    private Boolean mostraUsuario;

    public RelatorioEvolucaoDespesaControlador() {
        super();
    }

    public List<SelectItem> buscarLogos() {
        return Util.getListSelectItemSemCampoVazio(LogoRelatorio.values());
    }

    @URLAction(mappingId = "relatorio-evolucao-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mostraUsuario = Boolean.FALSE;
        logoRelatorio = LogoRelatorio.PREFEITURA;
        portalTipoAnexo = PortalTipoAnexo.EVOLUCAO_DESPESA;
        super.limparCampos();
    }

    public LogoRelatorio getLogoRelatorio() {
        return logoRelatorio;
    }

    public void setLogoRelatorio(LogoRelatorio logoRelatorio) {
        this.logoRelatorio = logoRelatorio;
    }

    public Boolean getMostraUsuario() {
        return mostraUsuario == null ? Boolean.FALSE : mostraUsuario;
    }

    public void setMostraUsuario(Boolean mostraUsuario) {
        this.mostraUsuario = mostraUsuario;
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Evolucao da Despesa", exercicio, TipoRelatorioItemDemonstrativo.LEI_4320);
        Integer anoMenosUm = exercicio.getAno() - 1;
        Integer anoMenosDois = anoMenosUm - 1;
        Integer anoMenosTres = anoMenosDois - 1;
        Integer anoMenosQuatro = anoMenosTres - 1;
        Exercicio exercicioMenosUm = getExercicioFacade().getExercicioPorAno(anoMenosUm);
        Exercicio exercicioMenosDois = getExercicioFacade().getExercicioPorAno(anoMenosDois);
        Exercicio exercicioMenosTres = getExercicioFacade().getExercicioPorAno(anoMenosTres);
        Exercicio exercicioMenosQuatro = getExercicioFacade().getExercicioPorAno(anoMenosQuatro);
        UsuarioSistema usuarioCorrente = getSistemaFacade().getUsuarioCorrente();
        boolean isCamara = LogoRelatorio.CAMARA.equals(logoRelatorio);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", isCamara ? "CÂMARA MUNICIPAL DE RIO BRANCO" : "MUNICÍPIO DE RIO BRANCO");
        dto.adicionarParametro("ISCAMARA", isCamara);
        List<ItemDemonstrativoComponenteDTO> itensDtos = ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens);
        dto.adicionarParametro("itens", itensDtos);
        dto.adicionarParametro("USER", getMostraUsuario() ? usuarioCorrente.getNome() : "", false);
        dto.adicionarParametro("ANO_ATUAL", exercicio.getAno());
        dto.adicionarParametro("ANO-1", exercicioMenosUm.getAno());
        dto.adicionarParametro("ANO-2", exercicioMenosDois.getAno());
        dto.adicionarParametro("ANO-3", exercicioMenosTres.getAno());
        dto.adicionarParametro("ANO-4", exercicioMenosQuatro.getAno());
        dto.adicionarParametro("VARIACAO_1", exercicioMenosTres.getAno() + "/" + exercicioMenosQuatro.getAno());
        dto.adicionarParametro("VARIACAO_2", exercicioMenosDois.getAno() + "/" + exercicioMenosTres.getAno());
        dto.adicionarParametro("VARIACAO_3", exercicioMenosUm.getAno() + "/" + exercicioMenosDois.getAno());
        dto.adicionarParametro("VARIACAO_4", exercicio.getAno() + "/" + exercicioMenosUm.getAno());
        dto.adicionarParametro("exercicioCorrenteId", exercicio.getId());
        dto.adicionarParametro("exercicioMenosUmId", exercicioMenosUm.getId());
        dto.adicionarParametro("exercicioMenosDoisId", exercicioMenosDois.getId());
        dto.adicionarParametro("exercicioMenosTresId", exercicioMenosTres.getId());
        dto.adicionarParametro("exercicioMenosQuatroId", exercicioMenosQuatro.getId());
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio(getNomeArquivo());
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioEvolucaoDespesa";
    }

    @Override
    public String getApi() {
        return "contabil/evolucao-despesa/";
    }
}
