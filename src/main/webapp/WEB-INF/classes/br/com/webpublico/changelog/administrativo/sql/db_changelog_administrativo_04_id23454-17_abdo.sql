update lotereducaovalorbem lr set lr.quantidadedepreciacao = (
select count(b.id)
   from bem b
  inner join eventobem eb  on eb.bem_id = b.id
  left join estadobem inicial  on inicial.id   = eb.estadoinicial_id
  inner join estadobem resultante  on resultante.id   = eb.estadoresultante_id
  inner join grupobem gb on gb.id = resultante.grupobem_id
  inner join tiporeducao tr on tr.grupobem_id = gb.id
  where eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = b.id and eb2.dataoperacao <= lr.data)
    and lr.data between tr.iniciovigencia and coalesce(tr.fimvigencia, lr.data)
    and eb.situacaoeventobem not in ('BAIXADO', 'ESTORNADO')
    and resultante.detentoraorcamentaria_id = lr.unidadeorcamentaria_id
    and resultante.estadobem <> 'BAIXADO'
    and gb.tipobem = lr.tipobem
    and tr.tiporeducaovalorbem = 'DEPRECIACAO'
    and not exists(select 1 from itemsolicitacaoalienacao solicitacao
                     inner join eventobem ev on ev.id = solicitacao.id
                     left join itemaprovacaoalienacao aprovacao on aprovacao.itemsolicitacaoalienacao_id = solicitacao.id
                     left join eventobem evef on evef.id = aprovacao.id
                   where evef.dataoperacao <= lr.data
                     and ev.situacaoeventobem <> 'FINALIZADO'
                     and (aprovacao.id is null or evef.situacaoeventobem <> 'FINALIZADO')
                     and ev.bem_id = b.id
                     and not exists(select 1
                                       from leilaoalienacaolotebem leilao
                                      inner join eventobem ev on ev.id = leilao.id
                                    where leilao.itemsolicitacaoalienacao_id = solicitacao.id
                                      and ev.situacaoeventobem in ('FINALIZADO'))))
where lr.tiporeducao != 'DEPRECIACAO';

update lotereducaovalorbem lr set lr.valordepreciacao = (
select sum(resultante.valororiginal)
   from bem b
  inner join eventobem eb  on eb.bem_id = b.id
  left join estadobem inicial  on inicial.id   = eb.estadoinicial_id
  inner join estadobem resultante  on resultante.id   = eb.estadoresultante_id
  inner join grupobem gb on gb.id = resultante.grupobem_id
  inner join tiporeducao tr on tr.grupobem_id = gb.id
  where eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = b.id and eb2.dataoperacao <= lr.data)
    and lr.data between tr.iniciovigencia and coalesce(tr.fimvigencia, lr.data)
    and eb.situacaoeventobem not in ('BAIXADO', 'ESTORNADO')
    and resultante.detentoraorcamentaria_id = lr.unidadeorcamentaria_id
    and resultante.estadobem <> 'BAIXADO'
    and gb.tipobem = lr.tipobem
    and tr.tiporeducaovalorbem = 'DEPRECIACAO'
    and not exists(select 1 from itemsolicitacaoalienacao solicitacao
                     inner join eventobem ev on ev.id = solicitacao.id
                     left join itemaprovacaoalienacao aprovacao on aprovacao.itemsolicitacaoalienacao_id = solicitacao.id
                     left join eventobem evef on evef.id = aprovacao.id
                   where evef.dataoperacao <= lr.data
                     and ev.situacaoeventobem <> 'FINALIZADO'
                     and (aprovacao.id is null or evef.situacaoeventobem <> 'FINALIZADO')
                     and ev.bem_id = b.id
                     and not exists(select 1
                                       from leilaoalienacaolotebem leilao
                                      inner join eventobem ev on ev.id = leilao.id
                                    where leilao.itemsolicitacaoalienacao_id = solicitacao.id
                                      and ev.situacaoeventobem in ('FINALIZADO'))))
where lr.tiporeducao != 'DEPRECIACAO';

