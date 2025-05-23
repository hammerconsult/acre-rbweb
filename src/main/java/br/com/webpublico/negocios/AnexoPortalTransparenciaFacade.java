package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.PaginaPrefeituraPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class AnexoPortalTransparenciaFacade extends AbstractFacade<AnexoPortalTransparencia> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PaginaPrefeituraPortalFacade paginaPrefeituraPortalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnexoPortalTransparenciaFacade() {
        super(AnexoPortalTransparencia.class);
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasAdministrativas(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipo(parte, sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(AnexoPortalTransparencia selecionado) {
        if (selecionado.getUnidadeOrganizacional() != null) {
            return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }
        return null;
    }

    public List<PaginaPrefeituraPortal> buscarPaginas(String parte) {
        return paginaPrefeituraPortalFacade.buscarPaginas(parte, true);
    }

    public Arquivo criarArquivo(UploadedFile file) throws IOException {
        return arquivoFacade.criarArquivo(file);
    }

    @Override
    public void preSave(AnexoPortalTransparencia entidade) {
        entidade.setDataCadastro(sistemaFacade.getDataOperacao());
        entidade.setUnidadeOrganizacional(entidade.getHierarquia() != null ? entidade.getHierarquia().getSubordinada() : null);

        Util.validarCampos(entidade);
        validarAnexoJaCadastrado(entidade);

        Arquivo arquivoAtual = buscarArquivo(entidade);
        if (arquivoAtual != null) {
            arquivoFacade.removerArquivo(arquivoAtual);
        }
        entidade.setArquivo(arquivoFacade.salvarArquivoJaRecuperadoRetornando(entidade.getArquivo()));
    }

    private void validarAnexoJaCadastrado(AnexoPortalTransparencia selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (hasAnexoPorNomeExercicioPaginaEMes(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não deve existir mais que um registro com o mesmo Exercício: <b>" + selecionado.getExercicio() +
                "</b>, Página do Portal: <b>" + selecionado.getPaginaPrefeituraPortal().getNome() + " - " + selecionado.getPaginaPrefeituraPortal().getChave() + "</b>" +
                (selecionado.getMes() != null ? " e Mês: <b>" + selecionado.getMes().getDescricao() + "</b>" : ""));
        }
        ve.lancarException();
    }

    public boolean hasAnexoPorNomeExercicioPaginaEMes(AnexoPortalTransparencia anexoPortal) {
        String sql = " select anexoPortal.id " +
            "  from anexoportaltransparencia anexoPortal " +
            " where anexoPortal.exercicio_id = :idExercicio " +
            "   and anexoPortal.paginaprefeituraportal_id = :idPagina " +
            "   and anexoPortal.nome = :nomeAnexoPortal " +
            (anexoPortal.getMes() != null ? " and anexoPortal.mes = :mes " : "") +
            (anexoPortal.getId() != null ? " and anexoPortal.id <> :idAnexoPortal " : "") +
            " order by anexoPortal.datacadastro desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", anexoPortal.getExercicio().getId());
        q.setParameter("idPagina", anexoPortal.getPaginaPrefeituraPortal().getId());
        q.setParameter("nomeAnexoPortal", anexoPortal.getNome());
        if (anexoPortal.getMes() != null) {
            q.setParameter("mes", anexoPortal.getMes().name());
        }
        if (anexoPortal.getId() != null) {
            q.setParameter("idAnexoPortal", anexoPortal.getId());
        }
        List<BigDecimal> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public Arquivo buscarArquivo(AnexoPortalTransparencia anexoPortal) {
        if (anexoPortal == null || anexoPortal.getId() == null) return null;
        String sql = " select arq.* " +
            " from anexoportaltransparencia anexoPortal " +
            "    inner join arquivo arq on arq.id = anexoPortal.arquivo_id " +
            " where anexoPortal.id = :idAnexoPortal ";
        Query q = em.createNativeQuery(sql, Arquivo.class);
        q.setParameter("idAnexoPortal", anexoPortal.getId());
        List<Arquivo> arquivos = q.getResultList();
        if (arquivos != null && !arquivos.isEmpty()) {
            Arquivo arquivo = arquivos.get(0);
            Hibernate.initialize(arquivo.getPartes());
            return arquivo;
        }
        return null;
    }
}
