package br.com.webpublico.entidades;


import br.com.webpublico.enums.TipoColetaDados;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Audited
@Entity
public class LevantamentoColetaDados extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    private Long codigo;

    @Etiqueta("Data da Coleta")
    private Date dataColeta;

    @ManyToOne
    @Etiqueta("Usuário Sistema")
    private UsuarioSistema usuarioSistema;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Coleta de Dados")
    private TipoColetaDados tipoColetaDados;

    @OneToMany(mappedBy = "levantamentoColetaDados", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LevantamentoColetaDadosItem> itens;

    public LevantamentoColetaDados() {
        itens = Lists.newArrayList();
        tipoColetaDados = TipoColetaDados.POR_BCI;
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

    public Date getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(Date dataColeta) {
        this.dataColeta = dataColeta;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoColetaDados getTipoColetaDados() {
        return tipoColetaDados;
    }

    public void setTipoColetaDados(TipoColetaDados tipoColetaDados) {
        this.tipoColetaDados = tipoColetaDados;
    }

    public List<LevantamentoColetaDadosItem> getItens() {
        return itens;
    }

    public void setItens(List<LevantamentoColetaDadosItem> itens) {
        this.itens = itens;
    }
}
