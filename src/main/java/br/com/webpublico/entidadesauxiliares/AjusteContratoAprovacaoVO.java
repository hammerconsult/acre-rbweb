package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.util.List;

public class AjusteContratoAprovacaoVO {

    private AjusteContratoDadosCadastrais ajusteDadosAlteracao;
    private AjusteContratoDadosCadastrais ajusteDadosOriginal;
    private Contrato contrato;
    private AlteracaoContratual alteracaoContratual;
    private List<ItemAjusteContratoDadosCadastraisVO> itensAjusteDadosCadastrais;
    private List<ItemContrato> itensContratoDeAprovadoParaEmElaboracao;

    public AjusteContratoAprovacaoVO() {
        itensAjusteDadosCadastrais = Lists.newArrayList();
        itensContratoDeAprovadoParaEmElaboracao = Lists.newArrayList();
    }

    public List<ItemContrato> getItensContratoDeAprovadoParaEmElaboracao() {
        return itensContratoDeAprovadoParaEmElaboracao;
    }

    public void setItensContratoDeAprovadoParaEmElaboracao(List<ItemContrato> itensContratoDeAprovadoParaEmElaboracao) {
        this.itensContratoDeAprovadoParaEmElaboracao = itensContratoDeAprovadoParaEmElaboracao;
    }

    public List<ItemAjusteContratoDadosCadastraisVO> getItensAjusteDadosCadastrais() {
        return itensAjusteDadosCadastrais;
    }

    public void setItensAjusteDadosCadastrais(List<ItemAjusteContratoDadosCadastraisVO> itensAjusteDadosCadastrais) {
        this.itensAjusteDadosCadastrais = itensAjusteDadosCadastrais;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public AjusteContratoDadosCadastrais getAjusteDadosAlteracao() {
        return ajusteDadosAlteracao;
    }

    public void setAjusteDadosAlteracao(AjusteContratoDadosCadastrais ajusteDadosAlteracao) {
        this.ajusteDadosAlteracao = ajusteDadosAlteracao;
    }

    public AjusteContratoDadosCadastrais getAjusteDadosOriginal() {
        return ajusteDadosOriginal;
    }

    public void setAjusteDadosOriginal(AjusteContratoDadosCadastrais ajusteDadosOriginal) {
        this.ajusteDadosOriginal = ajusteDadosOriginal;
    }

    public boolean hasAlteracaoObjeto(Object obj) {
        return Util.isNotNull(obj);
    }

    public boolean hasAlteracaoObjetoString(String obj) {
        return !Util.isStringNulaOuVazia(obj);
    }

    public boolean isAjusteAprovadoParaEmElaboracao() {
        return ajusteDadosOriginal != null
            && ajusteDadosOriginal.getSituacaoContrato().isAprovado()
            && ajusteDadosAlteracao != null
            && ajusteDadosAlteracao.getSituacaoContrato() != null
            && ajusteDadosAlteracao.getSituacaoContrato().isEmElaboracao();
    }
}
