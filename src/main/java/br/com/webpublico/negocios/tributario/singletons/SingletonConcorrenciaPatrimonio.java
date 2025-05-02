package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ejb.*;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 20, unit = TimeUnit.MINUTES)
public class SingletonConcorrenciaPatrimonio implements Serializable {

    private List<Bem> bensBloqueados;
    private Map<Class, List<UnidadeOrganizacional>> mapaMovimentoUnidades;

    @Lock(LockType.WRITE)
    public void bloquearBem(Bem bem, AssistenteMovimentacaoBens assistente) {
        if (bensBloqueados == null) {
            resetarBloqueioBem();
        }
        if (!verificarBloqueioBem(bem, assistente)) {
            bensBloqueados.add(bem);
        }
    }

    @Lock(LockType.WRITE)
    public void desbloquearBem(Bem bem) {
        if (bensBloqueados != null && bensBloqueados.contains(bem)) {
            bensBloqueados.remove(bem);
        }
    }

    @Lock(LockType.WRITE)
    public void desbloquearBens(List<Bem> bens) {
        if (bens != null && !bens.isEmpty()) {
            for (Bem bem : bens) {
                desbloquearBem(bem);
            }
        }
    }

    @Lock(LockType.WRITE)
    public void verificarBloqueioBemLancandoException(Bem bem, AssistenteMovimentacaoBens assistente) {
        MovimentacaoBemException ve = new MovimentacaoBemException();
        if (bensBloqueados == null) {
            resetarBloqueioBem();
        }
        for (Bem bemBloqueado : bensBloqueados) {
            if (bemBloqueado.equals(bem)) {
                assistente.setBloquearAcoesTela(true);
                ve.adicionarMensagemDeOperacaoNaoPermitida("O bem: " + bemBloqueado + ". está sendo movimentado em outro processo.");
            }
        }
        ve.lancarException();
    }

    @Lock(LockType.WRITE)
    public boolean verificarBloqueioBem(Bem bem, AssistenteMovimentacaoBens assistente) {
        if (bensBloqueados == null) {
            resetarBloqueioBem();
        }
        for (Bem bemBloqueado : bensBloqueados) {
            if (bemBloqueado.equals(bem)) {
                assistente.setBloquearAcoesTela(true);
                assistente.getMensagens().add("O bem: " + bemBloqueado + ". está sendo movimentado em outro processo.");
                return true;
            }
        }
        return false;
    }

    public void resetarBloqueioBem() {
        bensBloqueados = Lists.newArrayList();
    }

    public boolean hasBemBloqueado(Bem bem) {
        if (bensBloqueados == null) {
            resetarBloqueioBem();
        }
        for (Bem bemBloqueado : bensBloqueados) {
            if (bemBloqueado.equals(bem)) {
                return true;
            }
        }
        return false;
    }

    @Lock(LockType.WRITE)
    public void bloquearMovimentoPorUnidade(Class movimento, UnidadeOrganizacional unidade, AssistenteMovimentacaoBens assistente) {
        if (mapaMovimentoUnidades == null) {
            resetarBloqueioPorUnidade();
        }
        verificarBloqueioPorUnidade(movimento, unidade, assistente);
        if (!mapaMovimentoUnidades.containsKey(movimento)) {
            mapaMovimentoUnidades.put(movimento, Lists.<UnidadeOrganizacional>newArrayList(unidade));
        } else {
            mapaMovimentoUnidades.get(movimento).add(unidade);
        }
    }

    public void resetarBloqueioPorUnidade() {
        mapaMovimentoUnidades = Maps.newHashMap();
    }

    @Lock(LockType.WRITE)
    public void desbloquearMovimentoPorUnidade(Class movimento, UnidadeOrganizacional unidadeOrganizacional) {
        if (mapaMovimentoUnidades != null) {
            if (mapaMovimentoUnidades.containsKey(movimento)) {
                if (mapaMovimentoUnidades.get(movimento).size() > 1) {
                    Iterator i = mapaMovimentoUnidades.get(movimento).iterator();
                    while (i.hasNext()) {
                        Object unidadeDaVez = i.next();
                        if (unidadeDaVez.equals(unidadeOrganizacional)) {
                            i.remove();
                        }
                    }
                } else {
                    mapaMovimentoUnidades.remove(movimento);
                }
            }
            if (verificarMovimentoBloqueadoPorUnidade(movimento, unidadeOrganizacional)) {
                mapaMovimentoUnidades.remove(movimento);
            }
        }
    }

