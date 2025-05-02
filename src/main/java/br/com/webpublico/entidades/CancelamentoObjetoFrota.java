package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Octavio
 */

@Entity
@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Cancelamento")
public class CancelamentoObjetoFrota extends SuperEntidade {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    private Long codigo;

    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Cancelado em")
    @Pesquisavel
    @Obrigatorio
    private Date canceladoEm;

    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Length(maximo = 255)
    @Etiqueta("Motivo")
    @Obrigatorio
    private String motivo;

    @Tabelavel
    @Etiqueta("Reserva")
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private ReservaObjetoFrota reservaObjetoFrota;

    public CancelamentoObjetoFrota() {
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

    public Date getCanceladoEm() {
        return canceladoEm;
    }

    public void setCanceladoEm(Date canceladoEm) {
        this.canceladoEm = canceladoEm;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public ReservaObjetoFrota getReservaObjetoFrota() {
        return reservaObjetoFrota;
    }

    public void setReservaObjetoFrota(ReservaObjetoFrota reservaObjetoFrota) {
        this.reservaObjetoFrota = reservaObjetoFrota;
    }

    @Override
    public String toString() {
        try {
            return DataUtil.getDataFormatada(canceladoEm) + " - " + reservaObjetoFrota;
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
