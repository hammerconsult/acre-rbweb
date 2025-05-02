package br.com.webpublico.controle;

import br.com.webpublico.entidades.LevantamentoBemPatrimonial;
import br.com.webpublico.entidades.OrigemRecursoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.LevantamentoBensPatrimoniaisFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 26/05/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "correcao-levantamento-bem-movel", pattern = "/correcao-levantamento-bem-movel/", viewId = "/faces/administrativo/patrimonio/correcaolevantamentobemmovel/edita.xhtml"),
})
public class CorrecaoLevantamentoBemMovelControlador implements Serializable {

    @EJB
    private LevantamentoBensPatrimoniaisFacade facade;
    private LevantamentoBemPatrimonial levantamentoBemPatrimonial;
    private List<LevantamentoBemPatrimonial> lancamentos;
    private Integer quantidadeDeLancamentos;
    private String codigoInicial;
    private BigDecimal valorBem;

    @URLAction(mappingId = "correcao-levantamento-bem-movel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        levantamentoBemPatrimonial = null;
        lancamentos = Lists.newArrayList();
        quantidadeDeLancamentos = 0;
        codigoInicial = "0";
        valorBem = BigDecimal.ZERO;
    }

    public List<LevantamentoBemPatrimonial> completarLevantamentos(String filtro) {
        return facade.buscarLevantamentosPorCodigoPatrimonio(filtro.trim());
    }

    public void visualizarLevantamentoBemPatrimonial(LevantamentoBemPatrimonial levantamento) {
        FacesUtil.redirecionamentoInterno("/levantamento-de-bens-patrimoniais/ver/" + levantamento.getId());
    }

    public void corrigirLevantamento() {
        try {
            validarCampos();
            levantamentoBemPatrimonial.setValorBem(valorBem);
            levantamentoBemPatrimonial = facade.salvarLevantamento(levantamentoBemPatrimonial);
            criarProximosLancamentos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (levantamentoBemPatrimonial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Levantamento de Bem Móvel deve ser informado!");
        }
        if (quantidadeDeLancamentos == null || quantidadeDeLancamentos <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade de Lançamentos deve ser informado e deve ser maior que 0!");
        }
        if (codigoInicial == null || codigoInicial.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código Inicial deve ser informado!");
        }
        if (valorBem == null || valorBem.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor deve ser informado e deve ser maior que 0!");
        }
        ve.lancarException();
    }

    private String buscarProximoCodigo(String codigoAtual) {
        String proximoCodigo = "0" + (Integer.valueOf(codigoAtual) + 1);
        verificarExistenciaCodigo(proximoCodigo);
        return proximoCodigo;
    }

    private void verificarExistenciaCodigo(String codigo) {
        ValidacaoException ve = new ValidacaoException();
        LevantamentoBemPatrimonial levantamento = facade.buscarLevantamentoPorCodigoPatrimonio(codigo);
        if (levantamento != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um registro de levantamento de bem móvel com o número " + codigo + "!");
        }
        ve.lancarException();
    }

    private void criarProximosLancamentos() {
        try {
            verificarExistenciaCodigo(codigoInicial);
            Integer quantidadeRegistros;
            for (quantidadeRegistros = 0; quantidadeRegistros < quantidadeDeLancamentos; quantidadeRegistros++) {
                LevantamentoBemPatrimonial levantamentoNovo = (LevantamentoBemPatrimonial) Util.clonarObjeto(levantamentoBemPatrimonial);
                levantamentoNovo.setId(null);
                levantamentoNovo.setListaDeOriemRecursoBem(new ArrayList<OrigemRecursoBem>());
                for (OrigemRecursoBem origemRecursoBem : levantamentoBemPatrimonial.getListaDeOriemRecursoBem()) {
                    OrigemRecursoBem origemNovo = new OrigemRecursoBem();
                    origemNovo.setDescricao(origemRecursoBem.getDescricao());
                    origemNovo.setTipoRecursoAquisicaoBem(origemRecursoBem.getTipoRecursoAquisicaoBem());
                    origemNovo.setLevantamentoBemPatrimonial(levantamentoNovo);
                    levantamentoNovo.getListaDeOriemRecursoBem().add(origemNovo);
                }
                levantamentoNovo.setCodigoPatrimonio(codigoInicial);
                levantamentoNovo = facade.salvarLevantamento(levantamentoNovo);
                lancamentos.add(levantamentoNovo);
                codigoInicial = buscarProximoCodigo(codigoInicial);
            }
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addOperacaoRealizada("Novos registros gerados com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LevantamentoBemPatrimonial levantamentoBemPatrimonial : lancamentos) {
            total = total.add(levantamentoBemPatrimonial.getValorBem());
        }
        return total;
    }

    public Integer getQuantidadeDeRegistros() {
        return lancamentos.size();
    }

    public List<LevantamentoBemPatrimonial> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<LevantamentoBemPatrimonial> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public LevantamentoBemPatrimonial getLevantamentoBemPatrimonial() {
        return levantamentoBemPatrimonial;
    }

    public void setLevantamentoBemPatrimonial(LevantamentoBemPatrimonial levantamentoBemPatrimonial) {
        this.levantamentoBemPatrimonial = levantamentoBemPatrimonial;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public Integer getQuantidadeDeLancamentos() {
        return quantidadeDeLancamentos;
    }

    public void setQuantidadeDeLancamentos(Integer quantidadeDeLancamentos) {
        this.quantidadeDeLancamentos = quantidadeDeLancamentos;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }
}