    @Lock(LockType.WRITE)
    public void verificarBloqueioPorUnidade(Class movimento, UnidadeOrganizacional unidade, AssistenteMovimentacaoBens assistente) {
        MovimentacaoBemException ve = new MovimentacaoBemException();
        if (mapaMovimentoUnidades == null) {
            resetarBloqueioPorUnidade();
        }
        for (Class movimentoBloqueado : mapaMovimentoUnidades.keySet()) {
            if (movimentoBloqueado.equals(movimento)) {
                for (UnidadeOrganizacional unidadesBloqueada : mapaMovimentoUnidades.get(movimentoBloqueado)) {
                    if (unidade.equals(unidadesBloqueada)) {
                        String nomeClasse = buscarNomeDaClasse(movimento);
                        if (assistente != null) {
                            if (assistente.isOperacaoNovo()) {
                                ve.adicionarMensagemDeOperacaoNaoPermitida("Estão sendo gerados movimentos para os bens na " + nomeClasse + ". Por favor, aguarde alguns instantes e execute o processo novamente.");
                            } else {
                                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe outro usuário movimentando esta " + nomeClasse + ". Por favor, aguarde alguns instantes e execute o processo novamente.");
                            }
                            assistente.setBloquearAcoesTela(true);
                        }
                    }
                }
            }
        }
        ve.lancarException();
    }

    public boolean verificarMovimentoBloqueadoPorUnidade(Class movimento, UnidadeOrganizacional unidadeOrganizacional) {
        if (mapaMovimentoUnidades != null && mapaMovimentoUnidades.get(movimento) != null) {
            List<UnidadeOrganizacional> unidades = mapaMovimentoUnidades.get(movimento);
            for (UnidadeOrganizacional unidade : unidades) {
                if (mapaMovimentoUnidades != null
                    && mapaMovimentoUnidades.containsKey(movimento)
                    && unidade.equals(unidadeOrganizacional)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean verificarBensBloqueadoSingletonAoPesquisar(AssistenteMovimentacaoBens assistente) {
        if (assistente.getBensSelecionados() != null && !assistente.getBensSelecionados().isEmpty()) {
            for (Bem bensSelecionado : assistente.getBensSelecionados()) {
                if (hasBemBloqueado(bensSelecionado)) {
                    if (assistente.isOperacaoNovo()) {
                        assistente.setBloquearAcoesTela(true);
                        assistente.getMensagens().add("O bem: " + bensSelecionado + ". está sendo movimentado em outro processo.");
                    } else {
                        assistente.getMensagens().add(assistente.getMensagemBensBloqueadoSingleton());
                        assistente.setBloquearAcoesTela(true);
                        return true;
                    }
                }
            }
            return assistente.hasInconsistencias();
        }
        if (assistente.hasBensSelecionadoVo()) {
            for (BemVo bensSelecionado : assistente.getBensSelecionadosVo()) {
                if (hasBemBloqueado(bensSelecionado.getBem())) {
                    if (assistente.isOperacaoNovo()) {
                        assistente.setBloquearAcoesTela(true);
                        assistente.getMensagens().add("O bem: " + bensSelecionado.getBem() + ". está sendo movimentado em outro processo.");
                    } else {
                        assistente.getMensagens().add(assistente.getMensagemBensBloqueadoSingleton());
                        assistente.setBloquearAcoesTela(true);
                        return true;
                    }
                }
            }
            return assistente.hasInconsistencias();
        }
        return false;
    }

    public void desbloquearMovimentacaoSingleton(AssistenteMovimentacaoBens assistente, Class movimento) {
        if (assistente != null) {
            if (assistente.hasBensSelecionadoVo()) {
                for (BemVo bemVo : assistente.getBensSelecionadosVo()) {
                    desbloquearBem(bemVo.getBem());
                }
            }
            if (assistente.getBensSelecionados() != null && !assistente.getBensSelecionados().isEmpty()) {
                desbloquearBens(assistente.getBensSelecionados());
            }
            if (assistente.getUnidadeOrganizacional() != null) {
                desbloquearMovimentoPorUnidade(movimento, assistente.getUnidadeOrganizacional());
            }
            assistente.setBloquearAcoesTela(false);
        }
    }

    private String buscarNomeDaClasse(Class movimento) {
        return Util.buscarNomeDaClasse(movimento);
    }

    public List<Bem> getBensBloqueados() {
        return bensBloqueados;
    }

    public void setBensBloqueados(List<Bem> bensBloqueados) {
        this.bensBloqueados = bensBloqueados;
    }

    public Map<Class, List<UnidadeOrganizacional>> getMapaMovimentoUnidades() {
        return mapaMovimentoUnidades;
    }

    public void setMapaMovimentoUnidades(Map<Class, List<UnidadeOrganizacional>> mapaMovimentoUnidades) {
        this.mapaMovimentoUnidades = mapaMovimentoUnidades;
    }
}
