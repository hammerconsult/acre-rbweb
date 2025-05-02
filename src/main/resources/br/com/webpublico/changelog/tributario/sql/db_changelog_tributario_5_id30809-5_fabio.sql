update acaofiscal acao set acao.SITUACAOACAOFISCAL = 'EXECUTANDO'
where acao.SITUACAOACAOFISCAL = 'DESIGNADO'
  and exists (select id from DoctoAcaoFiscal where ACAOFISCAL_ID = acao.id)
