package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCapacitacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AndreGustavo on 15/10/2014.
 */
@Entity
@Audited
@Etiqueta("Capacitação")
public class Capacitacao extends EventoDeRH {
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Início")
    @Tabelavel
    @Pesquisavel
    private Date dataInicio;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Término")
    @Tabelavel
    @Pesquisavel
    private Date dataTermino;
    @Etiqueta("Quantidade de Dias")
    private Integer qtdDias;
    @Etiqueta("Quantidade de Vagas")
    private Integer qtdVagas;
    @Etiqueta("Carga Horária")
    private Integer cargaHoraria;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoCapacitacao situacao;
    @OneToMany(mappedBy = "capacitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuloCapacitacao> modulos;
    @OneToMany(mappedBy = "capacitacao", cascade = CascadeType.ALL)
    private List<InscricaoCapacitacao> inscricoes;
    @OneToMany(mappedBy = "capacitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CapacitacaoHabilidade> habilidades;
    @OneToMany(mappedBy = "capacitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetodoCientifCapacitacao> metodoCientifCapacitacaos;

    public Capacitacao() {
        situacao = SituacaoCapacitacao.EM_ANDAMENTO;
        modulos = new ArrayList<>();
        inscricoes = new ArrayList<>();
        habilidades = new ArrayList<>();
        metodoCientifCapacitacaos = new ArrayList<>();
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public List<ModuloCapacitacao> getModulos() {
        return modulos;
    }

    public void setModulos(List<ModuloCapacitacao> modulos) {
        this.modulos = modulos;
    }

    public List<InscricaoCapacitacao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(List<InscricaoCapacitacao> inscricoes) {
        this.inscricoes = inscricoes;
    }

    public List<CapacitacaoHabilidade> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<CapacitacaoHabilidade> habilidades) {
        this.habilidades = habilidades;
    }

    public List<MetodoCientifCapacitacao> getMetodoCientifCapacitacaos() {
        return metodoCientifCapacitacaos;
    }

    public void setMetodoCientifCapacitacaos(List<MetodoCientifCapacitacao> metodoCientifCapacitacaos) {
        this.metodoCientifCapacitacaos = metodoCientifCapacitacaos;
    }

    public SituacaoCapacitacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCapacitacao situacao) {
        this.situacao = situacao;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public Integer getQtdDias() {
        return qtdDias;
    }

    public void setQtdDias(Integer qtdDias) {
        this.qtdDias = qtdDias;
    }

    public Integer getQtdVagas() {
        return qtdVagas;
    }

    public void setQtdVagas(Integer qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataInicio) + " - " + getNome();
    }
}


