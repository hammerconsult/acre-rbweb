package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by AndreGustavo on 11/09/2014.
 */
@Entity
@Audited
@Etiqueta("Dígitos Vencimento")
public class DigitoVencimento extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Dígito")
    private Integer digito;
    @Etiqueta("Dia")
    private Integer dia;
    @Etiqueta("Mês")
    private Integer mes;
    @ManyToOne
    private ParametrosTransitoTransporte parametro;
    @ManyToOne
    private ParametrosOtt parametroOtt;
    @Enumerated(EnumType.STRING)
    private TipoDigitoVencimento tipoDigitoVencimento;

    public DigitoVencimento() {
        dia = null;
        mes = null;
        parametro = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDigito() {
        return digito;
    }

    public void setDigito(Integer digito) {
        this.digito = digito;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public ParametrosTransitoTransporte getParametro() {
        return parametro;
    }

    public void setParametro(ParametrosTransitoTransporte parametro) {
        this.parametro = parametro;
    }

    public TipoDigitoVencimento getTipoDigitoVencimento() {
        return tipoDigitoVencimento;
    }

    public void setTipoDigitoVencimento(TipoDigitoVencimento tipoDigitoVencimento) {
        this.tipoDigitoVencimento = tipoDigitoVencimento;
    }

    public ParametrosOtt getParametroOtt() {
        return parametroOtt;
    }

    public void setParametroOtt(ParametrosOtt parametroOtt) {
        this.parametroOtt = parametroOtt;
    }

    public enum TipoDigitoVencimento {
        LICENCIAMENTO("Licenciamento"),
        CREDENCIAL("Credenciamento"),
        VISTORIA_VEICULO_OTT("Vistoria"),
        CERTIFICADO_VEICULO_OTT("Certificado");

        private final String descricao;

        TipoDigitoVencimento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
