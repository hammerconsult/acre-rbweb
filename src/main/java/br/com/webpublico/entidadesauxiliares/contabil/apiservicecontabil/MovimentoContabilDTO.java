package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.ParametroEventoDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoExtraorcamentarioDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoSubContaDTO;
import com.google.common.collect.Lists;

import java.util.List;

public class MovimentoContabilDTO {
    private List<ParametroEventoDTO> contabilizar;
    private List<SaldoExtraorcamentarioDTO> saldoExtraorcamentario;
    private List<SaldoFonteDespesaORCDTO> saldoOrcamentario;
    private List<SaldoSubContaDTO> saldoFinanceiro;
    private List<String> mensagensErro;
    public MovimentoContabilDTO() {
        this.contabilizar = Lists.newArrayList();
        this.saldoExtraorcamentario = Lists.newArrayList();
        this.saldoOrcamentario = Lists.newArrayList();
        this.saldoFinanceiro = Lists.newArrayList();
        this.mensagensErro = Lists.newArrayList();
    }

    public List<ParametroEventoDTO> getContabilizar() {
        return contabilizar;
    }

    public void setContabilizar(List<ParametroEventoDTO> contabilizar) {
        this.contabilizar = contabilizar;
    }

    public List<SaldoExtraorcamentarioDTO> getSaldoExtraorcamentario() {
        return saldoExtraorcamentario;
    }

    public void setSaldoExtraorcamentario(List<SaldoExtraorcamentarioDTO> saldoExtraorcamentario) {
        this.saldoExtraorcamentario = saldoExtraorcamentario;
    }

    public List<SaldoFonteDespesaORCDTO> getSaldoOrcamentario() {
        return saldoOrcamentario;
    }

    public void setSaldoOrcamentario(List<SaldoFonteDespesaORCDTO> saldoOrcamentario) {
        this.saldoOrcamentario = saldoOrcamentario;
    }

    public List<SaldoSubContaDTO> getSaldoFinanceiro() {
        return saldoFinanceiro;
    }

    public void setSaldoFinanceiro(List<SaldoSubContaDTO> saldoFinanceiro) {
        this.saldoFinanceiro = saldoFinanceiro;
    }

    public List<String> getMensagensErro() {
        return mensagensErro;
    }

    public void setMensagensErro(List<String> mensagensErro) {
        this.mensagensErro = mensagensErro;
    }
}
