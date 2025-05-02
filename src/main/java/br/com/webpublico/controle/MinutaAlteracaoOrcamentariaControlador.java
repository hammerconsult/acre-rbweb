package br.com.webpublico.controle;

import br.com.webpublico.entidades.AlteracaoORC;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AlteracaoORCFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 25/02/14
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "minutaAlteracaoOrcamentariaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "minuta-alteracao-orcamentaria", pattern = "/minuta-alteracao-orcamentaria/", viewId = "/faces/financeiro/orcamentario/alteracaoorcamentaria/minuta.xhtml")
})

public class MinutaAlteracaoOrcamentariaControlador implements Serializable {

    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    private AlteracaoORC alteracaoORC;
    private String conteudoMinuta;


    @URLAction(mappingId = "minuta-alteracao-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void parametrosIniciais() {
    }

    public void limpar() {
        conteudoMinuta = null;
        alteracaoORC = null;
    }

    public List<AlteracaoORC> completaAlteraoOrc(String parte) {
        return alteracaoORCFacade.listaFiltrandoNoExercicio(parte.trim(), alteracaoORCFacade.getSistemaFacade().getExercicioCorrente());
    }

    public void buscarConteudoMinuta() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("idAlteracaoORC", alteracaoORC.getId());
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(alteracaoORCFacade.getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("anoExCorrente", alteracaoORCFacade.getSistemaFacade().getExercicioCorrente().getAno());
            dto.adicionarParametro("caminhoDaImagem", FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif");
            dto.setNomeRelatorio("Minuta da Alteração Orçamentária");
            dto.setApi("contabil/minuta-alteracao-orcamentaria/conteudo/");
            ConfiguracaoDeRelatorio configuracao = alteracaoORCFacade.getSistemaFacade().buscarConfiguracaoDeRelatorioPorChave();
            String urlWebreport = configuracao.getUrl() + dto.getApi();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ResponseEntity<String> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, String.class);
            conteudoMinuta = exchange.getBody();
        } catch (Exception ex) {
            FacesUtil.addError("Erro ao gerar a Minuta: ", ex.getMessage());
        }
    }

    public void gerarRelatorio() {
        try {
            if (conteudoMinuta != null) {
                RelatorioDTO dto = new RelatorioDTO();
                dto.adicionarParametro("conteudoMinuta", conteudoMinuta);
                dto.setNomeRelatorio("Minuta da Alteração Orçamentária");
                dto.setApi("contabil/minuta-alteracao-orcamentaria/");
                ReportService.getInstance().gerarRelatorio(alteracaoORCFacade.getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }


    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    public String getConteudoMinuta() {
        return conteudoMinuta;
    }

    public void setConteudoMinuta(String conteudoMinuta) {
        this.conteudoMinuta = conteudoMinuta;
    }
}
