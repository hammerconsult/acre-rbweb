package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AlteracaoContratual;
import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class CorrecaoDadosAdministrativoVO {

    private List<Bem> bens;
    private List<ItemDoctoItemEntradaVO> itensDocumentoEntrada;
    private TipoFuture tipoFuture;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Date dataOperacao;
    private UsuarioSistema usuarioSistema;
    private UnidadeOrganizacional unidadeOrganizacional;
    private AlteracaoContratual alteracaoContratual;
    private List<AlteracaoContratual> alteracoesContratuais;

    public CorrecaoDadosAdministrativoVO() {
        bens = Lists.newArrayList();
        itensDocumentoEntrada = Lists.newArrayList();
    }

    public List<ItemDoctoItemEntradaVO> getItensDocumentoEntrada() {
        return itensDocumentoEntrada;
    }

    public void setItensDocumentoEntrada(List<ItemDoctoItemEntradaVO> itensDocumentoEntrada) {
        this.itensDocumentoEntrada = itensDocumentoEntrada;
    }

    public List<Bem> getBens() {
        return bens;
    }

    public void setBens(List<Bem> bens) {
        this.bens = bens;
    }

    public TipoFuture getTipoFuture() {
        return tipoFuture;
    }

    public void setTipoFuture(TipoFuture tipoFuture) {
        this.tipoFuture = tipoFuture;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public List<AlteracaoContratual> getAlteracoesContratuais() {
        return alteracoesContratuais;
    }

    public void setAlteracoesContratuais(List<AlteracaoContratual> alteracoesContratuais) {
        this.alteracoesContratuais = alteracoesContratuais;
    }

    public enum TipoFuture {
        CONSULTA_BENS,
        REPROCESSAMENTO_ALTERACAO_CONTRATUAL,
        ITEM_DOCUMENTO_FISCAL;
    }
}
