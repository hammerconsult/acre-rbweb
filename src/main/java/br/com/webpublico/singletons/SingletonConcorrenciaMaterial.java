package br.com.webpublico.singletons;

import br.com.webpublico.entidades.RequisicaoMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Sets;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Set;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonConcorrenciaMaterial implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    private Set<RequisicaoMaterial> requisicoesMateriais;

    @Lock(LockType.WRITE)
    public void bloquearRequisicao(RequisicaoMaterial requisicaoMaterial) {
        if (requisicoesMateriais == null) {
            reiniciarRequisicoes();
        }
        requisicoesMateriais.add(requisicaoMaterial);
    }

    @Lock(LockType.WRITE)
    public void desbloquearRequisicao(RequisicaoMaterial requisicaoMateria) {
        requisicoesMateriais.remove(requisicaoMateria);
    }

    @Lock(LockType.WRITE)
    public boolean isBloqueado(RequisicaoMaterial requisicaoMateria) {
        return requisicoesMateriais != null && requisicoesMateriais.contains(requisicaoMateria);
    }

    public void reiniciarRequisicoes() {
        requisicoesMateriais = Sets.newHashSet();
    }

    public void validarRequisicaoSingleton(RequisicaoMaterial requisicaoMaterial) {
        ValidacaoException ve = new ValidacaoException();
        if (isBloqueado(requisicaoMaterial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe outro usuário concluindo a saída da requisição " + requisicaoMaterial + "Atualize a página e tente novamente.");
        }
        ve.lancarException();
    }

    public Set<RequisicaoMaterial> getRequisicoesMateriais() {
        return requisicoesMateriais;
    }

    public void setRequisicoesMateriais(Set<RequisicaoMaterial> requisicoesMateriais) {
        this.requisicoesMateriais = requisicoesMateriais;
    }
}
