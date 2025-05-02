package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 30/12/13
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PortalTransparenciaFacade extends AbstractFacade<PortalTransparencia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PortalTransparenciaFacade() {
        super(PortalTransparencia.class);
    }

    public void salvar(PortalTransparencia entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        try {
            if (verificaSeExisteArquivo(arquivo)) {
                if (entity.getArquivo() == null) {
                    arquivoFacade.removerArquivo(arquivo);
                }
            }
            if (fileUploadEvent != null) {
                entity.setArquivo(arquivo);
                salvarArquivo(fileUploadEvent, arquivo);
            }
            em.merge(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void salvarNovo(PortalTransparencia entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        try {
            if (fileUploadEvent != null) {
                entity.setArquivo(arquivo);
                salvarArquivo(fileUploadEvent, arquivo);
            }
            entity.setDataCadastro(new Date());
            em.persist(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean verificaSeExisteArquivo(Arquivo arq) {
        Arquivo ar = null;
        if (arq == null) {
            return false;
        }
        try {
            ar = arquivoFacade.recupera(arq.getId());
        } catch (Exception e) {
            return false;
        }
        if (arq.equals(ar)) {
            return true;
        }
        return false;
    }

    public void salvarArquivo(FileUploadEvent fileUploadEvent, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public List<PortalTransparencia> recuperarTodosPorExercicio(Exercicio e) {
        Query consulta = em.createNativeQuery("select anexo.* from PortalTransparencia anexo where anexo.exercicio_id = :idExercicio and anexo.situacao = :naoPublicado ", PortalTransparencia.class);
        consulta.setParameter("idExercicio", e.getId());
        consulta.setParameter("naoPublicado", PortalTransparenciaSituacao.NAO_PUBLICADO.name());
        return consulta.getResultList();
    }

    public void salvarPortalTransparenciaArquivo(PortalTransparencia portalTransparencia) {
        em.persist(portalTransparencia.getArquivo());
        em.persist(portalTransparencia);
    }

    public PortalTransparencia buscarAnexoPorExercicioTipoTipoAnexoEMes(Exercicio exercicio, PortalTipoAnexo tipoAnexo, Mes mes) {
        if (exercicio == null || tipoAnexo == null) return null;
        String sql = " select apt.* " +
            " from PortalTransparencia apt " +
            " where apt.exercicio_id = :idExercicio " +
            "   and apt.tipo = :tipo " +
            "   and apt.tipoanexo = :tipoAnexo " +
            (mes != null ? "   and apt.mes = :mes " : "") +
            " order by apt.dataCadastro desc ";
        Query q = em.createNativeQuery(sql, PortalTransparencia.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("tipo", tipoAnexo.getTipo().name());
        q.setParameter("tipoAnexo", tipoAnexo.name());
        if (mes != null) {
            q.setParameter("mes", mes.name());
        }
        List<PortalTransparencia> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
