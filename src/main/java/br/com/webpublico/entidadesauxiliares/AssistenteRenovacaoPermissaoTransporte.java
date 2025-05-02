package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Wellington on 11/01/2016.
 */
public class AssistenteRenovacaoPermissaoTransporte implements Serializable {

    private Date dataOperacao;
    private Exercicio exercicio;
    private UsuarioSistema usuarioSistema;
    private PermissaoTransporte permissaoTransporte;
    private RenovacaoPermissao renovacaoPermissao;
    private List<CalculoRBTrans> calculosGerados;

    public AssistenteRenovacaoPermissaoTransporte() {
        calculosGerados = Lists.newArrayList();
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public RenovacaoPermissao getRenovacaoPermissao() {
        return renovacaoPermissao;
    }

    public void setRenovacaoPermissao(RenovacaoPermissao renovacaoPermissao) {
        this.renovacaoPermissao = renovacaoPermissao;
    }

    public List<CalculoRBTrans> getCalculosGerados() {
        return calculosGerados;
    }

    public void setCalculosGerados(List<CalculoRBTrans> calculosGerados) {
        this.calculosGerados = calculosGerados;
    }
}
