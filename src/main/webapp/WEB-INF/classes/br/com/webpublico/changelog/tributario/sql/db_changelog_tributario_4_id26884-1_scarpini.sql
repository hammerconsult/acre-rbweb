update NOTIFICACAO noti
set criadoem = (select min(rev.DATAHORA)
                from REVISAOAUDITORIA rev
                         inner join NOTIFICACAO_AUD aud on aud.REV = rev.id where aud.id = noti.id);
