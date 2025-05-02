package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 23/08/2016.
 */

@Entity
@Audited
@Etiqueta("Exportação de Permissionários e Concessionários")
@Table(name = "ARQPERMISSCONCESS")
public class ArquivoPermissionarioConcessionario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data/Hora da Operação")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @OneToMany(mappedBy = "arquivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPermissionarioConcessionario> itensArquivo;

    public ArquivoPermissionarioConcessionario() {
        super();
        this.itensArquivo = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public List<ItemPermissionarioConcessionario> getItensArquivo() {
        return itensArquivo;
    }

    public void setItensArquivo(List<ItemPermissionarioConcessionario> itensArquivo) {
        this.itensArquivo = itensArquivo;
    }

    public String getCodigoString() {
        return codigo == null ? "Preenchido automaticamente ao salvar." : codigo.toString();
    }

}
