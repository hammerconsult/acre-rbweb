package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 14/08/14
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Chamado")
@Audited
@Entity
public class Chamado extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Visualizado Por")
    private String visualizadoPor;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Módulo")
    private ModuloSistema modulo;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Nível")
    private NivelChamado nivel;
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuario;
    @Etiqueta("Mensagem")
    private String mensagem;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Enviada Em")
    private Date enviadaEm;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Resolvida Em")
    private Date resolvidaEm;
    @OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespostaChamado> respostas;

    public Chamado() {
        this.nivel = NivelChamado.SUPORTE_PREFEITURA;
        respostas = new ArrayList<RespostaChamado>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVisualizadoPor() {
        return visualizadoPor;
    }

    public void setVisualizadoPor(String visualizadoPor) {
        this.visualizadoPor = visualizadoPor;
    }

    public ModuloSistema getModulo() {
        return modulo;
    }

    public void setModulo(ModuloSistema modulo) {
        this.modulo = modulo;
    }

    public NivelChamado getNivel() {
        return nivel;
    }

    public void setNivel(NivelChamado nivel) {
        this.nivel = nivel;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getEnviadaEm() {
        return enviadaEm;
    }

    public void setEnviadaEm(Date enviadaEm) {
        this.enviadaEm = enviadaEm;
    }

    public Date getResolvidaEm() {
        return resolvidaEm;
    }

    public void setResolvidaEm(Date resolvidaEm) {
        this.resolvidaEm = resolvidaEm;
    }

    public List<RespostaChamado> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<RespostaChamado> respostas) {
        this.respostas = respostas;
    }

    public Integer getQuantidadeVisualizacao() {
        if (visualizadoPor == null) {
            return 0;
        } else {
            if (visualizadoPor.contains(",")) {
                String[] split = visualizadoPor.split(",");
                return split.length;
            }
            return 1;
        }
    }

    public NivelChamado getProximoNivel() {
        if (this.getNivel().equals(NivelChamado.SUPORTE_PREFEITURA)) {
            return NivelChamado.SUPORTE;
        } else if (this.getNivel().equals(NivelChamado.SUPORTE)) {
            return NivelChamado.RESPONSAVEL_MODULO;
        }
        return NivelChamado.RESPONSAVEL_MODULO;
    }

    @Override
    public String toString() {
        return mensagem;
    }
}
