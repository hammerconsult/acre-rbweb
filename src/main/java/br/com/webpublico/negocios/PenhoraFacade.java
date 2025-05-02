/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class PenhoraFacade extends AbstractFacade<Penhora> {

    public static final int FILTRO_BAIXADAS_E_NAO_BAIXADAS = 1;
    public static final int FILTRO_BAIXADAS = 2;
    public static final int FILTRO_NAO_BAIXADAS = 3;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public PenhoraFacade() {
        super(Penhora.class);
    }

    @Override
    public Penhora recuperar(Object id) {
        Penhora p = em.find(Penhora.class, id);
        p.getArquivos().size();
        return p;
    }

    @Override
    public void salvar(Penhora entity) {
        Historico historico = new Historico();
        historico.setCadastro(entity.getCadastroImobiliario());
        historico.setDataRegistro(new Date());
        historico.setDataSolicitacao(entity.getDataPenhora());
        historico.setDigitador(sistemaFacade.getUsuarioCorrente());
        if (entity.getAutor() != null && entity.getAutor().getNome() != null) {
            historico.setSolicitante(entity.getAutor().getNome());
        }
        historico.setMotivo(entity.getDescricaoDaPenhora() + " " + entity.getMotivo());
        em.merge(entity);
        em.merge(historico);
    }

    public void salvar(Penhora selecionado, List<FileUploadEvent> files) {
        if (files != null) {
            try {
                Historico historico = new Historico();
                historico.setCadastro(selecionado.getCadastroImobiliario());
                historico.setDataRegistro(new Date());
                historico.setDataSolicitacao(selecionado.getDataPenhora());
                historico.setDigitador(sistemaFacade.getUsuarioCorrente());
                historico.setSolicitante(selecionado.getAutor().getNome());
                historico.setMotivo(selecionado.getDescricaoDaPenhora() + " " + selecionado.getMotivo());


                for (FileUploadEvent event : files) {
                    UploadedFile arquivoRecebido = event.getFile();
                    Arquivo arq = new Arquivo();
                    arq.setNome(arquivoRecebido.getFileName());
                    //arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
                    arq.setMimeType(arquivoFacade.getMimeType(arquivoRecebido.getFileName()));
                    arq.setDescricao("Upload Cadastro Imobiliário");
                    arq.setTamanho(arquivoRecebido.getSize());
                    ArquivoPenhora arquivoPenhora = new ArquivoPenhora();
                    arquivoPenhora.setArquivo(arq);
                    arquivoPenhora.setPenhora(selecionado);
                    selecionado.getArquivos().add(arquivoPenhora);

                    arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
                    arquivoRecebido.getInputstream().close();
                }

                em.merge(selecionado);
                em.merge(historico);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Penhora> lista() {
        return em.createQuery("from Penhora").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Penhora> listaPorFiltro(CadastroImobiliario filtroCadastroImobiliario, int filtroTipoPenhora, String filtroNumeroProcesso, String filtroNumeroProtocolo, Pessoa filtroAutor) {

        String juncao = " where ";
        String hql = "select distinct p from Penhora p ";

        if (filtroCadastroImobiliario != null && filtroCadastroImobiliario.getId() != null) {
            hql += juncao + " p.cadastroImobiliario = :cadastroimobiliario";
            juncao = " and ";
        }

        if (filtroTipoPenhora == FILTRO_BAIXADAS) {
            hql += juncao + " p.dataBaixa is not null";
            juncao = " and ";
        } else if (filtroTipoPenhora == FILTRO_NAO_BAIXADAS) {
            hql += juncao + " p.dataBaixa is null";
            juncao = " and ";
        }

        if (!"".equals(filtroNumeroProcesso) && !filtroNumeroProcesso.isEmpty()) {
            hql += juncao + " p.numeroProcesso like :numeroprocesso";
            juncao = " and ";
        }

        if (!"".equals(filtroNumeroProtocolo) && !filtroNumeroProtocolo.isEmpty()) {
            hql += juncao + " p.numeroProtocolo like :numeroprotocolo";
            juncao = " and ";
        }

        if (filtroAutor != null && filtroAutor.getId() != null) {
            hql += juncao + " p.autor = :autor";
            juncao = " and ";
        }
        hql += " order by p.id desc";

        Query q = em.createQuery(hql);

        if (filtroCadastroImobiliario != null && filtroCadastroImobiliario.getId() != null) {
            q.setParameter("cadastroimobiliario", filtroCadastroImobiliario);
        }

        if (!"".equals(filtroNumeroProcesso) && !filtroNumeroProcesso.isEmpty()) {
            q.setParameter("numeroprocesso", "%" + filtroNumeroProcesso.trim() + "%");
        }

        if (!"".equals(filtroNumeroProtocolo) && !filtroNumeroProtocolo.isEmpty()) {
            q.setParameter("numeroprotocolo", "%" + filtroNumeroProtocolo.trim() + "%");
        }

        if (filtroAutor != null && filtroAutor.getId() != null) {
            q.setParameter("autor", filtroAutor);
        }

        return q.getResultList();
    }

    public Integer getUltimoNumeroSequencia() {
        Integer sequencia;
        String sql = "SELECT MAX(p.sequencia) FROM Penhora p";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() != null) {
            sequencia = ((BigDecimal) q.getSingleResult()).intValue();
        } else {
            sequencia = 0;
        }

        return sequencia + 1;
    }
}