update lotereducaovalorbem lr set lr.quantidadeexaustao = (
select count(b.id)
   from bem b
  inner join eventobem eb  on eb.bem_id = b.id
  left join estadobem inicial  on inicial.id   = eb.estadoinicial_id
  inner join estadobem resultante  on resultante.id   = eb.estadoresultante_id
  inner join grupobem gb on gb.id = resultante.grupobem_id
  inner join tiporeducao tr on tr.grupobem_id = gb.id
  where eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = b.id and eb2.dataoperacao <= lr.data)
    and lr.data between tr.iniciovigencia and coalesce(tr.fimvigencia, lr.data)
    and eb.situacaoeventobem not in ('BAIXADO', 'ESTORNADO')
    and resultante.detentoraorcamentaria_id = lr.unidadeorcamentaria_id
    and resultante.estadobem <> 'BAIXADO'
    and gb.tipobem = lr.tipobem
    and tr.tiporeducaovalorbem = 'EXAUSTAO'
    and not exists(select 1 from itemsolicitacaoalienacao solicitacao
                     inner join eventobem ev on ev.id = solicitacao.id
                     left join itemaprovacaoalienacao aprovacao on aprovacao.itemsolicitacaoalienacao_id = solicitacao.id
                     left join eventobem evef on evef.id = aprovacao.id
                   where evef.dataoperacao <= lr.data
                     and ev.situacaoeventobem <> 'FINALIZADO'
                     and (aprovacao.id is null or evef.situacaoeventobem <> 'FINALIZADO')
                     and ev.bem_id = b.id
                     and not exists(select 1
                                       from leilaoalienacaolotebem leilao
                                      inner join eventobem ev on ev.id = leilao.id
                                    where leilao.itemsolicitacaoalienacao_id = solicitacao.id
                                      and ev.situacaoeventobem in ('FINALIZADO'))))
where lr.tiporeducao != 'EXAUSTAO';

update lotereducaovalorbem lr set lr.valorexaustao = (
select sum(resultante.valororiginal)
   from bem b
  inner join eventobem eb  on eb.bem_id = b.id
  left join estadobem inicial  on inicial.id   = eb.estadoinicial_id
  inner join estadobem resultante  on resultante.id   = eb.estadoresultante_id
  inner join grupobem gb on gb.id = resultante.grupobem_id
  inner join tiporeducao tr on tr.grupobem_id = gb.id
  where eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = b.id and eb2.dataoperacao <= lr.data)
    and lr.data between tr.iniciovigencia and coalesce(tr.fimvigencia, lr.data)
    and eb.situacaoeventobem not in ('BAIXADO', 'ESTORNADO')
    and resultante.detentoraorcamentaria_id = lr.unidadeorcamentaria_id
    and resultante.estadobem <> 'BAIXADO'
    and gb.tipobem = lr.tipobem
    and tr.tiporeducaovalorbem = 'EXAUSTAO'
    and not exists(select 1 from itemsolicitacaoalienacao solicitacao
                     inner join eventobem ev on ev.id = solicitacao.id
                     left join itemaprovacaoalienacao aprovacao on aprovacao.itemsolicitacaoalienacao_id = solicitacao.id
                     left join eventobem evef on evef.id = aprovacao.id
                   where evef.dataoperacao <= lr.data
                     and ev.situacaoeventobem <> 'FINALIZADO'
                     and (aprovacao.id is null or evef.situacaoeventobem <> 'FINALIZADO')
                     and ev.bem_id = b.id
                     and not exists(select 1
                                       from leilaoalienacaolotebem leilao
                                      inner join eventobem ev on ev.id = leilao.id
                                    where leilao.itemsolicitacaoalienacao_id = solicitacao.id
                                      and ev.situacaoeventobem in ('FINALIZADO'))))
where lr.tiporeducao != 'EXAUSTAO';


update lotereducaovalorbem lr set lr.quantidadeamortizacao = (
select count(b.id)
   from bem b
  inner join eventobem eb  on eb.bem_id = b.id
  left join estadobem inicial  on inicial.id   = eb.estadoinicial_id
  inner join estadobem resultante  on resultante.id   = eb.estadoresultante_id
  inner join grupobem gb on gb.id = resultante.grupobem_id
  inner join tiporeducao tr on tr.grupobem_id = gb.id
  where eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = b.id and eb2.dataoperacao <= lr.data)
    and lr.data between tr.iniciovigencia and coalesce(tr.fimvigencia, lr.data)
    and eb.situacaoeventobem not in ('BAIXADO', 'ESTORNADO')
    and resultante.detentoraorcamentaria_id = lr.unidadeorcamentaria_id
    and resultante.estadobem <> 'BAIXADO'
    and gb.tipobem = lr.tipobem
    and tr.tiporeducaovalorbem = 'AMORTIZACAO'
    and not exists(select 1 from itemsolicitacaoalienacao solicitacao
                     inner join eventobem ev on ev.id = solicitacao.id
                     left join itemaprovacaoalienacao aprovacao on aprovacao.itemsolicitacaoalienacao_id = solicitacao.id
                     left join eventobem evef on evef.id = aprovacao.id
                   where evef.dataoperacao <= lr.data
                     and ev.situacaoeventobem <> 'FINALIZADO'
                     and (aprovacao.id is null or evef.situacaoeventobem <> 'FINALIZADO')
                     and ev.bem_id = b.id
                     and not exists(select 1
                                       from leilaoalienacaolotebem leilao
                                      inner join eventobem ev on ev.id = leilao.id
                                    where leilao.itemsolicitacaoalienacao_id = solicitacao.id
                                      and ev.situacaoeventobem in ('FINALIZADO'))))
