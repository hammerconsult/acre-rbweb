package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.Legislacao;
import br.com.webpublico.nfse.domain.TipoLegislacao;
import br.com.webpublico.nfse.domain.dtos.LegislacaoDTO;
import br.com.webpublico.nfse.domain.dtos.TipoLegislacaoDTO;
import br.com.webpublico.nfse.domain.dtos.TipoLegislacaoLegislacaoDTO;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.util.NfseValidacaoUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Stateless
public class TipoLegislacaoFacade extends AbstractFacade<TipoLegislacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LegislacaoFacade legislacaoFacade;

    public TipoLegislacaoFacade() {
        super(TipoLegislacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasOrdemRegistrada(TipoLegislacao tipoLegislacao) {
        String sql = " select 1 from tipolegislacao " +
            " where tipolegislacao.ordemexibicao =:ordemexibicao ";
        if (tipoLegislacao.getId() != null) {
            sql += " and tipolegislacao.id <> :id ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("ordemexibicao", tipoLegislacao.getOrdemExibicao());
        if (tipoLegislacao.getId() != null) {
            q.setParameter("id", tipoLegislacao.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public void salvar(TipoLegislacao tipoLegislacao) {
        validarTipoLegislacao(tipoLegislacao);
        if (tipoLegislacao.getId() == null) {
            salvarNovo(tipoLegislacao);
        } else {
            salvar(tipoLegislacao);
        }
    }

    private void validarTipoLegislacao(TipoLegislacao tipoLegislacao) throws NfseValidacaoException {
        NfseValidacaoUtil.validarCampos(tipoLegislacao);
        NfseValidacaoException ve = new NfseValidacaoException();
        if (hasOrdemRegistrada(tipoLegislacao)) {
            ve.adicionarMensagem("A ordem de exibição digitada já está registrada.");
        }
        ve.lancarException();
    }

    public List<TipoLegislacao> getAll() {
        return lista();
    }

    public TipoLegislacao getById(Long id) {
        return recuperar(id);
    }

    public void remover(TipoLegislacao tipoLegislacao) {
        remover(tipoLegislacao);
    }

    public List<TipoLegislacaoLegislacaoDTO> buscarTipoLegislacoComLegiscoes() {
        List<TipoLegislacaoLegislacaoDTO> toReturn = Lists.newArrayList();
        Map<TipoLegislacaoDTO, List<LegislacaoDTO>> map = Maps.newHashMap();
        List<LegislacaoDTO> legislacoes = Legislacao.toListLegislacaoDTO(legislacaoFacade.buscarLegislacaoParaExibicao());
        if (!legislacoes.isEmpty()) {
            for (LegislacaoDTO legislacaoDTO : legislacoes) {
                if (!map.containsKey(legislacaoDTO.getTipoLegislacaoDTO())) {
                    map.put(legislacaoDTO.getTipoLegislacaoDTO(), new ArrayList());
                }
                map.get(legislacaoDTO.getTipoLegislacaoDTO()).add(legislacaoDTO);
            }
        }
        for (TipoLegislacaoDTO tipoLegislacaoDTO : map.keySet()) {
            TipoLegislacaoLegislacaoDTO item = new TipoLegislacaoLegislacaoDTO();
            item.setTipoLegislacaoDTO(tipoLegislacaoDTO);
            item.setLegislacoesDTO(map.get(tipoLegislacaoDTO));
            Collections.sort(item.getLegislacoesDTO(), new Comparator<LegislacaoDTO>() {
                @Override
                public int compare(LegislacaoDTO o1, LegislacaoDTO o2) {
                    return o1.getOrdemExibicao().compareTo(o2.getOrdemExibicao());
                }
            });
            toReturn.add(item);
        }
        Collections.sort(toReturn, new Comparator<TipoLegislacaoLegislacaoDTO>() {
            @Override
            public int compare(TipoLegislacaoLegislacaoDTO o1, TipoLegislacaoLegislacaoDTO o2) {
                return o1.getTipoLegislacaoDTO().getOrdemExibicao().compareTo(o2.getTipoLegislacaoDTO().getOrdemExibicao());
            }
        });
        return toReturn;
    }
}

