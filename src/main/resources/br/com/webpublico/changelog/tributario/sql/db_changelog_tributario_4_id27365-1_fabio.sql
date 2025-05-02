update autoinfracaofiscal aut set
aut.VALORAUTOINFRACAO = coalesce((select sum(lanc.issdevido) from LancamentoContabilFiscal lanc inner join REGISTROLANCAMENTOCONTABIL reg on reg.ID = lanc.REGISTROLANCAMENTOCONTABIL_ID where reg.ID = aut.REGISTRO_ID and reg.SITUACAO in ('AUTO_INFRACAO','PAGO','AGUARDANDO','DIVIDA_ATIVA')),0),
aut.JUROS = coalesce((select sum(lanc.juros) from LancamentoContabilFiscal lanc inner join REGISTROLANCAMENTOCONTABIL reg on reg.ID = lanc.REGISTROLANCAMENTOCONTABIL_ID where reg.ID = aut.REGISTRO_ID and reg.SITUACAO in ('AUTO_INFRACAO','PAGO','AGUARDANDO','DIVIDA_ATIVA')),0),
aut.MULTA = coalesce((select sum(lanc.multa) from LancamentoContabilFiscal lanc inner join REGISTROLANCAMENTOCONTABIL reg on reg.ID = lanc.REGISTROLANCAMENTOCONTABIL_ID where reg.ID = aut.REGISTRO_ID and reg.SITUACAO in ('AUTO_INFRACAO','PAGO','AGUARDANDO','DIVIDA_ATIVA')),0),
aut.correcao = coalesce((select sum(lanc.correcao) from lancamentocontabilfiscal lanc inner join registrolancamentocontabil reg on reg.id = lanc.registrolancamentocontabil_id where reg.id = aut.registro_id and reg.situacao in ('AUTO_INFRACAO','PAGO','AGUARDANDO','DIVIDA_ATIVA')),0)
where aut.id = 194810656
