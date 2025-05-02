package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;


import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Nacionalidade;
import br.com.webpublico.pessoa.dto.CertidaoCasamentoDTO;
import br.com.webpublico.util.DataUtil;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class CertidaoCasamentoPortal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nomeConjuge;
    @Transient
    private String cpfConjuge;
    private String nomeCartorio;
    private String numeroLivro;
    private Integer numeroFolha;
    private Integer numeroRegistro;
    @ManyToOne
    private Nacionalidade nacionalidade;
    private String estado;
    private String localTrabalhoConjuge;
    private Date dataCasamento;
    private Date dataNascimentoConjuge;
    @ManyToOne
    private Cidade cidadeCartorio;
    @ManyToOne
    private Cidade naturalidadeConjuge;
    private Date dataAverbacao;

    public CertidaoCasamentoPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeConjuge() {
        return nomeConjuge;
    }

    public void setNomeConjuge(String nomeConjuge) {
        this.nomeConjuge = nomeConjuge;
    }

    public String getCpfConjuge() {
        return cpfConjuge;
    }

    public void setCpfConjuge(String cpfConjuge) {
        this.cpfConjuge = cpfConjuge;
    }

    public String getNomeCartorio() {
        return nomeCartorio;
    }

    public void setNomeCartorio(String nomeCartorio) {
        this.nomeCartorio = nomeCartorio;
    }

    public String getNumeroLivro() {
        return numeroLivro;
    }

    public void setNumeroLivro(String numeroLivro) {
        this.numeroLivro = numeroLivro;
    }

    public Integer getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(Integer numeroFolha) {
        this.numeroFolha = numeroFolha;
    }

    public Integer getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(Integer numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLocalTrabalhoConjuge() {
        return localTrabalhoConjuge;
    }

    public void setLocalTrabalhoConjuge(String localTrabalhoConjuge) {
        this.localTrabalhoConjuge = localTrabalhoConjuge;
    }

    public Date getDataCasamento() {
        return dataCasamento != null ? DataUtil.dataSemHorario(dataCasamento) : dataCasamento;

    }

    public void setDataCasamento(Date dataCasamento) {
        this.dataCasamento = dataCasamento;
    }

    public Date getDataNascimentoConjuge() {
        return dataNascimentoConjuge != null ? DataUtil.dataSemHorario(dataNascimentoConjuge) : dataNascimentoConjuge;

    }

    public void setDataNascimentoConjuge(Date dataNascimentoConjuge) {
        this.dataNascimentoConjuge = dataNascimentoConjuge;
    }

    public Cidade getCidadeCartorio() {
        return cidadeCartorio;
    }

    public void setCidadeCartorio(Cidade cidadeCartorio) {
        this.cidadeCartorio = cidadeCartorio;
    }

    public Cidade getNaturalidadeConjuge() {
        return naturalidadeConjuge;
    }

    public void setNaturalidadeConjuge(Cidade naturalidadeConjuge) {
        this.naturalidadeConjuge = naturalidadeConjuge;
    }

    public Date getDataAverbacao() {
        return dataAverbacao != null ? DataUtil.dataSemHorario(dataAverbacao) : dataAverbacao;
    }

    public void setDataAverbacao(Date dataAverbacao) {
        this.dataAverbacao = dataAverbacao;
    }

    public static CertidaoCasamentoPortal dtoToCertidaoCasamentoPortal(CertidaoCasamentoDTO dto) {
        CertidaoCasamentoPortal c = new CertidaoCasamentoPortal();
        c.setCidadeCartorio(Cidade.dtoToCidade(dto.getCidadeCartorio()));
        c.setDataCasamento(dto.getDataCasamento());
        c.setDataNascimentoConjuge(dto.getDataNascimentoConjuge());
        c.setNomeCartorio(dto.getNomeCartorio());
        c.setNomeConjuge(dto.getNomeConjuge());
        c.setNaturalidadeConjuge(Cidade.dtoToCidade(dto.getNaturalidadeConjuge()));
        c.setEstado(dto.getEstado());
        c.setLocalTrabalhoConjuge(dto.getLocalTrabalhoConjuge());
        c.setNacionalidade(Nacionalidade.dtoToNacionalidade(dto.getNacionalidadeConjuge()));
        c.setNumeroFolha(dto.getNumeroFolha());
        c.setNumeroLivro(dto.getNumeroLivro());
        c.setNumeroRegistro(dto.getNumeroRegistro());
        c.setDataAverbacao(dto.getDataAverbacao());
        return c;

    }

    public boolean isCamposPreenchidos() {
        if (this.cidadeCartorio == null
            && this.dataCasamento == null
            && this.dataNascimentoConjuge == null
            && this.nomeCartorio == null
            && this.nomeConjuge == null
            && this.estado == null
            && this.localTrabalhoConjuge == null
            && this.nacionalidade == null
            && this.numeroFolha == null
            && this.numeroLivro == null
            && this.numeroRegistro == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertidaoCasamentoPortal that = (CertidaoCasamentoPortal) o;
        return Objects.equals(nomeConjuge != null ? nomeConjuge.toUpperCase().trim() : null, that.nomeConjuge != null ? that.nomeConjuge.toUpperCase().trim() : null)
            && Objects.equals(nomeCartorio != null ? nomeCartorio.toUpperCase().trim() : null, that.nomeCartorio != null ? that.nomeCartorio.toUpperCase().trim() : null) &&
            Objects.equals(numeroLivro != null ? numeroLivro.trim() : null, that.numeroLivro != null ? that.numeroLivro.trim() : null)
            && Objects.equals(numeroFolha, that.numeroFolha) &&
            Objects.equals(numeroRegistro, that.numeroRegistro) && Objects.equals(nacionalidade, that.nacionalidade) &&
            Objects.equals(estado != null ? estado.toUpperCase().trim() : null, that.estado != null ? that.estado.toUpperCase().trim() : null)
            && Objects.equals(localTrabalhoConjuge != null ? localTrabalhoConjuge.toUpperCase().trim() : null, that.localTrabalhoConjuge != null ? that.localTrabalhoConjuge.toUpperCase().trim() : null) &&
            Objects.equals(DataUtil.getDataFormatada(dataCasamento), DataUtil.getDataFormatada(that.dataCasamento)) &&
            Objects.equals(DataUtil.getDataFormatada(dataNascimentoConjuge), DataUtil.getDataFormatada(that.dataNascimentoConjuge)) &&
            Objects.equals(cidadeCartorio, that.cidadeCartorio) && Objects.equals(naturalidadeConjuge, that.naturalidadeConjuge)
            && Objects.equals(dataAverbacao, that.dataAverbacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeConjuge, nomeCartorio, numeroLivro, numeroFolha, numeroRegistro, nacionalidade, estado, localTrabalhoConjuge, dataCasamento, dataNascimentoConjuge, cidadeCartorio, naturalidadeConjuge, dataAverbacao);
    }
}
