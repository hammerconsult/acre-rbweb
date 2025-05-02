/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author heinz
 */

@Stateless
public class CertidaoAtividadeBCEFacade extends AbstractFacade<CertidaoAtividadeBCE> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private ArquivoFacade arquivoFacade;

    public CertidaoAtividadeBCEFacade() {
        super(CertidaoAtividadeBCE.class);
    }

    public boolean verificaParcelaVencidaENaoPaga(CadastroEconomico ce) {
        if (getParcelaVencidaENaoPaga(ce).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<ParcelaValorDivida> getParcelaVencidaENaoPaga(CadastroEconomico ce) {
        String sql = "SELECT pv.* FROM ParcelaValorDivida pv "
                + "INNER JOIN valorDivida vd ON vd.id = pv.valordivida_id "
                + "INNER JOIN calculoIss calculo ON calculo.id = vd.calculo_id "
                + "INNER JOIN situacaoparcelavalordivida situacao ON situacao.id = pv.situacaoatual_id "
                + "WHERE pv.vencimento < :dataAtual "
                + "AND calculo.cadastroeconomico_id = :cadastroEconomicoId "
                + "AND situacao.situacaoparcela = 'PAGO'"
                + "ORDER BY pv.vencimento";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("dataAtual", new Date());
        q.setParameter("cadastroEconomicoId", ce.getId());
        return q.getResultList();
    }

    public void montarArquivoParaSalvar(CertidaoAtividadeBCE certidao) {
        verificarArquivos(certidao);
    }

    public void verificarArquivos(CertidaoAtividadeBCE certidao) {
        for (CertidaoBCEArquivos obj : certidao.getCertidaoArquivos()) {
            if (obj.getCertidaoAtividadeBce().getId() == null) {
                salvarArquivo((UploadedFile) obj.getFile(), obj.getArquivo());
            }
        }
    }

    public void salvarArquivo(UploadedFile uploadedFile, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = uploadedFile;
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType());
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CertidaoAtividadeBCE salvarRetornado(CertidaoAtividadeBCE  certidaoAtividadeBCE) {
        return em.merge(certidaoAtividadeBCE);
    }

    @Override
    public CertidaoAtividadeBCE recuperar(Object id) {
        CertidaoAtividadeBCE certidao = em.find(CertidaoAtividadeBCE.class, id);
        certidao.getCertidaoArquivos().size();
        return certidao;
    }

    public List<CertidaoAtividadeBCE> recuperarCertidaoAtividadeBCE(CadastroEconomico cadastroEconomico) {
        String sql = " SELECT * FROM CERTIDAOATIVIDADEBCE " +
                "where CADASTROECONOMICO_ID = :id";
        Query q = em.createNativeQuery(sql, CertidaoAtividadeBCE.class);
        q.setParameter("id", cadastroEconomico.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

}
