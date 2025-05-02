package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class UnidadeMedidaAmbiental extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Unidade")
    private TipoUnidade tipoUnidade;
    @ManyToOne
    @Etiqueta("Assunto")
    private AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental;
    @Obrigatorio
    @Etiqueta("Ativo")
    private Boolean ativo;

    public UnidadeMedidaAmbiental() {
        super();
        this.ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoUnidade getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoUnidade tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public AssuntoLicenciamentoAmbiental getAssuntoLicenciamentoAmbiental() {
        return assuntoLicenciamentoAmbiental;
    }

    public void setAssuntoLicenciamentoAmbiental(AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental) {
        this.assuntoLicenciamentoAmbiental = assuntoLicenciamentoAmbiental;
    }

    public enum TipoUnidade implements EnumComDescricao {
        COMPRIMENTO("Comprimento (M)"),
        CAPACIDADE("Capacidade (L)"),
        MASSA("Massa (Kg)"),
        VOLUME("Volume (M3)"),
        QUANTIDADE("Quantidade");
        private final String descricao;

        TipoUnidade(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @Override
    public String toString() {
        return descricao;
    }
}
