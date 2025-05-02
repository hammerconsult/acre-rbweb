/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAlteracaoCertidaoDA;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Ativa")
public class AlteracaoSituacaoCDA extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Alteração")
    private TipoAlteracaoCertidaoDA tipoAlteracao;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Alteração")
    private Date data;
    private String motivo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário Responsável")
    private UsuarioSistema usuarioSistema;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String nrProtocolo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano do Protocolo")
    private Integer anoProtocolo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("CDA")
    private CertidaoDividaAtiva certidao;
    @Invisivel
    @Transient
    private Long criadoEm;

    public AlteracaoSituacaoCDA() {
        this.criadoEm = System.nanoTime();
    }

    public AlteracaoSituacaoCDA(TipoAlteracaoCertidaoDA tipoAlteracao,
                                Date data, UsuarioSistema usuarioSistema,
                                String nrProtocolo, Integer anoProtocolo, Long codigo, String motivo,
                                CertidaoDividaAtiva certidao) {
        this.tipoAlteracao = tipoAlteracao;
        this.data = data;
        this.motivo = motivo;
        this.usuarioSistema = usuarioSistema;
        this.nrProtocolo = nrProtocolo;
        this.certidao = certidao;
        this.criadoEm = System.nanoTime();
        this.anoProtocolo = anoProtocolo;
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getNrProtocolo() {
        return nrProtocolo;
    }

    public void setNrProtocolo(String nrProtocolo) {
        this.nrProtocolo = nrProtocolo;
    }

    public CertidaoDividaAtiva getCertidao() {
        return certidao;
    }

    public void setCertidao(CertidaoDividaAtiva certidao) {
        this.certidao = certidao;
    }

    public TipoAlteracaoCertidaoDA getTipoAlteracao() {
        return tipoAlteracao;
    }

    public void setTipoAlteracao(TipoAlteracaoCertidaoDA tipoAlteracao) {
        this.tipoAlteracao = tipoAlteracao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Integer getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(Integer anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }
}
