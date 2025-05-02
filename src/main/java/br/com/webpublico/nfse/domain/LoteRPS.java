package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.SituacaoLoteRps;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Etiqueta("Lote de RPS")
public class LoteRPS extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroEconomico prestador;
    private String numero;
    @Enumerated(EnumType.STRING)
    private SituacaoLoteRps situacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRecebimento;
    @OneToOne
    private XmlWsNfse xmlWsNfse;
    @OneToOne(mappedBy = "loteRps")
    private LoteRPSLog log;
    private String protocolo;
    private String versaoSistema;
    private String versaoAbrasf;
    private Boolean homologacao;
    @OneToMany(mappedBy = "loteRPS")
    private List<Rps> listaRps;

    public LoteRPS() {
        listaRps = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public SituacaoLoteRps getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLoteRps situacao) {
        this.situacao = situacao;
    }

    public XmlWsNfse getXmlWsNfse() {
        return xmlWsNfse;
    }

    public void setXmlWsNfse(XmlWsNfse xmlWsNfse) {
        this.xmlWsNfse = xmlWsNfse;
    }

    public LoteRPSLog getLog() {
        return log;
    }

    public void setLog(LoteRPSLog log) {
        this.log = log;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<Rps> getListaRps() {
        return listaRps;
    }

    public void setListaRps(List<Rps> listaRps) {
        this.listaRps = listaRps;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public Date getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(Date dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public String getVersaoSistema() {
        return versaoSistema;
    }

    public void setVersaoSistema(String versaoSistema) {
        this.versaoSistema = versaoSistema;
    }

    public String getVersaoAbrasf() {
        return versaoAbrasf;
    }

    public void setVersaoAbrasf(String versaoAbrasf) {
        this.versaoAbrasf = versaoAbrasf;
    }

    public Boolean getHomologacao() {
        return homologacao;
    }

    public void setHomologacao(Boolean homologacao) {
        this.homologacao = homologacao;
    }

    @Override
    public String toString() {
        return "Cadastro: " + prestador + " Numero: " + numero;
    }
}
