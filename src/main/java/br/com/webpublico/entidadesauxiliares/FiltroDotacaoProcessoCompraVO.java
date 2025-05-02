/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.SolicitacaoMaterial;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.enums.TipoOperacaoORC;

import java.io.Serializable;

public class FiltroDotacaoProcessoCompraVO implements Serializable {

    private SolicitacaoMaterial solicitacaoMaterial;
    private FonteDespesaORC fonteDespesaORC;
    private Exercicio exercicio;
    private UnidadeOrganizacional unidadeOrganizacional;
    private TipoObjetoCompra tipoObjetoCompra;
    private TipoOperacaoORC tipoOperacao;
    private Boolean execucaoContratoOrSemContrato;

    public FiltroDotacaoProcessoCompraVO() {
        execucaoContratoOrSemContrato = true;
    }

    public TipoOperacaoORC getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoORC tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public Boolean getExecucaoContratoOrSemContrato() {
        return execucaoContratoOrSemContrato;
    }

    public void setExecucaoContratoOrSemContrato(Boolean execucaoContratoOrSemContrato) {
        this.execucaoContratoOrSemContrato = execucaoContratoOrSemContrato;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }
}
