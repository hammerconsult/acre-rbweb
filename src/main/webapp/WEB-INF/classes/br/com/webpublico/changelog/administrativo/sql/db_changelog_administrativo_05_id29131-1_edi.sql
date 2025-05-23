delete from statuslicitacao
where licitacao_id = 871185735
and tiposituacaolicitacao = 'HOMOLOGADA';

delete from statuslicitacao
where licitacao_id = 871185735
and tiposituacaolicitacao = 'ADJUDICADA';

delete from StatusFornecedorLicitacao
where id in (
select sf.id from StatusFornecedorLicitacao sf
inner join licitacaofornecedor forn on forn.id = sf.licitacaofornecedor_id
where forn.licitacao_id = 871185735
and sf.tiposituacao = 'HOMOLOGADA');

delete from StatusFornecedorLicitacao
where id in (
select sf.id from StatusFornecedorLicitacao sf
inner join licitacaofornecedor forn on forn.id = sf.licitacaofornecedor_id
where forn.licitacao_id = 871185735
and sf.tiposituacao = 'ADJUDICADA');

update itemprocessodecompra set situacaoitemprocessodecompra = 'AGUARDANDO'
where id in (
select item.id from Licitacao lic
 inner join processodecompra pro on pro.id = lic.processodecompra_id
 inner join loteprocessodecompra lote on lote.processodecompra_id = pro.id
 inner join itemprocessodecompra item on item.loteprocessodecompra_id = lote.id
 where lic.id = 871185735)
