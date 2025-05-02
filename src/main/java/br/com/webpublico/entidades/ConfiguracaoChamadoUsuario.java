package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 14/08/14
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Chamado")
@Audited
@Entity
public class ConfiguracaoChamadoUsuario extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Módulo")
    @Obrigatorio
    private ModuloSistema modulo;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Nível")
    @Obrigatorio
    private NivelChamado nivel;
    @Etiqueta("Usuário")
    @ManyToOne
    @Obrigatorio
    private UsuarioSistema usuario;
    @Etiqueta("Usuário")
    @ManyToOne
    @Obrigatorio
    private ConfiguracaoChamado configucao;

    public ConfiguracaoChamadoUsuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ConfiguracaoChamado getConfigucao() {
        return configucao;
    }

    public void setConfigucao(ConfiguracaoChamado configucao) {
        this.configucao = configucao;
    }

    @Override
    public String toString() {
        return usuario.getNome();
    }
}
