package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SessaoAtividade;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.RelatorioReceitaAbrasfFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.SessaoAtividadeDTO;
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
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioAbrasf", pattern = "/relatorioreceitaabrasf/novo/",
        viewId = "/faces/tributario/relatorioAbrasf/edita.xhtml")
})
public class RelatorioReceitaAbrasfControlador implements Serializable {
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private RelatorioReceitaAbrasfFacade relatorioFacade;
    private Mes mes;
    private String semana;
    private List<SessaoAtividade> sessoesSelecionadas;
    private List<SessaoAtividade> sessoes;
    private Exercicio exercicio;

    @URLAction(mappingId = "novoRelatorioAbrasf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        exercicio = relatorioFacade.buscarExercicioCorrente();
        sessoesSelecionadas = Lists.newArrayList();
        buscarSessoesAtividade();
    }

    public String getCaminhoPadrao() {
        return "/relatorioreceitaabrasf/";
    }

    public List<SelectItem> montarMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public void buscarSessoesAtividade() {
        sessoes = relatorioFacade.buscarSessoesAtividade();
    }

    public void gerarRelatorioPdf() {
        try {
            validarRelatorio();
            RelatorioDTO dto = instanciarRelatorioDto();
            dto.setApi("tributario/receita-abras/");
            ReportService.getInstance().gerarRelatorio(SistemaService.getInstance().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public StreamedContent gerarRelatorioExcel() {
        try {
            validarRelatorio();
            RelatorioDTO dto = instanciarRelatorioDto();
            dto.setApi("tributario/receita-abras/excel/");
            dto.adicionarParametro("NOME_ARQUIVO", getNomeRelatorioAoImprimir());
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xlsx", getNomeRelatorioAoImprimir() + ".xlsx");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private String getNomeRelatorioAoImprimir() {
        return "RECEITA_ABRASF";
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    private RelatorioDTO instanciarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        List<SessaoAtividadeDTO> sessoes = Lists.newArrayList();
        for (SessaoAtividade sessao : sessoesSelecionadas) {
            SessaoAtividadeDTO s = new SessaoAtividadeDTO();
            s.setId(sessao.getId());
            s.setDenominacao(sessao.getDenominacao());
            s.setSessao(sessao.getSessao());
            sessoes.add(s);
        }
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("sessoes", sessoes);
        dto.adicionarParametro("mes", mes.getToDto());
        dto.adicionarParametro("exercicio", exercicio.getAno());
        dto.adicionarParametro("dataOperacao", relatorioFacade.getDataOperacao());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.adicionarParametro("NOME_RELATORIO", "Relatório de Monitoramento Receita ABRASF");
        dto.setNomeRelatorio(getNomeRelatorioAoImprimir());
        dto.setNomeParametroBrasao("BRASAO");
        return dto;
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (exercicio != null && mes != null) {
            if (DataUtil.criarDataComMesEAno(mes.getNumeroMes(), exercicio.getAno()).toDate().after(new Date())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O mês e ano deve ser menor ou igual ao mês e ano atual.");
            }
        }
        ve.lancarException();
    }

    public void adicionarSessaoAtividade(SessaoAtividade sessao) {
        sessoesSelecionadas.add(sessao);
    }

    public void removerSessaoAtividade(SessaoAtividade sessao) {
        sessoesSelecionadas.remove(sessao);
    }

    public boolean containsSessao(SessaoAtividade sessao) {
        return sessoesSelecionadas.contains(sessao);
    }

    public boolean canAdicionarSessoes() {
        return sessoesSelecionadas != null && sessoesSelecionadas.size() == 4;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public String getSemana() {
        return semana;
    }

    public void setSemana(String semana) {
        this.semana = semana;
    }

    public List<SessaoAtividade> getSessoesSelecionadas() {
        return sessoesSelecionadas;
    }

    public void setSessoesSelecionadas(List<SessaoAtividade> sessoesSelecionadas) {
        this.sessoesSelecionadas = sessoesSelecionadas;
    }

    public List<SessaoAtividade> getSessoes() {
        return sessoes;
    }

    public void setSessoes(List<SessaoAtividade> sessoes) {
        this.sessoes = sessoes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
