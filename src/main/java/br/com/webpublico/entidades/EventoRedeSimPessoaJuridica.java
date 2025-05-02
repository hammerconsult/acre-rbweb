package br.com.webpublico.entidades;

import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Fabio on 09/10/2018.
 */
@Entity
@Audited
@Table(name = "EVENTOREDESIMPJ")
public class EventoRedeSimPessoaJuridica extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private JuntaComercialPessoaJuridica juntaComercial;
    private Integer codigo;
    private String tipo;
    private String descricao;

    public EventoRedeSimPessoaJuridica() {
    }

    public EventoRedeSimPessoaJuridica(JuntaComercialPessoaJuridica juntaComercial, Integer codigo, String tipo, String descricao) {
        this.juntaComercial = juntaComercial;
        this.codigo = codigo;
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public EventoRedeSimPessoaJuridica(JuntaComercialPessoaJuridica junta, EventoRedeSimDTO eventoRedeSimDTO) {
        this.juntaComercial = junta;
        this.codigo = junta.getEventosRedeSim().size() + 1;
        this.tipo = eventoRedeSimDTO.getTipo() != null ? eventoRedeSimDTO.getTipo() : "Manuntenção do Cadastro";
        this.descricao = eventoRedeSimDTO.getIdentificador() != null ? "Identificador : " + eventoRedeSimDTO.getIdentificador() : "Integração Individual";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JuntaComercialPessoaJuridica getJuntaComercial() {
        return juntaComercial;
    }

    public void setJuntaComercial(JuntaComercialPessoaJuridica juntaComercial) {
        this.juntaComercial = juntaComercial;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
