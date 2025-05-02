package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class HistoricoEnvioEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoEmpregadorESocial empregador;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvio;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    private TipoClasseESocial tipoClasseESocial;

    @Enumerated(EnumType.STRING)
    private Mes mes;

    @ManyToOne
    private Exercicio exercicio;

    private String motivo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "historicoEnvioEsocial", orphanRemoval = true)
    private List<ItemHistoricoEnvioEsocial> itemHistoricoEnvios;

    public HistoricoEnvioEsocial() {
        itemHistoricoEnvios = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoClasseESocial getTipoClasseESocial() {
        return tipoClasseESocial;
    }

    public void setTipoClasseESocial(TipoClasseESocial tipoClasseESocial) {
        this.tipoClasseESocial = tipoClasseESocial;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public List<ItemHistoricoEnvioEsocial> getItemHistoricoEnvios() {
        return itemHistoricoEnvios;
    }

    public void setItemHistoricoEnvios(List<ItemHistoricoEnvioEsocial> itemHistoricoEnvios) {
        this.itemHistoricoEnvios = itemHistoricoEnvios;
    }
}
