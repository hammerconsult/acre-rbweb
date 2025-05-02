package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.TransporteConfiguracaoCLPLCP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 26/12/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Entity
@Etiqueta("Transporte Configuração Contábil")
public class TransporteConfiguracaoContabil extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Transporte")
    private Date dataTransporte;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício de Destino")
    private Exercicio exercicioDestino;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoCLPLCP> transporteConfiguracoesCLPLCPs;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoOCC> transporteConfiguracoesOCCs;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoCDE> transporteConfiguracoesCDEs;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoCLP> transporteConfiguracoesCLPs;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoLCP> transporteConfiguracoesLCPs;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoGrupoPatrimonial> transporteConfiguracoesGruposPatrimoniais;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoGrupoMaterial> transporteConfiguracoesGruposMateriais;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteTipoConfiguracaoContabil> tiposConfiguracoesContabeis;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoTipoObjetoCompra> transporteConfiguracoesTiposObjetoCompra;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoTipoViagem> transporteConfiguracoesTipoViagem;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteConfiguracaoTipoPessoa> transporteConfiguracoesTipoPessoa;

    @OneToMany(mappedBy = "transporteConfigContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteGrupoOrcamentario> transporteGruposOrcamentarios;

    public TransporteConfiguracaoContabil() {
        limparConfiguracoes();
    }

    public void limparConfiguracoes() {
        transporteConfiguracoesOCCs = Lists.newArrayList();
        transporteConfiguracoesCDEs = Lists.newArrayList();
        transporteConfiguracoesCLPs = Lists.newArrayList();
        transporteConfiguracoesLCPs = Lists.newArrayList();
        transporteConfiguracoesGruposPatrimoniais = Lists.newArrayList();
        transporteConfiguracoesGruposMateriais = Lists.newArrayList();
        transporteConfiguracoesTipoViagem = Lists.newArrayList();
        transporteConfiguracoesTipoPessoa = Lists.newArrayList();
        transporteConfiguracoesTiposObjetoCompra = Lists.newArrayList();
        transporteConfiguracoesCLPLCPs = Lists.newArrayList();
        transporteGruposOrcamentarios = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataTransporte() {
        return dataTransporte;
    }

    public void setDataTransporte(Date dataTransporte) {
        this.dataTransporte = dataTransporte;
    }

    public Exercicio getExercicioDestino() {
        return exercicioDestino;
    }

    public void setExercicioDestino(Exercicio exercicioDestino) {
        this.exercicioDestino = exercicioDestino;
    }

    public List<TransporteConfiguracaoOCC> getTransporteConfiguracoesOCCs() {
        return transporteConfiguracoesOCCs;
    }

    public void setTransporteConfiguracoesOCCs(List<TransporteConfiguracaoOCC> transporteConfiguracoesOCCs) {
        this.transporteConfiguracoesOCCs = transporteConfiguracoesOCCs;
    }

    public List<TransporteConfiguracaoCDE> getTransporteConfiguracoesCDEs() {
        return transporteConfiguracoesCDEs;
    }

    public void setTransporteConfiguracoesCDEs(List<TransporteConfiguracaoCDE> transporteConfiguracoesCDEs) {
        this.transporteConfiguracoesCDEs = transporteConfiguracoesCDEs;
    }

    public List<TransporteConfiguracaoCLP> getTransporteConfiguracoesCLPs() {
        return transporteConfiguracoesCLPs;
    }

    public void setTransporteConfiguracoesCLPs(List<TransporteConfiguracaoCLP> transporteConfiguracoesCLPs) {
        this.transporteConfiguracoesCLPs = transporteConfiguracoesCLPs;
    }

    public List<TransporteConfiguracaoLCP> getTransporteConfiguracoesLCPs() {
        return transporteConfiguracoesLCPs;
    }

    public void setTransporteConfiguracoesLCPs(List<TransporteConfiguracaoLCP> transporteConfiguracoesLCPs) {
        this.transporteConfiguracoesLCPs = transporteConfiguracoesLCPs;
    }

    public List<TransporteConfiguracaoGrupoPatrimonial> getTransporteConfiguracoesGruposPatrimoniais() {
        return transporteConfiguracoesGruposPatrimoniais;
    }

    public void setTransporteConfiguracoesGruposPatrimoniais(List<TransporteConfiguracaoGrupoPatrimonial> transporteConfiguracoesGruposPatrimoniais) {
        this.transporteConfiguracoesGruposPatrimoniais = transporteConfiguracoesGruposPatrimoniais;
    }

    public List<TransporteConfiguracaoGrupoMaterial> getTransporteConfiguracoesGruposMateriais() {
        return transporteConfiguracoesGruposMateriais;
    }

    public void setTransporteConfiguracoesGruposMateriais(List<TransporteConfiguracaoGrupoMaterial> transporteConfiguracoesGruposMateriais) {
        this.transporteConfiguracoesGruposMateriais = transporteConfiguracoesGruposMateriais;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<TransporteTipoConfiguracaoContabil> getTiposConfiguracoesContabeis() {
        return tiposConfiguracoesContabeis;
    }

    public void setTiposConfiguracoesContabeis(List<TransporteTipoConfiguracaoContabil> tiposConfiguracoesContabeis) {
        this.tiposConfiguracoesContabeis = tiposConfiguracoesContabeis;
    }

    public List<TransporteConfiguracaoTipoViagem> getTransporteConfiguracoesTipoViagem() {
        return transporteConfiguracoesTipoViagem;
    }

    public void setTransporteConfiguracoesTipoViagem(List<TransporteConfiguracaoTipoViagem> transporteConfiguracoesTipoViagem) {
        this.transporteConfiguracoesTipoViagem = transporteConfiguracoesTipoViagem;
    }

    public List<TransporteConfiguracaoTipoPessoa> getTransporteConfiguracoesTipoPessoa() {
        return transporteConfiguracoesTipoPessoa;
    }

    public void setTransporteConfiguracoesTipoPessoa(List<TransporteConfiguracaoTipoPessoa> transporteConfiguracoesTipoPessoa) {
        this.transporteConfiguracoesTipoPessoa = transporteConfiguracoesTipoPessoa;
    }

    public List<TransporteConfiguracaoTipoObjetoCompra> getTransporteConfiguracoesTiposObjetoCompra() {
        return transporteConfiguracoesTiposObjetoCompra;
    }

    public void setTransporteConfiguracoesTiposObjetoCompra(List<TransporteConfiguracaoTipoObjetoCompra> transporteConfiguracoesTiposObjetoCompra) {
        this.transporteConfiguracoesTiposObjetoCompra = transporteConfiguracoesTiposObjetoCompra;
    }

    public List<TransporteConfiguracaoCLPLCP> getTransporteConfiguracoesCLPLCPs() {
        return transporteConfiguracoesCLPLCPs;
    }

    public void setTransporteConfiguracoesCLPLCPs(List<TransporteConfiguracaoCLPLCP> transporteConfiguracoesCLPLCPs) {
        this.transporteConfiguracoesCLPLCPs = transporteConfiguracoesCLPLCPs;
    }

    public List<TransporteGrupoOrcamentario> getTransporteGruposOrcamentarios() {
        return transporteGruposOrcamentarios;
    }

    public void setTransporteGruposOrcamentarios(List<TransporteGrupoOrcamentario> transporteGruposOrcamentarios) {
        this.transporteGruposOrcamentarios = transporteGruposOrcamentarios;
    }

    @Override
    public String toString() {
        try {
            return "de: " + Util.getAnoDaData(this.dataTransporte) + " para: " + exercicioDestino.getAno();
        } catch (Exception e) {
            return "";
        }
    }
}
