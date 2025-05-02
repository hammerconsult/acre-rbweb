package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.SituacaoDiaria;
import br.com.webpublico.enums.TipoProposta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "exportacao-planilha-diarias", pattern = "/exportacao-planilha-diarias/", viewId = "/faces/financeiro/relatorio/exportacao-planilha-diarias.xhtml")
})
@ManagedBean
public class ExportacaoPlanilhaDiariasControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private SituacaoDiaria situacaoDiaria;
    private TipoProposta tipoProposta;

    @URLAction(mappingId = "exportacao-planilha-diarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        situacaoDiaria = null;
        tipoProposta = TipoProposta.CONCESSAO_DIARIA;
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoDiaria.values());
    }

    public List<SelectItem> getTiposDeProposta() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(TipoProposta.CONCESSAO_DIARIA, TipoProposta.CONCESSAO_DIARIA.getDescricao()));
        retorno.add(new SelectItem(TipoProposta.COLABORADOR_EVENTUAL, TipoProposta.COLABORADOR_EVENTUAL.getDescricao()));
        retorno.add(new SelectItem(TipoProposta.SUPRIMENTO_FUNDO, TipoProposta.SUPRIMENTO_FUNDO.getDescricao()));
        return retorno;
    }

    @Override
    public String getNomeRelatorio() {
        switch (tipoProposta) {
            case CONCESSAO_DIARIA:
                return "DEMONSTRATIVO-DA-CONCESSÃO-DE-ADIANTAMENTOS-DIÁRIAS-E-PASSAGENS";
            case SUPRIMENTO_FUNDO:
                return "DEMONSTRATIVO-DAS-CONCESSÕES-E-COMPROVAÇÕES-DE-ADIANTAMENTOS-SUPRIMENTOS-DE-FUNDOS";
            case COLABORADOR_EVENTUAL:
                return "DEMONSTRATIVO-DA-CONCESSÃO-DE-ADIANTAMENTOS-DIÁRIAS-E-PASSAGENS-COLABORADOR-EVENTUAL";
            default:
                return "";
        }
    }

    public StreamedContent gerarRelatorio() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(buscarParametros()));
            dto.adicionarParametro("dataInicial", dataInicial.getTime());
            dto.adicionarParametro("dataFinal", dataFinal.getTime());
            dto.adicionarParametro("tipoProposta", tipoProposta.getToDto());
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi(getApi());
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xlsx", getNomeRelatorio() + ".xlsx");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    public String getApi() {
        switch (tipoProposta) {
            case CONCESSAO_DIARIA:
                return "contabil/exportacao-planilha-diaria/excel/";
            case SUPRIMENTO_FUNDO:
                return "contabil/exportacao-planilha-suprimento-fundo/excel/";
            case COLABORADOR_EVENTUAL:
                return "contabil/exportacao-planilha-colaborador-eventual/excel/";
            default:
                return "";
        }
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    private List<ParametrosRelatorios> buscarParametros() {
        List<ParametrosRelatorios> parametrosRelatorios = Lists.newArrayList();
        filtrosParametrosUnidade(parametrosRelatorios);
        if (situacaoDiaria != null) {
            parametrosRelatorios.add(new ParametrosRelatorios(" prop.situacao ", ":situacao", null, OperacaoRelatorio.IGUAL, situacaoDiaria.name(), null, 1, false));
        }
        if (pessoa != null) {
            parametrosRelatorios.add(new ParametrosRelatorios(" prop.PESSOAFISICA_ID ", ":pessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
        }
        return parametrosRelatorios;
    }

    public TipoProposta getTipoProposta() {
        return tipoProposta;
    }

    public void setTipoProposta(TipoProposta tipoProposta) {
        this.tipoProposta = tipoProposta;
    }

    public SituacaoDiaria getSituacaoDiaria() {
        return situacaoDiaria;
    }

    public void setSituacaoDiaria(SituacaoDiaria situacaoDiaria) {
        this.situacaoDiaria = situacaoDiaria;
    }
}
