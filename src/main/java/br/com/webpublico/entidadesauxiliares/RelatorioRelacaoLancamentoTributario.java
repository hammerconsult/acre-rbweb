package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Wellington on 17/11/2015.
 */
public class RelatorioRelacaoLancamentoTributario implements Serializable {

    public Long criadoEm;
    private RelacaoLancamentoTributario relacaoLancamentoTributario;
    private Date geradoEm;
    private Date finalizadoEm;
    private UsuarioSistema usuarioSistema;
    private AbstractFiltroRelacaoLancamentoDebito filtros;
    private AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario;
    private ByteArrayOutputStream dados;

    public RelatorioRelacaoLancamentoTributario() {
        this.criadoEm = System.nanoTime();
    }

    public RelatorioRelacaoLancamentoTributario(RelacaoLancamentoTributario relacaoLancamentoTributario, Date geradoEm, UsuarioSistema usuarioSistema,
                                                AbstractFiltroRelacaoLancamentoDebito filtros, AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario) {
        this.criadoEm = System.nanoTime();
        this.relacaoLancamentoTributario = relacaoLancamentoTributario;
        this.geradoEm = geradoEm;
        this.usuarioSistema = usuarioSistema;
        this.filtros = (AbstractFiltroRelacaoLancamentoDebito) Util.clonarObjeto(filtros);
        this.assistenteRelacaoLancamentoTributario = assistenteRelacaoLancamentoTributario;
    }

    public RelacaoLancamentoTributario getRelacaoLancamentoTributario() {
        return relacaoLancamentoTributario;
    }

    public void setRelacaoLancamentoTributario(RelacaoLancamentoTributario relacaoLancamentoTributario) {
        this.relacaoLancamentoTributario = relacaoLancamentoTributario;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }

    public Date getFinalizadoEm() {
        return finalizadoEm;
    }

    public void setFinalizadoEm(Date finalizadoEm) {
        this.finalizadoEm = finalizadoEm;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public AbstractFiltroRelacaoLancamentoDebito getFiltros() {
        return filtros;
    }

    public void setFiltros(AbstractFiltroRelacaoLancamentoDebito filtros) {
        this.filtros = filtros;
    }

    public AssistenteRelacaoLancamentoTributario getAssistenteRelacaoLancamentoTributario() {
        return assistenteRelacaoLancamentoTributario;
    }

    public void setAssistenteRelacaoLancamentoTributario(AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario) {
        this.assistenteRelacaoLancamentoTributario = assistenteRelacaoLancamentoTributario;
    }

    public ByteArrayOutputStream getDados() {
        return dados;
    }

    public void setDados(ByteArrayOutputStream dados) {
        this.dados = dados;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatorioRelacaoLancamentoTributario that = (RelatorioRelacaoLancamentoTributario) o;

        return criadoEm.equals(that.criadoEm);

    }

    @Override
    public int hashCode() {
        return criadoEm.hashCode();
    }

    public enum RelacaoLancamentoTributario {
        IPTU("I.P.T.U"), ISSQN("I.S.S.Q.N"), DIVIDA_ATIVA("DÍVIDA ATIVA"), ALVARA("ALVARÁ"), CEASA("CEASA"),
        RENDAS_PATRIMONIAIS("RENDAS PATRIMONIAIS"), NOTA_FISCAL_AVULA("NOTA FISCAL AVULSA"), ITBI("I.T.B.I"),
        FISCALIZACAO_ISSQN("FISCALIZAÇÃO DE I.S.S.Q.N"), PARCELAMENTO("PARCELAMENTO"), DIVIDAS_DIVERSAS("DÍVIDAS DIVERSAS"),
        TAXAS_DIVERSAS("TAXAS DIVERSAS"), FISCALIZACAO_SECRETARIAS("FISCALIZAÇÃO DE SECRETARIAS"), PROCESSO_DEBITO("PROCESSO DE DÉBITO"),
        DEBITOS_A_PRESCREVER("DÉBITOS À PRESCREVER");

        private String descricao;

        RelacaoLancamentoTributario(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
