package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeOrganizacional;

import java.util.Date;
import java.util.Objects;

public class VoTramite {

    private Long id;
    private UnidadeOrganizacional unidadeOrganizacional;
    private Date dataAceite;
    private Date dataRegistro;
    private Integer indice;
    private VoProcesso processo;
    private String destino;
    private String destinoExterno;
    private String responsavel;
    private String motivo;
    private String observacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Date getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(Date dataAceite) {
        this.dataAceite = dataAceite;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

    public VoProcesso getProcesso() {
        return processo;
    }

    public void setProcesso(VoProcesso processo) {
        this.processo = processo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDestinoExterno() {
        return destinoExterno;
    }

    public void setDestinoExterno(String destinoExterno) {
        this.destinoExterno = destinoExterno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoTramite voTramite = (VoTramite) o;
        return Objects.equals(id, voTramite.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