where lr.tiporeducao != 'AMORTIZACAO';

update lotereducaovalorbem lr set lr.valoramortizacao = (
select sum(resultante.valororiginal)
   from bem b
  inner join eventobem eb  on eb.bem_id = b.id
  left join estadobem inicial  on inicial.id   = eb.estadoinicial_id
  inner join estadobem resultante  on resultante.id   = eb.estadoresultante_id
  inner join grupobem gb on gb.id = resultante.grupobem_id
  inner join tiporeducao tr on tr.grupobem_id = gb.id
  where eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = b.id and eb2.dataoperacao <= lr.data)
    and lr.data between tr.iniciovigencia and coalesce(tr.fimvigencia, lr.data)
    and eb.situacaoeventobem not in ('BAIXADO', 'ESTORNADO')
    and resultante.detentoraorcamentaria_id = lr.unidadeorcamentaria_id
    and resultante.estadobem <> 'BAIXADO'
    and gb.tipobem = lr.tipobem
    and tr.tiporeducaovalorbem = 'AMORTIZACAO'
    and not exists(select 1 from itemsolicitacaoalienacao solicitacao
                     inner join eventobem ev on ev.id = solicitacao.id
                     left join itemaprovacaoalienacao aprovacao on aprovacao.itemsolicitacaoalienacao_id = solicitacao.id
                     left join eventobem evef on evef.id = aprovacao.id
                   where evef.dataoperacao <= lr.data
                     and ev.situacaoeventobem <> 'FINALIZADO'
                     and (aprovacao.id is null or evef.situacaoeventobem <> 'FINALIZADO')
                     and ev.bem_id = b.id
                     and not exists(select 1
                                       from leilaoalienacaolotebem leilao
                                      inner join eventobem ev on ev.id = leilao.id
                                    where leilao.itemsolicitacaoalienacao_id = solicitacao.id
                                      and ev.situacaoeventobem in ('FINALIZADO'))))
where lr.tiporeducao != 'AMORTIZACAO';


update lotereducaovalorbem lr set lr.quantidadedepreciacao = (
select count(rvb.id)
       from reducaovalorbem rvb
    where rvb.lotereducaovalorbem_id = lr.id)
where lr.tiporeducao = 'DEPRECIACAO';

update lotereducaovalorbem lr set lr.valordepreciacao = (
    select sum(es.valororiginal)
       from reducaovalorbem rvb
     inner join eventobem eb on eb.id = rvb.id
     inner join estadobem es on es.id = eb.estadoresultante_id
    where rvb.lotereducaovalorbem_id = lr.id)
where lr.tiporeducao = 'DEPRECIACAO';

update lotereducaovalorbem lr set lr.quantidadeexaustao = (
select count(rvb.id)
       from reducaovalorbem rvb
    where rvb.lotereducaovalorbem_id = lr.id)
where lr.tiporeducao = 'EXAUSTAO';

update lotereducaovalorbem lr set lr.valorexaustao = (
    select sum(es.valororiginal)
       from reducaovalorbem rvb
     inner join eventobem eb on eb.id = rvb.id
     inner join estadobem es on es.id = eb.estadoresultante_id
    where rvb.lotereducaovalorbem_id = lr.id)
where lr.tiporeducao = 'EXAUSTAO';

update lotereducaovalorbem lr set lr.quantidadeamortizacao = (
select count(rvb.id)
       from reducaovalorbem rvb
    where rvb.lotereducaovalorbem_id = lr.id)
where lr.tiporeducao = 'AMORTIZACAO';

update lotereducaovalorbem lr set lr.valoramortizacao = (
    select sum(es.valororiginal)
       from reducaovalorbem rvb
     inner join eventobem eb on eb.id = rvb.id
     inner join estadobem es on es.id = eb.estadoresultante_id
    where rvb.lotereducaovalorbem_id = lr.id)
where lr.tiporeducao = 'AMORTIZACAO';
