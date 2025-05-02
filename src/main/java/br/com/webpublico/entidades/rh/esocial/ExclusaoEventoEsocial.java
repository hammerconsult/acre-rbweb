package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.rh.esocial.TipoSituacaoGeracaoEsocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Registro de Exclusão S-3000")
public class ExclusaoEventoEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data da criação")
    @Tabelavel
    @Pesquisavel
    private Date dataCriacao;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Envio")
    @Tabelavel
    @Pesquisavel
    private Date dataEnvio;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @OneToMany(mappedBy = "exclusaoEventoEsocial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroExclusaoS3000> itemExclusao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Status")
    @Tabelavel
    @Pesquisavel
    private TipoSituacaoGeracaoEsocial status;

    public ExclusaoEventoEsocial() {
        itemExclusao = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public List<RegistroExclusaoS3000> getItemExclusao() {
        return itemExclusao;
    }

    public void setItemExclusao(List<RegistroExclusaoS3000> itemExclusao) {
        this.itemExclusao = itemExclusao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoSituacaoGeracaoEsocial getStatus() {
        return status;
    }

    public void setStatus(TipoSituacaoGeracaoEsocial status) {
        this.status = status;
    }
}
