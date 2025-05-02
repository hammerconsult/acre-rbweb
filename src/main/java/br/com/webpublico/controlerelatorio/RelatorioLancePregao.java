/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ItemPregao;
import br.com.webpublico.entidades.Pregao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Sarruf
 */
@SessionScoped
@ManagedBean
public class RelatorioLancePregao implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private List<ItemPregao> itensRelatorio;
    private Pregao pregaoAtual;
    private boolean todosItensMarcados;

    public RelatorioLancePregao() {
    }

    public void exibirDialogGerarRelatorio(Pregao pregao) {
        marcarTodosItens(pregao);
        FacesUtil.executaJavaScript("dialogItensRelatorio.show()");
    }

    private void marcarTodosItens(Pregao pregao) {
        itensRelatorio = Lists.newArrayList();
        pregaoAtual = pregao;
        itensRelatorio.addAll(pregao.getListaDeItemPregao());
        setTodosItensMarcados(true);
    }

    public void gerarRelatorio(Pregao pregao) {
        if (pregao != null) {
            marcarTodosItens(pregao);
            gerarRelatorio("PDF");
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarItensSelecionados();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            String nomeRelatorio = "Relatório de Lances dos Fornecedores";
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("SECRETARIA", sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente().getDescricao());
            dto.adicionarParametro("idPregao", pregaoAtual.getId());
            dto.adicionarParametro("condicao", getCondicaoItensSelecionados());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/lance-pregao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.executaJavaScript("dialogItensRelatorio.hide()");
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarItensSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (itensRelatorio.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessario selecionar pelo menos um(1) item.");
        } else {
            boolean hasItemSelecionado = false;
            for (ItemPregao itemPregao : itensRelatorio) {
                if (itemPregao.isSelecionado()) {
                    hasItemSelecionado = true;
                }
            }
            if (!hasItemSelecionado) {
                ve.adicionarMensagemDeCampoObrigatorio("É necessario selecionar pelo menos um(1) item.");
            }
        }
        ve.lancarException();
    }

    private String getCondicaoItensSelecionados() {
        StringBuilder sql = new StringBuilder();
        sql.append("and itpre.id in (");
        for (ItemPregao itemPregao : itensRelatorio) {
            if (itemPregao.isSelecionado()) {
                sql.append(itemPregao.getId()).append(",");
            }
        }
        sql.replace(sql.lastIndexOf(","), sql.lastIndexOf(",") + 1, "");
        sql.append(")");
        return sql.toString();
    }

    public void setItemSelecionado(ItemPregao itemPregao, boolean selecionado) {
        itemPregao.setSelecionado(selecionado);
    }

    public List<ItemPregao> getItensRelatorio() {
        return itensRelatorio;
    }

    public void setItensRelatorio(List<ItemPregao> itensRelatorio) {
        this.itensRelatorio = itensRelatorio;
    }

    public boolean isTodosItensMarcados() {
        return todosItensMarcados;
    }

    public void setTodosItensMarcados(boolean todosItensMarcados) {
        this.todosItensMarcados = todosItensMarcados;
        for (ItemPregao itemPregao : itensRelatorio) {
            itemPregao.setSelecionado(todosItensMarcados);
        }
    }
}
