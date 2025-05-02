package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/07/13
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Cacheable
@Audited
@Etiqueta("Efetivação de Lançamento Geral de ISS Fixo")
public class EfetivacaoIssFixoGeral implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Usuário da Efetivação")
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data/Hora da Efetivação")
    @Tabelavel
    @Pesquisavel
    private Date dataDaEfetivacao;
    @OneToMany(mappedBy = "efetivacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EfetivacaoProcessoIssFixoGeral> listaDeProcessos;
    @Transient
    @Invisivel
    private Long criadoEm;

    public EfetivacaoIssFixoGeral() {
        this.criadoEm = System.nanoTime();
        this.listaDeProcessos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<EfetivacaoProcessoIssFixoGeral> getListaDeProcessos() {
        return listaDeProcessos;
    }

    public void setListaDeProcessos(List<EfetivacaoProcessoIssFixoGeral> listaDeProcessos) {
        this.listaDeProcessos = listaDeProcessos;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataDaEfetivacao() {
        return dataDaEfetivacao;
    }

    public String getDataDaEfetivacaoTimeStamp() {
        return Util.dateHourToString(dataDaEfetivacao);
    }

    public void setDataDaEfetivacao(Date dataDaEfetivacao) {
        this.dataDaEfetivacao = dataDaEfetivacao;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
