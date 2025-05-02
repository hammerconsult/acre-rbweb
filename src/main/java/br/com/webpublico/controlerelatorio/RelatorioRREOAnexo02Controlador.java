/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.ConfiguracaoAnexosRREO;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.ConfiguracaoAnexosRREOFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo2", pattern = "/relatorio/rreo/anexo2/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo02.xhtml")})
public class RelatorioRREOAnexo02Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private ConfiguracaoAnexosRREOFacade configuracaoAnexosRREOFacade;
    private ConfiguracaoAnexosRREO configuracaoAnexosRREO;

    public RelatorioRREOAnexo02Controlador() {
        super();
    }

    public List<SelectItem> getBimestres() {
        return Util.getListSelectItemSemCampoVazio(BimestreAnexosLei.values(), false);
    }

    @URLAction(mappingId = "relatorio-rreo-anexo2", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO2_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        configuracaoAnexosRREO = null;
        super.limparCampos();
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), exercicio.getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setRelatorio(getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", exercicio, TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("BIMESTRE_FINAL", bimestre.isUltimoBimestre());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("CONFIGURACAO", configuracaoAnexosRREO.toDto());
        adicionarItemDemonstrativoFiltrosCampoACampo(dto);
        dto.setNomeRelatorio("RELATÓRIO-RREO-ANEXO-02");
    }

    public void validarConfiguracao() {
        ValidacaoException ve = new ValidacaoException();
        configuracaoAnexosRREO = configuracaoAnexosRREOFacade.buscarConfiguracaoPorExercicio(exercicio);
        if (configuracaoAnexosRREO == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe configuração de relatório para o exercício de " + exercicio.getAno() +
                ". <a href='" + FacesUtil.getRequestContextPath() + "/configuracao-anexos-rreo/listar/' target='_blank'>Clique aqui para configurar.</a>");
        }
        ve.lancarException();
        if (configuracaoAnexosRREO.getLinhaTotalIntraOrcamentario() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número da linha DESPESAS (INTRA-ORÇAMENTÁRIAS) (II) não foi informado na configuração." + exercicio.getAno() +
                ". <a href='" + FacesUtil.getRequestContextPath() + "/configuracao-anexos-rreo/editar/" + configuracaoAnexosRREO.getId() + "' target='_blank'>Clique aqui para configurar.</a>");
        }
        if (configuracaoAnexosRREO.getLinhaTotalGeral() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número da linha TOTAL (III) = (I + II) não foi informado na configuração." + exercicio.getAno() +
                ". <a href='" + FacesUtil.getRequestContextPath() + "/configuracao-anexos-rreo/editar/" + configuracaoAnexosRREO.getId() + "' target='_blank'>Clique aqui para configurar.</a>");
        }
        ve.lancarException();
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        validarConfiguracao();
        instanciarItemDemonstrativoFiltros();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo02";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo2/";
    }

    public BimestreAnexosLei getBimestre() {
        return bimestre;
    }

    public void setBimestre(BimestreAnexosLei bimestre) {
        this.bimestre = bimestre;
    }
}
