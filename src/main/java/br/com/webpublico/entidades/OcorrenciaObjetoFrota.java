/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Ocorrências")
public class OcorrenciaObjetoFrota extends SuperEntidade implements PossuidorArquivo {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo")
    @Enumerated(EnumType.STRING)
    private TipoObjetoFrota tipoObjetoFrota;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Veículo/Equipamento")
    @Tabelavel
    @Pesquisavel
    private ObjetoFrota objetoFrota;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo da Ocorrência")
    @Pesquisavel
    private TipoOcorrenciaObjetoFrota tipoOcorrenciaObjetoFrota;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data da Ocorrência")
    @Obrigatorio
    private Date dataOcorrencia;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Cidade")
    @Obrigatorio
    @Pesquisavel
    private Cidade cidade;
    @Tabelavel
    @Etiqueta("Local da Ocorrência")
    @Obrigatorio
    @Pesquisavel
    @Length(maximo = 70)
    private String localOcorrencia;
    @Etiqueta("Observações")
    @Length(maximo = 255)
    private String observacao;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;


    public OcorrenciaObjetoFrota() {
    }

    public OcorrenciaObjetoFrota(OcorrenciaObjetoFrota ocorrenciaObjetoFrota, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(ocorrenciaObjetoFrota.getId());
        this.setTipoObjetoFrota(ocorrenciaObjetoFrota.getTipoObjetoFrota());
        this.setObjetoFrota(ocorrenciaObjetoFrota.getObjetoFrota());
        this.setTipoOcorrenciaObjetoFrota(ocorrenciaObjetoFrota.getTipoOcorrenciaObjetoFrota());
        this.setDataOcorrencia(ocorrenciaObjetoFrota.getDataOcorrencia());
        this.setCidade(ocorrenciaObjetoFrota.getCidade());
        this.setLocalOcorrencia(ocorrenciaObjetoFrota.getLocalOcorrencia());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoOcorrenciaObjetoFrota getTipoOcorrenciaObjetoFrota() {
        return tipoOcorrenciaObjetoFrota;
    }

    public void setTipoOcorrenciaObjetoFrota(TipoOcorrenciaObjetoFrota tipoOcorrenciaObjetoFrota) {
        this.tipoOcorrenciaObjetoFrota = tipoOcorrenciaObjetoFrota;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public String getLocalOcorrencia() {
        return localOcorrencia;
    }

    public void setLocalOcorrencia(String localOcorrencia) {
        this.localOcorrencia = localOcorrencia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return localOcorrencia + " - " + new SimpleDateFormat("dd/MM/yyyy").format(dataOcorrencia);
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
