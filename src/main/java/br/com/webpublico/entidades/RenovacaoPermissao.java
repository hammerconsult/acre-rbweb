package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AndreGustavo on 08/09/2014.
 */
@Entity
@Audited
@Etiqueta("Renovação de Permissão de Transporte")
public class RenovacaoPermissao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.DATE)
    private Date dataRenovacao;
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "renovacaoPermissao")
    private List<CalculoRenovacao> calculosRenovacao;

    public RenovacaoPermissao() {
        calculosRenovacao = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataRenovacao() {
        return dataRenovacao;
    }

    public void setDataRenovacao(Date dataRenovacao) {
        this.dataRenovacao = dataRenovacao;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public List<CalculoRenovacao> getCalculosRenovacao() {
        return calculosRenovacao;
    }

    public void setCalculosRenovacao(List<CalculoRenovacao> calculosRenovacao) {
        this.calculosRenovacao = calculosRenovacao;
    }
}
