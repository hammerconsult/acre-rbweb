insert into templateemail (id, tipo, assunto, conteudo)
select hibernate_sequence.nextval, 'REJEICAO_SOLICITACAO_ITBI_ONLINE',
       'Rejeição da Solicitação de ITBI Online',
       'Link: ${LINK}<div><br/></div><div>Motivo da Rejeição: ${MOTIVO}</div>'
from dual
where not exists (select 1 from templateemail where tipo = 'REJEICAO_SOLICITACAO_ITBI_ONLINE');

insert into templateemail (id, tipo, assunto, conteudo)
select hibernate_sequence.nextval, 'AVALIACAO_FISCAL_SOLICITACAO_ITBI_ONLINE',
       'Avaliação do Fiscal sobre a Solicitação de ITBI Online',
       'Link: ${LINK}<div><br/></div><div>Parecer do Fiscal: ${MOTIVO}</div>'
from dual
where not exists (select 1 from templateemail where tipo = 'AVALIACAO_FISCAL_SOLICITACAO_ITBI_ONLINE');
