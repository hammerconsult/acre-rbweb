package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioDetalhamentoIptu;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LogradouroFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "relatorioDetalhamentoIptuControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-rol-iptu", pattern = "/relatorio/rol-iptu/", viewId = "/faces/tributario/iptu/relatorio/relatoriodetalhamentoiptu.xhtml")
})
public class RelatorioDetalhamentoIptuControlador implements Serializable {

    private FiltroRelatorioDetalhamentoIptu filtroRelatorio;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;

    public RelatorioDetalhamentoIptuControlador() {
    }

    @URLAction(mappingId = "novo-rol-iptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        filtroRelatorio = new FiltroRelatorioDetalhamentoIptu(sistemaFacade.getExercicioCorrente());
    }


    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getCadastroInicial() == null || filtroRelatorio.getCadastroInicial().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro inicial.");
        }
        if (filtroRelatorio.getCadastroFinal() == null || filtroRelatorio.getCadastroFinal().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro final.");
        }
        if (filtroRelatorio.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício.");
        }
        if (filtroRelatorio.getValorInicial() == null || filtroRelatorio.getValorInicial().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o valor inicial, deve ser maior que zero.");
        }
        if (filtroRelatorio.getValorFinal() == null || filtroRelatorio.getValorFinal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o valor final, deve ser maior que zero e maior que o valor inicial.");
        }
        if (filtroRelatorio.getValorInicial() != null && filtroRelatorio.getValorInicial().compareTo(BigDecimal.ZERO) >= 0 &&
            filtroRelatorio.getValorFinal() != null && filtroRelatorio.getValorFinal().compareTo(BigDecimal.ZERO) >= 0 &&
            filtroRelatorio.getValorInicial().compareTo(filtroRelatorio.getValorFinal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Se informado o valor inicial deve ser menor que o valor final.");
        }
        ve.lancarException();
    }

    private String getWhere(FiltroRelatorioDetalhamentoIptu filtroRelatorio) {
        StringBuilder where = new StringBuilder();
        filtroRelatorio.setFiltrosUtilizados(new StringBuilder());

        if (filtroRelatorio.getProcessoCalculoIPTU() != null) {
            where.append(" and processocalculo_id = ");
            where.append(filtroRelatorio.getProcessoCalculoIPTU().getId());

            filtroRelatorio.getFiltrosUtilizados().append(" Processo de Calculo: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getProcessoCalculoIPTU().getDescricao());
        }
        if (filtroRelatorio.getSetorInicial() != null && !filtroRelatorio.getSetorInicial().isEmpty()) {
            where.append(" and setor.codigo >= ");
            where.append(filtroRelatorio.getSetorInicial());

            filtroRelatorio.getFiltrosUtilizados().append(" Setor >= ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getSetorInicial());
        }

        if (filtroRelatorio.getSetorFinal() != null && !filtroRelatorio.getSetorFinal().isEmpty()) {
            where.append(" and setor.codigo <= ");
            where.append(filtroRelatorio.getSetorFinal());

            filtroRelatorio.getFiltrosUtilizados().append(" Setor <= ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getSetorInicial());
        }

        if (filtroRelatorio.getQuadra() != null) {
            where.append(" and lote.quadra_id = ");
            where.append(filtroRelatorio.getQuadra().getId());

            filtroRelatorio.getFiltrosUtilizados().append(" Quadra: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getQuadra().getDescricao());
        }

        if (filtroRelatorio.getBairro() != null) {
            where.append(" and bairro.id = ");
            where.append(filtroRelatorio.getBairro().getId());

            filtroRelatorio.getFiltrosUtilizados().append(" Bairro: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getBairro().getDescricao());
        }

        if (filtroRelatorio.getLogradouro() != null) {
            where.append(" and logradouro.id = ");
            where.append(filtroRelatorio.getLogradouro().getId());

            filtroRelatorio.getFiltrosUtilizados().append(" Logradouro: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getLogradouro().getNome());
        }

        if (filtroRelatorio.getTipoImovel() != null) {
            if (TipoImovel.PREDIAL.equals(filtroRelatorio.getTipoImovel())) {
                where.append(" and exists (select 1 from memoriacalculoiptu m where m.calculoiptu_id = calculoiptu.id and m.construcao_id is not null) ");
            } else {
                where.append(" and exists (select 1 from memoriacalculoiptu m where m.calculoiptu_id = calculoiptu.id and m.construcao_id is null) ");
            }

            filtroRelatorio.getFiltrosUtilizados().append(" Tipo de Imóvel: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getTipoImovel().getDescricao());
        }

        if (!filtroRelatorio.getCadastroInicial().trim().isEmpty()) {
            where.append(" and inscricaocadastral >= '");
            where.append(filtroRelatorio.getCadastroInicial());
            where.append("' ");

            filtroRelatorio.getFiltrosUtilizados().append(" Cadastro Inicial: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getCadastroInicial());
        }

        if (!filtroRelatorio.getCadastroFinal().trim().isEmpty()) {
            where.append(" and inscricaocadastral <= '");
            where.append(filtroRelatorio.getCadastroFinal());
            where.append("' ");

            filtroRelatorio.getFiltrosUtilizados().append(" Cadastro Final: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getCadastroFinal());
        }
        if (filtroRelatorio.getExercicio() != null) {
            where.append(" and processocalculo.exercicio_id = ");
            where.append(filtroRelatorio.getExercicio().getId());

            filtroRelatorio.getFiltrosUtilizados().append(" Exercício: ");
            filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getExercicio().getAno());
        }

        where.append(" and calculo.valorefetivo between " + filtroRelatorio.getValorInicial() + " and " + filtroRelatorio.getValorFinal() + " ");
        filtroRelatorio.getFiltrosUtilizados().append(" Valor Inicial: ");
        filtroRelatorio.getFiltrosUtilizados().append(Util.reaisToString(filtroRelatorio.getValorInicial()));
        filtroRelatorio.getFiltrosUtilizados().append(" Valor Final: ");
        filtroRelatorio.getFiltrosUtilizados().append(Util.reaisToString(filtroRelatorio.getValorFinal()));

        filtroRelatorio.getFiltrosUtilizados().append(" Somente Eftivados: ");
        filtroRelatorio.getFiltrosUtilizados().append(filtroRelatorio.getSomenteEfetivados() ? " Sim " : " Não ");

        return where.toString();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "TRIBUTÁRIO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.setNomeRelatorio("ROL. DE I.P.T.U.");
            dto.adicionarParametro("NOMERELATORIO", "ROL. DE I.P.T.U.");
            dto.adicionarParametro("WHERE", getWhere(filtroRelatorio));
            dto.adicionarParametro("ORDERBY", " order by inscricaocadastral ");
            dto.adicionarParametro("FILTROS", filtroRelatorio.getFiltrosUtilizados().toString());
            dto.adicionarParametro("DESCONTO", filtroRelatorio.getConsideraDesconto());
            dto.adicionarParametro("VALORINICIAL", filtroRelatorio.getValorInicial());
            dto.adicionarParametro("VALORFINAL", filtroRelatorio.getValorFinal());
            dto.adicionarParametro("SOMENTEEFETIVADOS", filtroRelatorio.getSomenteEfetivados() != null ? filtroRelatorio.getSomenteEfetivados() : false);
            dto.setApi("tributario/rol-iptu/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public FiltroRelatorioDetalhamentoIptu getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioDetalhamentoIptu filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }

    public void processarSelecaoDeBairro() {
        filtroRelatorio.setLogradouro(null);
    }

    public List<Logradouro> completarLogradouros(String parte) {
        if (filtroRelatorio.getBairro() != null) {
            return logradouroFacade.completaLogradourosPorBairro(parte, filtroRelatorio.getBairro());
        } else {
            return logradouroFacade.listaLogradourosAtivos(parte);
        }
    }

    public List<SelectItem> tiposDeImoveis() {
        return Util.getListSelectItem(TipoImovel.values());
    }

    public void limparProcesso(FiltroRelatorioDetalhamentoIptu filtroRelatorio) {
        filtroRelatorio.setProcessoCalculoIPTU(null);
    }

    public void limparBairro(FiltroRelatorioDetalhamentoIptu filtroRelatorio) {
        filtroRelatorio.setBairro(null);
    }

    public void limparLogradouro(FiltroRelatorioDetalhamentoIptu filtroRelatorio) {
        filtroRelatorio.setLogradouro(null);
    }
}
