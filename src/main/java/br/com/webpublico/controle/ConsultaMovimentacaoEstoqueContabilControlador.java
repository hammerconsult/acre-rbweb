package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroConsultaMovimentacaoEstoqueContabil;
import br.com.webpublico.entidadesauxiliares.MovimentacaoGrupoMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ConsultaMovimentacaoEstoqueContabilFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "mov-est-cont", pattern = "/consulta-movimentacao-estoque/contabil/", viewId = "/faces/administrativo/materiais/consulta-mov-estoque-contabil/edita.xhtml")
})
public class ConsultaMovimentacaoEstoqueContabilControlador implements Serializable {

    @EJB
    private ConsultaMovimentacaoEstoqueContabilFacade facade;
    private FiltroConsultaMovimentacaoEstoqueContabil filtro;
    private MovimentacaoGrupoMaterial movimentoSelecionado;
    private List<MovimentacaoGrupoMaterial> movimentacoes;
    private List<MovimentacaoGrupoMaterial> movimentacoesList;

    @URLAction(mappingId = "mov-est-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsulta() {
        filtro = new FiltroConsultaMovimentacaoEstoqueContabil();
        filtro.setDataFinal(facade.getSistemaFacade().getDataOperacao());
        filtro.setDataInicial(getDataInicial());
        movimentacoes = Lists.newArrayList();
        movimentacoesList = Lists.newArrayList();
    }

    private Date getDataInicial() {
        Integer ano = DataUtil.getAno(filtro.getDataFinal());
        return DataUtil.criarDataComMesEAno(1, ano).toDate();
    }

    public void gerarComparativo() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Comparativo por Grupo Contábil/Estoque");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("USER", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("movimentos", MovimentacaoGrupoMaterial.getMovimentosToDto(movimentacoes));
            dto.setApi("administrativo/comparativo-movimentacao-estoque-contabil/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void consultar() {
        try {
            validarConsulta();
            movimentacoes = facade.consultar(filtro);
            movimentacoesList = Lists.newArrayList(movimentacoes);
            aplicarFiltro();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void aplicarFiltro() {
        movimentacoes = Lists.newArrayList(movimentacoesList);
        if (hasMovimentacao()) {
            movimentacoes.removeIf(mov -> filtro.getSomenteDiferencaAConciliar() && mov.isGrupoConciliado());
            movimentacoes.removeIf(mov -> filtro.getSomenteDiferencaEstoque() && mov.isGrupoSemDiferencaEstoqueCalculado());
            movimentacoes.removeIf(mov -> filtro.getSomenteGrupoComInversaoSaldo() && mov.getSaldoAtualGrupoContabil().compareTo(BigDecimal.ZERO) < 0);
        }
    }

    private void validarConsulta() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (filtro.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        if (filtro.getHierarquiaOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária deve ser informado.");
        }
        if (filtro.getDataInicial() != null
            && filtro.getDataFinal() != null
            && DataUtil.verificaDataFinalMaiorQueDataInicial(filtro.getDataInicial(), filtro.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de inicio deve ser anterior a data final.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposMovimentosGrupo() {
        return Util.getListSelectItemSemCampoVazio(FiltroConsultaMovimentacaoEstoqueContabil.TipoMovimento.values(), false);
    }

    public void selecionarMovimento(MovimentacaoGrupoMaterial mov) {
        movimentoSelecionado = mov;
    }

    public boolean hasMovimentacao() {
        return movimentacoes != null && !movimentacoes.isEmpty();
    }

    public BigDecimal getTotalGeralEntradaEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalEntradaEstoque());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralSaidaEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalSaidaEstoque());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralSaldoEstoqueAtual() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getSaldoEstoqueAtual());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralSaldoEstoqueAnteiror() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getSaldoEstoqueAnterior());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralDiferencaEntradaSaida() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalEstoqueCalculado());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralDiferencaEntradaSaidaComEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getDiferencaEntradaSaidaComEstoque());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralEntradaContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalEntradaContabil());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralSaidaContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalSaidaContabil());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralSaldoAtualGrupoContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getSaldoAtualGrupoContabil());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralSaldoAnteriorGrupoContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getSaldoAnteriorGrupoContabil());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralDiferencaConciliacao() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalDiferencaConciliacao());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralDiferencaEntrada() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalDiferencaEntrada());
            }
        }
        return total;
    }

    public BigDecimal getTotalGeralDiferencaSaida() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoGrupoMaterial mov : movimentacoes) {
                total = total.add(mov.getTotalDiferencaSaida());
            }
        }
        return total;
    }

    public String getValorFormatado(BigDecimal valor) {
        return Util.formataValorSemCifrao(valor.abs());
    }

    public String getSiglaCreditoDebito(BigDecimal valor) {
        return valor.compareTo(BigDecimal.ZERO) >= 0 ? "C" : "D";
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaUsuarioPorTipoData(
            parte.trim(),
            facade.getSistemaFacade().getUsuarioCorrente(),
            facade.getSistemaFacade().getDataOperacao(),
            TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<GrupoMaterial> completarGrupoMaterial(String parte) {
        return facade.getGrupoMaterialFacade().buscarGruposMateriaisNivelAnalitico(parte.trim());
    }

    public FiltroConsultaMovimentacaoEstoqueContabil getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        this.filtro = filtro;
    }

    public List<MovimentacaoGrupoMaterial> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoGrupoMaterial> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public MovimentacaoGrupoMaterial getMovimentoSelecionado() {
        return movimentoSelecionado;
    }

    public void setMovimentoSelecionado(MovimentacaoGrupoMaterial movimentoSelecionado) {
        this.movimentoSelecionado = movimentoSelecionado;
    }
}
