package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hudson on 09/12/15.
 */

@Entity
@Audited
@GrupoDiagrama(nome = "Configuração Licitação")
@Etiqueta("Configurações Licitação")
public class ConfiguracaoLicitacao extends SuperEntidade implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Temporal(value = TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final de Vigência")
    @Temporal(value = TemporalType.DATE)
    private Date finalVigencia;

    @Etiqueta("Data Referência Reserva de Dotação")
    private Date dataReferenciaReservaDotacao;

    @Etiqueta("Configurações Processo de Compra")
    @Pesquisavel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoLicitacao", orphanRemoval = true)
    private List<ConfiguracaoProcessoCompra> configProcessoCompraUnidades;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoLicitacao", orphanRemoval = true)
    private List<UnidadeTercerializadaRh> unidadesTercerializadasRh;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoLicitacao", orphanRemoval = true)
    private List<UnidadeGrupoObjetoCompra> unidadesGrupoObjetoCompra;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoLicitacao", orphanRemoval = true)
    private List<ConfiguracaoReservaDotacao> configReservasDotacoes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoLicitacao", orphanRemoval = true)
    private List<ConfiguracaoAnexoLicitacao> configAnexosLicitacoes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoLicitacao", orphanRemoval = true)
    private List<ConfiguracaoSubstituicaoObjetoCompra> configSubstituicoesObjetoCompra;

    private String urlPncpServiceHomologacao;

    private String urlPncpServiceProducao;

    public ConfiguracaoLicitacao() {
        configProcessoCompraUnidades = new ArrayList<>();
        unidadesTercerializadasRh = new ArrayList<>();
        unidadesGrupoObjetoCompra = new ArrayList<>();
        configReservasDotacoes = Lists.newArrayList();
        configAnexosLicitacoes = Lists.newArrayList();
        configSubstituicoesObjetoCompra = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return null;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {

    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public List<ConfiguracaoProcessoCompra> getConfigProcessoCompraUnidades() {
        return configProcessoCompraUnidades;
    }

    public void setConfigProcessoCompraUnidades(List<ConfiguracaoProcessoCompra> configProcessoCompraUnidades) {
        this.configProcessoCompraUnidades = configProcessoCompraUnidades;
    }

    public List<UnidadeTercerializadaRh> getUnidadesTercerializadasRh() {
        return unidadesTercerializadasRh;
    }

    public void setUnidadesTercerializadasRh(List<UnidadeTercerializadaRh> unidadesTercerializadasRh) {
        this.unidadesTercerializadasRh = unidadesTercerializadasRh;
    }

    public List<UnidadeGrupoObjetoCompra> getUnidadesGrupoObjetoCompra() {
        return unidadesGrupoObjetoCompra;
    }

    public void setUnidadesGrupoObjetoCompra(List<UnidadeGrupoObjetoCompra> unidadesGrupoObjetoCompra) {
        this.unidadesGrupoObjetoCompra = unidadesGrupoObjetoCompra;
    }

    public List<ConfiguracaoReservaDotacao> getConfigReservasDotacoes() {
        return configReservasDotacoes;
    }

    public void setConfigReservasDotacoes(List<ConfiguracaoReservaDotacao> modalidades) {
        this.configReservasDotacoes = modalidades;
    }

    public List<ConfiguracaoAnexoLicitacao> getConfigAnexosLicitacoes() {
        return configAnexosLicitacoes;
    }

    public void setConfigAnexosLicitacoes(List<ConfiguracaoAnexoLicitacao> configAnexosLicitacoes) {
        this.configAnexosLicitacoes = configAnexosLicitacoes;
    }

    public Date getDataReferenciaReservaDotacao() {
        return dataReferenciaReservaDotacao;
    }

    public void setDataReferenciaReservaDotacao(Date dataReferencia) {
        this.dataReferenciaReservaDotacao = dataReferencia;
    }

    public String getUrlPncpServiceHomologacao() {
        return urlPncpServiceHomologacao;
    }

    public void setUrlPncpServiceHomologacao(String urlPncpServiceHomologacao) {
        this.urlPncpServiceHomologacao = urlPncpServiceHomologacao;
    }

    public String getUrlPncpServiceProducao() {
        return urlPncpServiceProducao;
    }

    public void setUrlPncpServiceProducao(String urlPncpServiceProducao) {
        this.urlPncpServiceProducao = urlPncpServiceProducao;
    }

    public List<ConfiguracaoSubstituicaoObjetoCompra> getConfigSubstituicoesObjetoCompra() {
        return configSubstituicoesObjetoCompra;
    }

    public void setConfigSubstituicoesObjetoCompra(List<ConfiguracaoSubstituicaoObjetoCompra> configSubstituicoesObjetoCompra) {
        this.configSubstituicoesObjetoCompra = configSubstituicoesObjetoCompra;
    }

    @Override
    public String toString() {
        return "Configuração de " + DataUtil.getDataFormatada(inicioVigencia);
    }
}
